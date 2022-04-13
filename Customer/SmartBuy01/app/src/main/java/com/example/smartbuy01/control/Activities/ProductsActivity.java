package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;

import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.control.adapters.CategoryAdapter;
import com.example.smartbuy01.control.adapters.ProductListAdapter;
import com.example.smartbuy01.model.ByteArray;
import com.example.smartbuy01.model.Category;
import com.example.smartbuy01.model.CategoryList;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.ProductList;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ProductsActivity extends AppCompatActivity {

    //recycler view for categories
    private RecyclerView categoriesRecyclerView;
    private CategoryAdapter categoriesItemAdapter;
    private RecyclerView.LayoutManager categoriesItemLayoutManager;

    //recyclerview for products
    private LinearLayout productsLinearLayout;
    private RecyclerView productsRecyclerView;
    private ProductListAdapter productItemAdapter;
    private RecyclerView.LayoutManager ProductItemLayoutManager;
    private List<Product> fullList;//full list of products
    private List<Product> shownList;//list of products filtered by search


    //sever api
    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onBackPressed(){
        //if back button is pressed, it will close the menu instead of finishing the activity
        if(productsLinearLayout.getVisibility()==View.VISIBLE){

            getSupportActionBar().setTitle("Browse Products");
            productsLinearLayout.setVisibility(View.GONE);
            categoriesRecyclerView.setVisibility(View.VISIBLE);
            fullList.clear();
            shownList.clear();
        }
        else
            super.onBackPressed();
    }
    //clear compositedisposable to prevent emory leaks
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
    public boolean onSupportNavigateUp() {
        //override toolbar back button
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.please_wait);
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        setUpCategories();
    }
    private void setUpToolbar(){
        Toolbar toolbar= findViewById(R.id.productsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Browse Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    private void setUpCategories(){
        //fetch categories on a seperate thread
        //making the rest of what happens on this activity work on a seperate thread than the min thread
        GetCategoriesAsyncTask task = new GetCategoriesAsyncTask(this);
        task.execute();
    }
    private void setUpCategoryRecyclerView(final List<Category> categories){
        setContentView(R.layout.activity_products);

        setUpToolbar();
        productsLinearLayout=findViewById(R.id.productsLinearLayout);

        //use list of fetchedd categories to fill list
        categoriesRecyclerView=findViewById(R.id.categoryRecyclerView);
        categoriesRecyclerView.setHasFixedSize(true);
        categoriesItemLayoutManager=new LinearLayoutManager(this);
        categoriesItemAdapter=new CategoryAdapter(categories);

        categoriesRecyclerView.setLayoutManager(categoriesItemLayoutManager);
        categoriesRecyclerView.setAdapter(categoriesItemAdapter);

        //on category click listener
        categoriesItemAdapter.setOnItemClickListener(new CategoryAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showProducts(categories.get(position).toString());
            }
        });
    }
    private void showProducts(String category){
        //use category to fetch products
        GetProductsAsyncTask task = new GetProductsAsyncTask(this);
        task.execute(category.replace(' ','_'));
        getSupportActionBar().setTitle(category);
    }
    private void setUpProducts(){
        setUpProductRecyclerView();
        setUpSearch();
        getImages();

        //hide the category list and show products
        categoriesRecyclerView.setVisibility(View.GONE);
        productsLinearLayout.setVisibility(View.VISIBLE);
    }
    private void setUpProductRecyclerView() {
        productsRecyclerView=findViewById(R.id.productsRecyclerView);
        productsRecyclerView.setHasFixedSize(true);
        ProductItemLayoutManager=new LinearLayoutManager(this);
        productItemAdapter=new ProductListAdapter(shownList);

        productsRecyclerView.setLayoutManager(ProductItemLayoutManager);
        productsRecyclerView.setAdapter(productItemAdapter);

        productItemAdapter.setOnItemClickListener(new ProductListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                productItemClicked(position);
            }
        });
    }
    private void setUpSearch(){
        MaterialEditText searcheditText=findViewById(R.id.productSearchEditText);
        searcheditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }
    private void getImages(){
        //start separate thread for each image fetch
        for (int i=0;i<shownList.size();i++) {
            GetProductImagesAsyncTask task=new GetProductImagesAsyncTask(this);
            task.execute(new MyImageTaskParams(shownList.get(i),i));
        }
    }
    private void filter(String searchEntry){
        //the list is filtered according to the search query
        shownList=new ArrayList<>();
        for (Product myProduct : fullList) {
            if (myProduct.getProductName().toLowerCase().contains(searchEntry.toLowerCase())) {
                shownList.add(myProduct);
            }
        }
        System.out.println(shownList.toString());
        productItemAdapter.filterList(shownList);
    }
    private void productItemClicked(int position){
        //get position of product in the productsInShop array because the products info uses the productsinshop array to retrieve info
        int pid=shownList.get(position).getProductId();

        Intent productInfoIntent=new Intent(getApplicationContext(),ProductInfoActivity.class);
        productInfoIntent.putExtra("Product_ID",pid);
        startActivity(productInfoIntent);
    }
    private static class GetCategoriesAsyncTask extends AsyncTask<Void,Void,Void> {
        private WeakReference<ProductsActivity> weakReference;

        GetCategoriesAsyncTask(ProductsActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<ProductsActivity>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final ProductsActivity activity=weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            //category list request
            activity.compositeDisposable.add(activity.myAPI.getAllCategories()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            //convert categories frm json
                            Gson gson = new Gson();
                            CategoryList categories= gson.fromJson(s,CategoryList.class);
                            activity.setUpCategoryRecyclerView(categories.getCategories());
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }//end of do in background
    }
    private static class GetProductsAsyncTask extends AsyncTask<String,Void,Void> {
        //products fetch asynctask
        private WeakReference<ProductsActivity> weakReference;

        GetProductsAsyncTask(ProductsActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<ProductsActivity>(activity);
        }

        @Override
        protected Void doInBackground(String... strings) {
            final ProductsActivity activity=weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            activity.compositeDisposable.add(activity.myAPI.getProductsByType(strings[0])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Gson gson = new Gson();
                            ProductList productList= gson.fromJson(s,ProductList.class);
                            System.out.println(s);
                            //prevent excess memory usage
                            if(activity.fullList!=null){
                                activity.fullList.clear();
                                activity.shownList.clear();
                            }
                            activity.fullList=productList.getProducts();
                            activity.shownList=activity.fullList;
                            activity.setUpProducts();
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }
    }
    private static class MyImageTaskParams {
        //grouping the product and its index in the list to be able to pass them together to the GetProductImagesAsyncTask
        Product p;
        int index;

        MyImageTaskParams(Product p,int index) {
            this.p=p;
            this.index=index;
        }
    }
    private static class GetProductImagesAsyncTask extends AsyncTask<MyImageTaskParams,Void,Void> {
        private WeakReference<ProductsActivity> weakReference;
        private Product p;
        private int index;
        GetProductImagesAsyncTask(ProductsActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<ProductsActivity>(activity);
        }
        protected void onPreExecute(MyImageTaskParams params) {
            //unpack params
            p=params.p;
            index=params.index;
        }

        @Override
        protected Void doInBackground(MyImageTaskParams... myImageTaskParams) {
            onPreExecute(myImageTaskParams[0]);
            final ProductsActivity activity=weakReference.get();
            //if activity is finished
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            //image post request
            activity.compositeDisposable.add(activity.myAPI.getProductImage(p.getProductId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            if (activity.productsLinearLayout.getVisibility()==View.VISIBLE) {
                                Gson gson = new Gson();
                                ByteArray byteArray = gson.fromJson(s, ByteArray.class);
                                if(byteArray.getByteArray().length>0) {
                                    p.setImage(byteArray.getByteArray());
                                }else
                                    p.setImage(null);
                                //notify adapter that the item has changed in this index
                                activity.productItemAdapter.notifyItemChanged(index);
                            }//end of if
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }//end of doinbackground
    }//end of GetProductImagesAsyncTask
}
