package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.control.adapters.HorizontalListAdapter;
import com.example.smartbuy01.model.ByteArray;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.ProductList;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ShopActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //attr
    private DrawerLayout drawerMenu;
    private Toolbar toolbar;

    //for precaution only
    private static boolean firstOpening=true;

    //sezrch button
    private ImageButton productsButton;

    //suggestion list attr
    private LinearLayout suggestionsLayout;
    public static List<Product> myProductSuggestions;
    private RecyclerView suggestionsRecyclerView;
    private HorizontalListAdapter suggestionAdapter;
    private RecyclerView.LayoutManager itemLayoutManager;

    //best selling list attr
    private List<Product> myBestSelling;
    private RecyclerView bestSellingRecyclerView;
    private HorizontalListAdapter bestSellingAdapter;


    //server api
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onStop(){
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        if(!firstOpening){
            if(User.isSignedIn()) {
                //if there already is a suggestion list
                if(myProductSuggestions==null || myProductSuggestions.isEmpty())
                    fetchSuggestions();
            }else
                //if user isnt signed in, the suggestion panel is hidden
                suggestionsLayout.setVisibility(View.GONE);
        }
        if(myBestSelling==null || myBestSelling.isEmpty())
            fetchBestSellers();
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        //check connection
        Shop.connection();
        //the drawer layout contains the shop layout
        setContentView(R.layout.drawer_layout);
        if(firstOpening) {
            //if this is the first time opening the app, open sign in activity.
            startActivityForResult(new Intent(getApplicationContext(), SignInActivity.class), 1);
        }

        setUpToolbar();
        setUpMenu();//navigation menu
        setUpButtons();

        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        suggestionsLayout=findViewById(R.id.suggestionsLinearLayout);

        //to refresh
        onRefresh();
    }//end of onCreate

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        //ceck connection before transferring
        Shop.connection();
        if(!Shop.connectionStatus){
            //if there is no connection, the error activity is started
            drawerMenu.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(),ConnectionErrorActivity.class));
        }else {
            //switch case for side menu
            switch (menuItem.getItemId()) {
                case R.id.navCartItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    break;
                case R.id.navFavouritesItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), FavouritesActivity.class));
                    break;
                case R.id.navOrdersItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), OrdersActivity.class));
                    break;
                case R.id.navMapItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                    break;
                case R.id.navFridgeItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), FridgeActivity.class));
                    break;
                case R.id.navAccountItem:
                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    drawerMenu.closeDrawer(GravityCompat.START);
                    break;
            }//end of switch case
        }//end of else
        return true;
    }
    @Override
    public void onBackPressed(){
        //if back button is pressed, it will close the menu instead of finishing the activity
        if(drawerMenu.isDrawerOpen(GravityCompat.START))
            drawerMenu.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }
    private void onRefresh(){
        //pull down t refresh page
        final SwipeRefreshLayout pullToRefreshShop = findViewById(R.id.pullToRefreshShop);
        pullToRefreshShop.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(User.isSignedIn()) {
                    fetchSuggestions();
                }else
                    suggestionsLayout.setVisibility(View.GONE);
                fetchBestSellers();
                pullToRefreshShop.setRefreshing(false);
            }
        });
    }
    private void setUpToolbar(){
        //no back button
        toolbar= findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
    }
    private void setUpMenu(){
        //setting up side menu with toolbar button
        drawerMenu=findViewById(R.id.drawerLayout);

        NavigationView navigationView=findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerMenu,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);

        drawerMenu.addDrawerListener(toggle);

        toggle.syncState();
    }
    private void setUpButtons(){
        productsButton=findViewById(R.id.productsButton);

        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productsIntent=new Intent(getApplicationContext(),ProductsActivity.class);
                startActivity(productsIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        //on startActivityForResult finish
        if(requestCode==1){
            firstOpening=false;
            if(User.isSignedIn()) {
                suggestionsLayout.setVisibility(View.VISIBLE);
                fetchSuggestions();
            }
        }
    }
    private void fetchSuggestions(){
        compositeDisposable.add(myAPI.getSuggestions(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //convert products
                        Gson gson = new Gson();
                        ProductList productList = gson.fromJson(s, ProductList.class);
                        myProductSuggestions=productList.getProducts();
                        //if there are no suggestions, nothing is shown in their place
                        if(!(myProductSuggestions.isEmpty())){
                            setUpSuggestionsRecyclerView();
                            getProductImages(myProductSuggestions,"Suggestions");
                        }
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    public void setUpSuggestionsRecyclerView() {

        suggestionsRecyclerView=findViewById(R.id.suggestionRecyclerView);
        suggestionsRecyclerView.setHasFixedSize(true);
        //horizontal list
        itemLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        suggestionAdapter=new HorizontalListAdapter(myProductSuggestions);

        suggestionsRecyclerView.setLayoutManager(itemLayoutManager);
        suggestionsRecyclerView.setAdapter(suggestionAdapter);

        suggestionAdapter.setOnItemClickListener(new HorizontalListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemClicked(myProductSuggestions.get(position).getProductId());
            }
        });
        //show cardview after it is setup
        suggestionsLayout.setVisibility(View.VISIBLE);
    }
    private void fetchBestSellers(){
        compositeDisposable.add(myAPI.getBestSellers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //convert best sellers
                        Gson gson = new Gson();
                        ProductList productList = gson.fromJson(s, ProductList.class);
                        myBestSelling=productList.getProducts();
                        //organize them into list
                        setUpBestSellingRecyclerView();
                        getProductImages(myBestSelling,"Best Selling");
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    public void setUpBestSellingRecyclerView() {

        bestSellingRecyclerView=findViewById(R.id.bestSellingRecyclerView);
        bestSellingRecyclerView.setHasFixedSize(true);
        //orizontal cardview
        itemLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        bestSellingAdapter=new HorizontalListAdapter(myBestSelling);

        bestSellingRecyclerView.setLayoutManager(itemLayoutManager);
        bestSellingRecyclerView.setAdapter(bestSellingAdapter);

        bestSellingAdapter.setOnItemClickListener(new HorizontalListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemClicked(myBestSelling.get(position).getProductId());
            }
        });
    }
    private void itemClicked(int productId){
        //start the product info activity and pass it the products id
        Intent productInfoIntent=new Intent(getApplicationContext(),ProductInfoActivity.class);
        productInfoIntent.putExtra("Product_ID",productId);
        startActivity(productInfoIntent);
    }
    private void getProductImages(List<Product> myProducts, final String listToLoad) {
        //start a thread that fetches every image separately
        //in this activity we also pass the name of the list the product belongs to
        for (int i=0;i<myProducts.size();i++) {
            GetProductImagesAsyncTask task=new GetProductImagesAsyncTask(this);
            task.execute(new MyImageTaskParams(myProducts.get(i),i,listToLoad));
        }
    }
    private static class MyImageTaskParams {
        //bundling the parameters because asynctask can only take 1 at a time
        Product p;
        int index;
        String listToLoad;
        MyImageTaskParams(Product p,int index,String listToLoad) {
            this.p=p;
            this.index=index;
            this.listToLoad=listToLoad;
        }
    }
    private static class GetProductImagesAsyncTask extends AsyncTask<MyImageTaskParams,Void,Void> {
        //image thread class
        private WeakReference<ShopActivity> weakReference;
        private Product p;
        private int index;
        private String listNameToLoad;
        GetProductImagesAsyncTask(ShopActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<ShopActivity>(activity);
        }

        protected void onPreExecute(MyImageTaskParams params) {
            //unload params
            p=params.p;
            index=params.index;
            listNameToLoad=params.listToLoad;
        }

        @Override
        protected Void doInBackground(MyImageTaskParams... myImageTaskParams) {
            onPreExecute(myImageTaskParams[0]);
            final ShopActivity activity=weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            activity.compositeDisposable.add(activity.myAPI.getProductImage(p.getProductId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            //convert image to gjo
                            Gson gson = new Gson();
                            ByteArray byteArray = gson.fromJson(s, ByteArray.class);
                            if(byteArray.getByteArray().length>0) {
                                p.setImage(byteArray.getByteArray());
                            }else
                                p.setImage(null);
                            //notify adapter that item changed in this position
                            if (listNameToLoad.equals("Best Selling")) {
                                activity.bestSellingAdapter.notifyItemChanged(index);
                            } else if (listNameToLoad.equals("Suggestions")) {
                                activity.suggestionAdapter.notifyItemChanged(index);
                            }
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }
    }
}//end of shopactivity
