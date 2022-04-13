package com.example.smartbuy01.control.Activities;

import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.ProductList;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MapActivity extends AppCompatActivity{
    //list of products to choose from
    private List<Product> myProducts;
    //textview array to show product location
    private List<TextView> myTVList=new ArrayList<>();
    //search bar
    AutoCompleteTextView searchACTextView;
    //server api
    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    //clear compositedisposable to prevent memory leakage
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.please_wait);
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        //starting thread that fetches products
        GetProductsAsyncTask task = new GetProductsAsyncTask(this);
        task.execute();

    }
    private void setUpMap(){
        setContentView(R.layout.activity_map);
        setUpToolbar();

        setUpTextViews();

        setUpSearch();

        //textwatcher that gives suggestions
        searchACTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                findProduct(s.toString().trim().toLowerCase());
            }
        });
    }
    private void setUpToolbar(){
        //toolbar with back button
        Toolbar toolbar= findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        //override back button
        onBackPressed();
        return true;
    }
    private void setUpTextViews(){
        //assign shelves and areas to textview array
        myTVList.add((TextView)findViewById(R.id.A0));
        myTVList.add((TextView)findViewById(R.id.A1));
        myTVList.add((TextView)findViewById(R.id.A2));
        myTVList.add((TextView)findViewById(R.id.A3));
        myTVList.add((TextView)findViewById(R.id.A4));
        myTVList.add((TextView)findViewById(R.id.B0));
        myTVList.add((TextView)findViewById(R.id.B1));
        myTVList.add((TextView)findViewById(R.id.B2));
        myTVList.add((TextView)findViewById(R.id.B3));
        myTVList.add((TextView)findViewById(R.id.B4));
        myTVList.add((TextView)findViewById(R.id.C0));
        myTVList.add((TextView)findViewById(R.id.C1));
        myTVList.add((TextView)findViewById(R.id.C2));
        myTVList.add((TextView)findViewById(R.id.C3));
        myTVList.add((TextView)findViewById(R.id.C4));
        myTVList.add((TextView)findViewById(R.id.D0));
        myTVList.add((TextView)findViewById(R.id.D1));
        myTVList.add((TextView)findViewById(R.id.D2));
        myTVList.add((TextView)findViewById(R.id.D3));
        myTVList.add((TextView)findViewById(R.id.D4));
        myTVList.add((TextView)findViewById(R.id.E0));
        myTVList.add((TextView)findViewById(R.id.E1));
        myTVList.add((TextView)findViewById(R.id.E2));
        myTVList.add((TextView)findViewById(R.id.E3));
        myTVList.add((TextView)findViewById(R.id.E4));
        myTVList.add((TextView)findViewById(R.id.F0));
        myTVList.add((TextView)findViewById(R.id.F1));
        myTVList.add((TextView)findViewById(R.id.F2));
        myTVList.add((TextView)findViewById(R.id.F3));
        myTVList.add((TextView)findViewById(R.id.F4));
        myTVList.add((TextView)findViewById(R.id.G0));
        myTVList.add((TextView)findViewById(R.id.H0));
        myTVList.add((TextView)findViewById(R.id.I0));
        myTVList.add((TextView)findViewById(R.id.J0));
        myTVList.add((TextView)findViewById(R.id.K0));
        myTVList.add((TextView)findViewById(R.id.L0));
    }
    private void setUpSearch(){
        searchACTextView=findViewById(R.id.mapSearchAutoCompleteTextView);

        List<String> productNames=getProductNames();
        //create an adapter for a list for suggestoins
        ArrayAdapter<String> myACAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,productNames);

        searchACTextView.setAdapter(myACAdapter);
    }
    private List<String> getProductNames(){
        List<String> nameList=new ArrayList<>();
        //fill list with product names
        for(Product myProduct : myProducts){
            nameList.add(myProduct.getProductName());
        }
        return nameList;
    }
    private void findProduct(String input){
        boolean productExists=false;
        for( Product myProduct : myProducts){
            if(myProduct.getProductName().toLowerCase().equals(input)){//finds product referenced
                productExists=true;
                for(TextView tv : myTVList){
                    if((myProduct.getArea()+myProduct.getRow()).equals(tv.getContentDescription())) {//finds matching textview
                        if(myProduct.isAvailable())//changes color (according to availability) of tv to show location
                            tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.available));
                        else
                            tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.outOfStock));
                    }else
                        tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));//if not tv we are looking for, make it transparent
                }
            }
        }
        if(!productExists)//if the input is non-existent, all textviews turn transparent
            for(TextView tv : myTVList){
                tv.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.transparent));
            }
    }
    private static class GetProductsAsyncTask extends AsyncTask<Void,Void,Void> {
        private WeakReference<MapActivity> weakReference;
        //asyntask thread to fetch productss
        GetProductsAsyncTask(MapActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<MapActivity>(activity);
        }
        @Override
        protected Void doInBackground(Void... voids) {
            final MapActivity activity=weakReference.get();

            if (activity == null || activity.isFinishing()) {
                return null;
            }
            activity.compositeDisposable.add(activity.myAPI.getAllProducts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Gson gson = new Gson();
                            ProductList products= gson.fromJson(s, ProductList.class);
                            activity.myProducts=products.getProducts();
                            activity.setUpMap();
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }
    }
}
