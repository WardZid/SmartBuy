package com.example.smartbuy01;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.example.smartbuy01.model.Order;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class OrderInfoActivity extends AppCompatActivity {

    private Order myOrder;
    TextView productsInOrderTextView;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        Intent intent = getIntent();
        int indexOfItemPassed=intent.getIntExtra(OrdersActivity.CLICKED_ORDER_INDEX,-1);
        if(indexOfItemPassed < 0) //will never happen
            finish();

        myOrder= Shop.getMyOrders().get(indexOfItemPassed);

        fetchProductsInOrder();

        setUpInfo();

        ImageButton closeProductInfo=findViewById(R.id.orderInfoCloseButton);
        closeProductInfo.bringToFront();
        closeProductInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void fetchProductsInOrder(){
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        compositeDisposable.add(myAPI.getProductsInOrder(myOrder.getOrderId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        myOrder.setProductInOrder(s);
                        productsInOrderTextView.setText(buildProductString());
                    }
                })//end of subscribeOn
        );//end of add
    }
    private void setUpInfo(){
        TextView orderIdTextView=findViewById(R.id.orderIdTextView);
        TextView orderDateTextView=findViewById(R.id.orderDateTextView);
        TextView orderStatusTextView=findViewById(R.id.orderStatusTextView);

        productsInOrderTextView=findViewById(R.id.productsInOrderTextView);

        orderIdTextView.setText(("Order ID: "+myOrder.getOrderId()));
        orderDateTextView.setText(myOrder.getDateOfOrder());
        if(myOrder.getOrderStatus().equals("COMPLETED")){
            orderStatusTextView.setText(myOrder.getOrderStatus());
            orderStatusTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.available));
        }else if(myOrder.getOrderStatus().equals("IN_PROGRESS")){
            orderStatusTextView.setText("In Progress");
            orderStatusTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
        }else if(myOrder.getOrderStatus().equals("WAITING")){
            orderStatusTextView.setText("In Queue");
            orderStatusTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.outOfStock));
        }else{
            orderStatusTextView.setText(myOrder.getOrderStatus());
            orderStatusTextView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.outOfStock));
        }


    }
    private String buildProductString(){
        String p="Products: \n";
        for(Order.ProductInOrderItem product : myOrder.getProductsInOrder()){
            p+="\n\nAmount: "+product.getAmountInOrder()+"\t| Product: "+Shop.getProductById(product.getProductId()).getProductName();
        }
        return p;
    }
}
