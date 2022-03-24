package com.example.smartbuy01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.smartbuy01.adapters.ProductListAdapter;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.Shop;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {
    //to pass the clicked index and show product info
    public static final String CLICKED_PRODUCT_INDEX="com.example.smartbuy01.CLICKED_PRODUCT_INDEX";//???

    private RecyclerView recyclerView;
    private ProductListAdapter itemAdapter;
    private RecyclerView.LayoutManager itemLayoutManager;//???

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shop.fetchProducts();
        setContentView(R.layout.activity_products);
        setUpToolbar();
        setUpSearch();
        setUpRecyclerView();
    }

    private void setUpToolbar(){
        Toolbar toolbar= findViewById(R.id.productsToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Browse Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
    private void filter(String searchEntry){
        ArrayList<Product> filteredList = new ArrayList<>();

        for (Product myProduct : Shop.getProductsInShop()) {
            if (myProduct.getProductName().toLowerCase().contains(searchEntry.toLowerCase())) {//הופך האותיות לאותיות קטנות
                filteredList.add(myProduct);
            }
        }

        itemAdapter.filterList(filteredList);
    }
    public void setUpRecyclerView() {
        recyclerView=findViewById(R.id.productsRecyclerView);
        recyclerView.setHasFixedSize(true);//???
        itemLayoutManager=new LinearLayoutManager(this);//כל ITEM נמצא בשורה נפרדה
        itemAdapter=new ProductListAdapter(Shop.getProductsInShop());//מכנסים פרטי כל מוצר לשורה

        recyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new ProductListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemClicked(position);
            }
        });
    }

    private void itemClicked(int position){
        Intent productInfoIntent=new Intent(getApplicationContext(),ProductInfoActivity.class);
        productInfoIntent.putExtra(CLICKED_PRODUCT_INDEX,position);
        startActivity(productInfoIntent);
        //PUTEXTRA העברת נתונים מדף לדף
    }
}
