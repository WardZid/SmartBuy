package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.control.adapters.CartListAdapter;
import com.example.smartbuy01.model.ByteArray;
import com.example.smartbuy01.model.Cart;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.ProductList;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.google.gson.Gson;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.ref.WeakReference;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CartActivity extends AppCompatActivity {

    //server api
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    //list adapter
    private CartListAdapter itemAdapter;

    //clear the composite disposable when finishing the activity
    //to save memory and prevrnt memory leakage
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
        setContentView(R.layout.please_wait);//loading
        //check connection first
        if(!Shop.connectionStatus) startActivity(new Intent(getApplicationContext(), ConnectionErrorActivity.class));
        else if(User.isSignedIn()) signedIn();
        else notSignedIn();
    }
    private void notSignedIn(){
        //tells user to sign in to view his cart
        setContentView(R.layout.please_sign_in);

        Toolbar toolbar= findViewById(R.id.pleaseToolbar);
        setSupportActionBar(toolbar);
        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button signInButton=(Button)findViewById(R.id.pleaseSignInButton);
        TextView signInTextView=(TextView) findViewById(R.id.signInToContinueTextView);
        signInTextView.setText(R.string.signInCart);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SignInActivity.class),2);//result being the user signed in
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        //when an activity for result is started
        if(requestCode==2 && User.isSignedIn()){
            //if the user signed in, we will refresh this activity
            finish();
            startActivity(getIntent());
        }else if(requestCode==3){
            finish();
            startActivity(getIntent());
        }
    }
    private void signedIn(){

        //init server api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);


        fetchCart();
        //i had to call setUpRecyclerView() in the accept method because fetching the cart was too slow in the background
        //took forever to realize that it was just working slow in the background
        //setUpRecyclerView();
    }//end of signedIn()
    private void setUpToolbar(){
        Toolbar toolbar= findViewById(R.id.cartToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    @Override
    public boolean onSupportNavigateUp() {
        //override the toolbar back button
        onBackPressed();
        return true;
    }
    private void fetchCart(){

        compositeDisposable.add(myAPI.getCartProducts(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        User.setMyCart(s);
                        if(!User.getMyCart().isEmpty()) fetchProductsInCart();
                        else {
                            //if cart is empty, empty cart layout is shown
                            setContentView(R.layout.empty_cart_layout);

                            //toolbar
                            Toolbar toolbar= findViewById(R.id.emptyCartToolbar);
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setTitle("Your Cart");
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);
                        }
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }//end of fetchCart()
    private void fetchProductsInCart(){
        //fetching the products in cart
        compositeDisposable.add(myAPI.getProductsInCart(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Gson gson = new Gson();
                        ProductList productList = gson.fromJson(s, ProductList.class);
                        //match every product with its correspondent cartlist object
                        for(int i=0;i<User.getMyCart().size();i++){
                            for(int k=0;k<productList.getProducts().size();k++){
                                if(User.getMyCart().get(i).getProductId()==productList.getProducts().get(k).getProductId()) {
                                    User.getMyCart().get(i).setProductInCart(productList.getProducts().get(k));
                                    break;
                                }
                            }
                        }
                        setUpRecyclerView();
                        getImages();
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    private void setUpRecyclerView() {
        setContentView(R.layout.activity_cart);

        setUpToolbar();
        //setup the list view
        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        itemAdapter=new CartListAdapter(User.getMyCart());

        recyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.setAdapter(itemAdapter);

        //set up clicklisteners
        itemAdapter.setOnItemClickListener(new CartListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                itemClicked(position);
            }

            @Override
            public void onItemChange(int position) {
                itemChanged(position);
            }

            @Override
            public void onDeleteClick(int position) {
                itemRemoved(position);
            }
        });
        //checkout layer
        setUpCheckOut();
    }
    private void setUpCheckOut(){
        //get views
        TextView totalItemsTextView = findViewById(R.id.totalAmountOfITemsTextView);
        TextView totalCostTextView=findViewById(R.id.totalCostTextView);
        Button checkOutButton=findViewById(R.id.checkOutButton);
        TextView tooManyItems=findViewById(R.id.unavailableItemsWarningTextView);

        //fill views
        totalItemsTextView.setText((""+getTotalAmountInCart()));
        totalCostTextView.setText(("$ "+getTotalPriceInCart()));

        //set onclick
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCart();
                if(cartAmountValid())
                    checkOut();
                else{
                    finish();
                    startActivity(getIntent());
                }
            }
        });

        //check cart validity
        if(User.getMyCart().isEmpty()){
            checkOutButton.setEnabled(false);
        } else if(!cartAmountValid()){
            checkOutButton.setEnabled(false);
            tooManyItems.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.outOfStock));
        }else{
            checkOutButton.setEnabled(true);
            tooManyItems.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
        }
    }
    private void checkOut(){
        //pop up dialog to make sure customer wans to proceed
        new MaterialStyledDialog.Builder(this)
                .setTitle("Proceed?")
                .setDescription("Your total is: " + getTotalPriceInCart())
                .setCustomView(null)
                .setIcon(R.drawable.ic_shopping_basket)
                .setNegativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Proceed")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //server post request
                            compositeDisposable.add(myAPI.sendOrder(User.getUserId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            Toast.makeText(getApplicationContext(),""+s,Toast.LENGTH_SHORT).show();
                                            finish();
                                            startActivity(getIntent());
                                        }
                                    })//end of subscribeOn
                            );//end of add
                    }//end of onClick
                }).show();//end of builder
    }
    private int getTotalAmountInCart() {
        //gets total amount of items in cart
        int countOfItems = 0;
        for (Cart cartItem : User.getMyCart()) {
            countOfItems += cartItem.getAmount();
        }
        return countOfItems;
    }
    private int getTotalPriceInCart() {
        //gets the sum total price of everything in the cart
        int totalPrice = 0;
        for (int ind = 0; ind < User.getMyCart().size(); ind++) {
            totalPrice += User.getMyCart().get(ind).getAmount() * User.getMyCart().get(ind).getProductInCart().getPrice();
        }
        return totalPrice;
    }
    public static boolean cartAmountValid() {
        //if any of the item in the cart have an amount less than what's in the User, i.e. not enough or out of stock
        for (int ind = 0; ind < User.getMyCart().size(); ind++) {
            if (User.getMyCart().get(ind).getAmount() > User.getMyCart().get(ind).getProductInCart().getAmount())
                return false;
        }
        return true;
    }
    private void itemClicked(int position){
        //start the info activity and send it the product id of the item we want to show
        Intent productInfoIntent=new Intent(getApplicationContext(), ProductInfoActivity.class);
        int product_id=User.getMyCart().get(position).getProductInCart().getProductId();
        productInfoIntent.putExtra("Product_ID",product_id);
        startActivityForResult(productInfoIntent,3);
    }
    private void itemChanged(final int position){
        //change amount
        final View enterAmountView= LayoutInflater.from(this).inflate(R.layout.choose_amount_layout,null);

        //pop up dialog to ask for new amount
        new MaterialStyledDialog.Builder(this)
                .setTitle("Choose Amount")
                .setDescription("*Choosing 0 will remove item.")
                .setCustomView(enterAmountView)
                .setIcon(R.drawable.ic_add_to_cart)
                .setNegativeText("Cancel")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveText("Confirm")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        //get value from edit name
                        MaterialEditText addAmountEditText =enterAmountView.findViewById(R.id.addAmountEditText);
                        //make sure something was added
                        if(!addAmountEditText.getText().toString().isEmpty())
                            //http post request
                            compositeDisposable.add(myAPI.alterCart(User.getUserId(),User.getMyCart().get(position).getProductId(),Integer.parseInt(addAmountEditText.getText().toString()))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            Toast.makeText(getApplicationContext(),""+s,Toast.LENGTH_SHORT).show();
                                            changeItem(position);
                                        }
                                    })//end of subscribeOn
                            );//end of add
                    }//end of onClick
                }).show();//end of builder
    }
    private void itemRemoved(final int position){
        //http request to remove item
        compositeDisposable.add(myAPI.deleteFromCart(User.getUserId(),User.getMyCart().get(position).getProductId())
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
    private void changeItem(int position){
        //refresh page after item change
        fetchCart();
    }
    private void removeItem(int position){
        User.getMyCart().remove(position);//removes from User.myCart list
        itemAdapter.notifyItemRemoved(position);//removes from recyclerView
        if(User.getMyCart().isEmpty())
            fetchCart();
        setUpCheckOut();
    }
    private void getImages(){
        //start a new thread to fetch the image for each product
        //easier on the main thread
        for (int i=0;i<User.getMyCart().size();i++) {
            GetProductImagesAsyncTask task=new GetProductImagesAsyncTask(this);
            task.execute(new MyImageTaskParams(User.getMyCart().get(i).getProductInCart(),i));
        }
    }
    private static class MyImageTaskParams {
        //class used to bundle more than one parameter to send to the asynctask class
        Product p;
        int index;
        MyImageTaskParams(Product p,int index) {
            this.p=p;
            this.index=index;
        }
    }
    private static class GetProductImagesAsyncTask extends AsyncTask<MyImageTaskParams,Void,Void> {
        private WeakReference<CartActivity> weakReference;
        private Product p;
        private int index;
        GetProductImagesAsyncTask(CartActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<CartActivity>(activity);
        }

        protected void onPreExecute(MyImageTaskParams params) {
            //unload params
            p=params.p;
            index=params.index;
        }

        @Override
        protected Void doInBackground(MyImageTaskParams... myImageTaskParams) {
            onPreExecute(myImageTaskParams[0]);
            final CartActivity activity=weakReference.get();
            //make sure activity still running
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            activity.compositeDisposable.add(activity.myAPI.getProductImage(p.getProductId())//concat blank to turn into string
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            //make sure activity still running
                            if (!(activity == null || activity.isFinishing())){
                                Gson gson = new Gson();
                                ByteArray byteArray = gson.fromJson(s, ByteArray.class);
                                if(byteArray.getByteArray().length>0) {
                                    //if the byte array is empty, there is no image
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
