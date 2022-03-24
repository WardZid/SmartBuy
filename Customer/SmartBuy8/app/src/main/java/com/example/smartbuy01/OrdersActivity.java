package com.example.smartbuy01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.example.smartbuy01.adapters.OrderListAdapter;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class OrdersActivity extends AppCompatActivity {

    public static final String CLICKED_ORDER_INDEX="com.example.smartbuy01.CLICKED_ORDER_INDEX";
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
        if(!Shop.connectionStatus) startActivity(new Intent(getApplicationContext(),ConnectionErrorActivity.class));
        else if(User.isSignedIn()) signedIn();
        else notSignedIn();
    }
    private void notSignedIn(){
        //tells user to sign in to view his orders
        setContentView(R.layout.please_sign_in);

        Toolbar toolbar= findViewById(R.id.pleaseToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//???
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button signInButton=(Button)findViewById(R.id.pleaseSignInButton);
        TextView signInTextView=(TextView) findViewById(R.id.signInToContinueTextView);
        signInTextView.setText(R.string.signInOrders);//כדי שיוצג ה-TEXT

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(),SignInActivity.class),2);//result being the user signed in
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {//????
        if(requestCode==2 && User.isSignedIn()){//אם המשתמש מתחבר ו-REQUESTCODE שווה ל2 אז נפתח הדף הנוכחי
            //if the user signed in, we will refresh this activity
            finish();
            startActivity(getIntent());
        }
    }
    private void signedIn(){

        fetchOrders();

        //i had to call setUpRecyclerView() in the accept method because fetching the orders was too slow in the background
        //took forever to realize that it was just working slow in the background
        //setUpRecyclerView();
    }//end of signedIn()
    private void setUpToolbar(){
        Toolbar toolbar= findViewById(R.id.ordersToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    private void fetchOrders(){
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        compositeDisposable.add(myAPI.getOrders(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                        Shop.setMyOrders(s);
                        if(!Shop.getMyOrders().isEmpty()) setUpRecyclerView();
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
        itemAdapter=new OrderListAdapter(Shop.getMyOrders());

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
        Intent orderInfoIntent=new Intent(getApplicationContext(),OrderInfoActivity.class);
        orderInfoIntent.putExtra(CLICKED_ORDER_INDEX,position);
        startActivity(orderInfoIntent);
    }
}
