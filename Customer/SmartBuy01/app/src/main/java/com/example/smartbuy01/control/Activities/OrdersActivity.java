package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.control.adapters.OrderListAdapter;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class OrdersActivity extends AppCompatActivity {

    //server api
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private OrderListAdapter itemAdapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        //check connection before doing anything
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
        //after activity for result finishes
        if(requestCode==2 && User.isSignedIn()){
            //if the user signed in, we will refresh this activity
            finish();
            startActivity(getIntent());
        }
    }
    private void signedIn(){
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        fetchOrders();

    }//end of signedIn()
    private void setUpToolbar(){
        Toolbar toolbar= findViewById(R.id.ordersToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        //override back button
        onBackPressed();
        return true;
    }
    private void fetchOrders(){
        //get order info from server
        compositeDisposable.add(myAPI.getOrders(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                        User.setMyOrders(s);
                        if(!User.getMyOrders().isEmpty()) setUpRecyclerView();
                        else {
                            setContentView(R.layout.empty_orders_layout);

                            //toolbar
                            Toolbar toolbar= findViewById(R.id.emptyOrdersToolbar);
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);
                        }
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }//end of fetchOrders()
    private void setUpRecyclerView() {
        setContentView(R.layout.activity_orders);

        setUpToolbar();
        //view
        RecyclerView recyclerView = findViewById(R.id.ordersRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        itemAdapter=new OrderListAdapter(User.getMyOrders());

        recyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.setAdapter(itemAdapter);

        itemAdapter.setOnItemClickListener(new OrderListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemClicked(position);
            }
        });
    }
    private void itemClicked(int position){
        //get order id and start order info activity
        Intent orderInfoIntent=new Intent(getApplicationContext(),OrderInfoActivity.class);
        int orderId=User.getMyOrders().get(position).getOrderId();
        orderInfoIntent.putExtra("ORDER_ID",orderId);
        startActivity(orderInfoIntent);
    }
}
