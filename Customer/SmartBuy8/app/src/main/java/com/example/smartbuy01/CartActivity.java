package com.example.smartbuy01;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.example.smartbuy01.adapters.CartListAdapter;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CartActivity extends AppCompatActivity {
    //control
    public static final String CLICKED_PRODUCT_INDEX="com.example.smartbuy01.CLICKED_PRODUCT_INDEX";

    //server api
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private CartListAdapter itemAdapter;



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
        if(!Shop.connectionStatus) startActivity(new Intent(getApplicationContext(),ConnectionErrorActivity.class));
        else if(User.isSignedIn()) signedIn();
        else notSignedIn();
    }
    private void notSignedIn(){
        //tells user to sign in to view his cart
        setContentView(R.layout.please_sign_in);

        Toolbar toolbar= findViewById(R.id.pleaseToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button signInButton=(Button)findViewById(R.id.pleaseSignInButton);
        TextView signInTextView=(TextView) findViewById(R.id.signInToContinueTextView);
        signInTextView.setText(R.string.signInCart);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(),SignInActivity.class),2);//result being the user signed in
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
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
    private void fetchCart(){
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        compositeDisposable.add(myAPI.getCartProducts(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                        Shop.setMyCart(s);
                        if(!Shop.getMyCart().isEmpty()) setUpRecyclerView();
                        else {
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

    private void setUpRecyclerView() {
        setContentView(R.layout.activity_cart);

        setUpToolbar();
        //view
        RecyclerView recyclerView = findViewById(R.id.cartRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        System.out.println("***********CatrACTIVITY:  "+ Shop.getMyCart().toString());
        itemAdapter=new CartListAdapter(Shop.getMyCart());

        recyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.setAdapter(itemAdapter);

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

        setUpCheckOut();
    }
    private void setUpCheckOut(){
        TextView totalItemsTextView = findViewById(R.id.totalAmountOfITemsTextView);
        TextView totalCostTextView=findViewById(R.id.totalCostTextView);
        Button checkOutButton=findViewById(R.id.checkOutButton);
        TextView tooManyItems=findViewById(R.id.unavailableItemsWarningTextView);

        totalItemsTextView.setText((""+Shop.getTotalAmountInCart()));
        totalCostTextView.setText(("$ "+Shop.getTotalPriceInCart()));

        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCart();
                if(Shop.cartAmountValid())
                    checkOut();
                else{
                    finish();
                    startActivity(getIntent());
                }
            }
        });

        if(Shop.getMyCart().isEmpty()){
            checkOutButton.setEnabled(false);
        } else if(!Shop.cartAmountValid()){
            checkOutButton.setEnabled(false);
            tooManyItems.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.outOfStock));
        }else{
            checkOutButton.setEnabled(true);
            tooManyItems.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.white));
        }
    }

    private void checkOut(){
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        new MaterialStyledDialog.Builder(this)
                .setTitle("Proceed?")
                .setDescription("Your total is: " + Shop.getTotalPriceInCart())
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
    private void itemClicked(int position){
        Intent productInfoIntent=new Intent(getApplicationContext(),ProductInfoActivity.class);
        /*
        * to get proper product
        * when i sent the normal position, the info of that position in the product lis opened and NOT the product in that position in cart list
        * so i made a method in the Shop class that gives me the correct position int the products list that corresponds with the position in the cartlist
        * */
        int positionInProductList=Shop.getPositionOfProduct(position);
        productInfoIntent.putExtra(CLICKED_PRODUCT_INDEX,positionInProductList);
        startActivityForResult(productInfoIntent,3);
    }

    private void itemChanged(final int position){
        final View enterAmountView= LayoutInflater.from(this).inflate(R.layout.cart_choose_amount_layout,null);

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
                        MaterialEditText addAmountEditText = (MaterialEditText)enterAmountView.findViewById(R.id.addAmountEditText);
                        if(!addAmountEditText.getText().toString().isEmpty())//make sure something was added
                            compositeDisposable.add(myAPI.alterCart(User.getUserId(),Shop.getMyCart().get(position).getProductId(),Integer.parseInt(addAmountEditText.getText().toString()))
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

        compositeDisposable.add(myAPI.deleteFromCart(User.getUserId(),Shop.getMyCart().get(position).getProductId())
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
        Shop.fetchProducts();//too slow, find fix!****************************************************

        fetchCart();
        setUpRecyclerView();
    }

    private void removeItem(int position){
        //did not implement this on onResume because the server is too slow (PPTP)
        Shop.getMyCart().remove(position);//removes from Shop.myCart list
        itemAdapter.notifyItemRemoved(position);//removes from recyclerView
        if(Shop.getMyCart().isEmpty())
            fetchCart();
        setUpCheckOut();
    }

}
