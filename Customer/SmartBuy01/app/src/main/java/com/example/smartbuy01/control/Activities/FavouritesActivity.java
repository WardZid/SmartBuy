package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.control.adapters.FavouritesAdapter;
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

public class FavouritesActivity extends AppCompatActivity {

    //server api objs
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    //product array and the list adapter
    private List<Product> myFavouriteProducts;
    private FavouritesAdapter itemAdapter;

    //prevent memory leakage
    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Override
    public boolean onSupportNavigateUp() {
        //override the toolbar back button
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading
        setContentView(R.layout.please_wait);
        //chaeck connecton before starting
        if(!Shop.connectionStatus) startActivity(new Intent(getApplicationContext(),ConnectionErrorActivity.class));
        else if(User.isSignedIn()) signedIn();
        else notSignedIn();
    }
    private void notSignedIn(){
        //tells user to sign in to view his orders
        setContentView(R.layout.please_sign_in);

        Toolbar toolbar= findViewById(R.id.pleaseToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button signInButton=(Button)findViewById(R.id.pleaseSignInButton);
        TextView signInTextView=(TextView) findViewById(R.id.signInToContinueTextView);
        signInTextView.setText(R.string.signInOrders);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SignInActivity.class),2);//result being the user signed in
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        //on activity finished
        if (requestCode == 2 && User.isSignedIn()) {
            //if the user signed in, we will refresh this activity
            finish();
            startActivity(getIntent());
        }
    }
    private void signedIn(){
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        fetchFavourites();
    }//end of signedIn()
    private void fetchFavourites(){
        //post request
        compositeDisposable.add(myAPI.getFavourites(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //convert from json to gjo
                        //gjo=generic java object
                        Gson gson=new Gson();
                        ProductList productList=gson.fromJson(s, ProductList.class);
                        //save list
                        myFavouriteProducts=productList.getProducts();
                        if(!myFavouriteProducts.isEmpty()) setUpFavourites();
                        else {
                            //if list is empty, empty layout is shown
                            setContentView(R.layout.empty_favourites);

                            //toolbar
                            Toolbar toolbar= findViewById(R.id.emptyFavouritesToolbar);
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setTitle("Your Favourites");
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);
                        }
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }//end of fetchFavourites()
    private void setUpFavourites(){
        setContentView(R.layout.activity_favourites);
        //set up list
        setUpRecyclerView();
        //toolbar
        setUpToolbar();
        //images
        getImages();
    }
    private void setUpRecyclerView() {
        //manage views
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.favouritesRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        itemAdapter = new FavouritesAdapter(myFavouriteProducts);

        recyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.setAdapter(itemAdapter);
        //listeners
        itemAdapter.setOnItemClickListener(new FavouritesAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemClicked(position);
            }
            @Override
            public void onDeleteClick(int position) {
                itemRemoved(position);
            }
        });
    }
    private void itemClicked(int position){
        //get id and start the info activity with it
        Intent productInfoIntent=new Intent(getApplicationContext(), ProductInfoActivity.class);
        int product_id=myFavouriteProducts.get(position).getProductId();
        productInfoIntent.putExtra("Product_ID",product_id);
        startActivityForResult(productInfoIntent,3);
    }
    private void itemRemoved(final int position){
        compositeDisposable.add(myAPI.deleteFromFavourites(User.getUserId(),myFavouriteProducts.get(position).getProductId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        removeItem(position);
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    private void removeItem(int position){
        myFavouriteProducts.remove(position);//removes from User.myFridge list
        itemAdapter.notifyItemRemoved(position);//removes from recyclerView
        if(myFavouriteProducts.isEmpty())
            fetchFavourites();
    }
    private void setUpToolbar(){
        //toolbar
        Toolbar toolbar= findViewById(R.id.favouritesToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Favourites");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    private void getImages(){
        //seperate thread for each image
        for(int i=0;i<myFavouriteProducts.size();i++){
            GetProductImagesAsyncTask task=new GetProductImagesAsyncTask(this);
            task.execute(new MyImageTaskParams(myFavouriteProducts.get(i),i));
        }
    }
    private static class MyImageTaskParams {
        //bundling the paras for asynctask
        Product p;
        int index;
        MyImageTaskParams(Product p,int index) {
            this.p=p;
            this.index=index;
        }
    }
    private static class GetProductImagesAsyncTask extends AsyncTask<MyImageTaskParams,Void,Void> {
        private WeakReference<FavouritesActivity> weakReference;
        private Product p;
        private int index;
        GetProductImagesAsyncTask(FavouritesActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<FavouritesActivity>(activity);
        }

        protected void onPreExecute(MyImageTaskParams params) {
            //unload params
            p=params.p;
            index=params.index;
        }

        @Override
        protected Void doInBackground(MyImageTaskParams... myImageTaskParams) {
            onPreExecute(myImageTaskParams[0]);
            final FavouritesActivity activity=weakReference.get();
            //make sure activity is still active
            if (activity == null || activity.isFinishing()) {
                return null;
            }

            activity.compositeDisposable.add(activity.myAPI.getProductImage(p.getProductId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (!(activity == null || activity.isFinishing())){
                                Gson gson = new Gson();
                                ByteArray byteArray = gson.fromJson(s, ByteArray.class);
                                if(byteArray.getByteArray().length>0) {
                                    p.setImage(byteArray.getByteArray());
                                }else
                                    p.setImage(null);
                                activity.itemAdapter.notifyItemChanged(index);
                            }
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }
    }
}
