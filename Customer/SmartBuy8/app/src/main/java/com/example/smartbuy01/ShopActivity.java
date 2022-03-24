package com.example.smartbuy01;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.ProductList;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShopActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerMenu;
    private Toolbar toolbar;
    Button productsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        Shop.connection();

        setContentView(R.layout.drawer_layout);
        Shop.fetchProducts();
        setUpToolbar();
        setUpMenu();
        setUpButtons();
        /*
        * startactivityforresult returns to get result*/
        if(!User.isSignedIn())//?
            startActivityForResult(new Intent(getApplicationContext(),SignInActivity.class),1);

    }//end of onCreate

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Shop.connection();
        if(!Shop.connectionStatus){
            drawerMenu.closeDrawer(GravityCompat.START);
            startActivity(new Intent(getApplicationContext(),ConnectionErrorActivity.class));
        }else {
            switch (menuItem.getItemId()) {
                case R.id.navCartItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), CartActivity.class));
                    break;
                case R.id.navOrdersItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), OrdersActivity.class));
                    break;
                case R.id.navSavedListsItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    break;
                case R.id.navMapItem:
                    drawerMenu.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                    break;
                case R.id.navAccountItem:
                    startActivity(new Intent(getApplicationContext(), AccountActivity.class));
                    drawerMenu.closeDrawer(GravityCompat.START);
                    break;
                case R.id.Fridge:
                    startActivity(new Intent(getApplicationContext(), FridgeActivity.class));
                    drawerMenu.closeDrawer(GravityCompat.START);
                    break;
            }
        }
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

    private void setUpToolbar(){
        toolbar= findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
    }
    private void setUpMenu(){
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
        if(requestCode==1){
//????
        }
    }


}//end of shopactivity
