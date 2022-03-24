package com.example.smartbuy01;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.ProductList;
import com.example.smartbuy01.model.ProductType;
import com.example.smartbuy01.model.Shop;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class MapActivity extends AppCompatActivity{

    private List<TextView> myTVList=new ArrayList<>();
    AutoCompleteTextView searchACTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setUpToolbar();

        setUpTextViews();

        setUpSearch();

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
        Toolbar toolbar= findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Map");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    private void setUpTextViews(){
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

        ArrayAdapter<String> myACAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,productNames);

        searchACTextView.setAdapter(myACAdapter);
    }
    private List<String> getProductNames(){
        List<String> nameList=new ArrayList<>();
        for(Product myProduct : Shop.getProductsInShop()){
            nameList.add(myProduct.getProductName());
        }
        return nameList;
    }

    private void findProduct(String input){
        boolean productExists=false;
        for( Product myProduct : Shop.getProductsInShop()){
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
}
