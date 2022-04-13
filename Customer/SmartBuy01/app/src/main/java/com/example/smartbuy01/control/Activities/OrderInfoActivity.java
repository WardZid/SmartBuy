package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.control.adapters.HorizontalListAdapter;
import com.example.smartbuy01.model.ByteArray;
import com.example.smartbuy01.model.Order;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.ProductList;
import com.example.smartbuy01.model.User;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class OrderInfoActivity extends AppCompatActivity {
    //order to show
    private Order myOrder;
    //recycler list view atr
    private List<Product> myProducts;
    private RecyclerView productsInOrderRecyclerView;
    private HorizontalListAdapter productsInOrderAdapter;
    private RecyclerView.LayoutManager productsInOrderLayoutManager;

    //server api
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    //prevent memory leakage
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
        setContentView(R.layout.please_wait);
        //init server api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        //get order id sent
        Intent intent = getIntent();
        int orderId=intent.getExtras().getInt("ORDER_ID");

        if(orderId < 0) //will never happen, but precaution
            finish();
        //fetch said order from te orders list
        for(Order o : User.getMyOrders()){
            if(o.getOrderId()==orderId)
                myOrder= o;
        }
        if(myOrder==null) finish();
        fetchProductsInOrder();
    }
    private void fetchProductsInOrder(){
        //fetch product summaries
        //here we fetch all product ids and the amounts
        compositeDisposable.add(myAPI.getProductsInOrderSummary(myOrder.getOrderId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        myOrder.setProductInOrder(s);
                        fetchProducts();
                    }
                })//end of subscribeOn
        );//end of add
    }

    private void fetchProducts(){
        compositeDisposable.add(myAPI.getProductsInOrder(myOrder.getOrderId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //convert products json into gjo
                        Gson gson = new Gson();
                        ProductList productList = gson.fromJson(s, ProductList.class);
                        myProducts=productList.getProducts();
                        //set up rest of activity
                        setUpInfo();
                        setUpProductsInOrderRecyclerView();
                        getImages();
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    private void setUpInfo(){
        setContentView(R.layout.activity_order_info);

        //set up layout
        TextView orderIdTextView=findViewById(R.id.orderIdTextView);
        TextView orderDateTextView=findViewById(R.id.orderDateTextView);
        TextView orderStatusTextView=findViewById(R.id.orderStatusTextView);

        orderIdTextView.setText(("Order ID: "+myOrder.getOrderId()));
        orderDateTextView.setText(myOrder.getDateOfOrder());

        //set up order status
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

        //exit button
        ImageButton closeProductInfo=findViewById(R.id.orderInfoCloseButton);
        closeProductInfo.bringToFront();
        closeProductInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void setUpProductsInOrderRecyclerView(){
        //products recycler view
        productsInOrderRecyclerView=findViewById(R.id.orderInfoRecyclerView);
        productsInOrderRecyclerView.setHasFixedSize(true);
        productsInOrderLayoutManager=new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        productsInOrderAdapter=new HorizontalListAdapter(myProducts);

        productsInOrderRecyclerView.setLayoutManager(productsInOrderLayoutManager);
        productsInOrderRecyclerView.setAdapter(productsInOrderAdapter);

        //on item click listener
        productsInOrderAdapter.setOnItemClickListener(new HorizontalListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemClicked(myProducts.get(position).getProductId());
            }
        });
    }
    private void itemClicked(int productId){
        //product info
        Intent productInfoIntent=new Intent(getApplicationContext(), ProductInfoActivity.class);
        productInfoIntent.putExtra("Product_ID",productId);
        startActivity(productInfoIntent);
    }
    private void getImages(){
        //get image for each product on a different thread
        for (int i=0;i<myProducts.size();i++) {
            GetProductImagesAsyncTask task=new GetProductImagesAsyncTask(this);
            task.execute(new MyImageTaskParams(myProducts.get(i),i));
        }
    }
    private static class MyImageTaskParams {
        //param bundle for thread
        Product p;
        int index;
        MyImageTaskParams(Product p,int index) {
            this.p=p;
            this.index=index;
        }
    }
    private static class GetProductImagesAsyncTask extends AsyncTask<MyImageTaskParams,Void,Void> {
        private WeakReference<OrderInfoActivity> weakReference;
        private Product p;
        private int index;
        GetProductImagesAsyncTask(OrderInfoActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<OrderInfoActivity>(activity);
        }

        protected void onPreExecute(MyImageTaskParams params) {
            //unload params
            p=params.p;
            index=params.index;
        }

        @Override
        protected Void doInBackground(MyImageTaskParams... myImageTaskParams) {
            onPreExecute(myImageTaskParams[0]);
            final OrderInfoActivity activity=weakReference.get();
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
                                activity.productsInOrderAdapter.notifyItemChanged(index);
                            }
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }
    }
}
