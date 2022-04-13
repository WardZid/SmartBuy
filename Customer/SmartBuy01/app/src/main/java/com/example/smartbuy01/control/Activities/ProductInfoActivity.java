package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smartbuy01.R;
import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.example.smartbuy01.model.ByteArray;
import com.example.smartbuy01.model.Product;
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

public class ProductInfoActivity extends AppCompatActivity {
    //product for show
    private Product myProduct;

    //server api
    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

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

        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        //get id from intent
        Intent intent = getIntent();
        int productId=intent.getExtras().getInt("Product_ID");
        if(productId < 0) //will never happen
            finish();


        getProduct(productId);
    }

    private void getProduct(int productId){
        //use thread to get product
        GetProductAsyncTask task = new GetProductAsyncTask(this);
        task.execute(productId);
    }

    private void setUpProductInfo(){
        setContentView(R.layout.activity_product_info);

        //set up info
        ImageView imageView=findViewById(R.id.productInfoImageImageView);
        TextView nameView= findViewById(R.id.productInfoNameTextView);
        TextView weightView=findViewById(R.id.productInfoWeightTextView);
        TextView priceView=findViewById(R.id.productInfoPriceTextView);
        TextView availabilityView=findViewById(R.id.productInfoAvailabilityTextView);

        if(myProduct.getProductImage()==null)
            imageView.setImageResource(R.drawable.ic_product_image);
        else
            imageView.setImageBitmap(myProduct.getProductImage());
        nameView.setText(myProduct.getProductName());
        weightView.setText((myProduct.getWeight()+"kg (per unit)"));
        priceView.setText(("$ "+myProduct.getPrice()));

        //availability
        if(myProduct.getAmount()>0){
            availabilityView.setText("Available");
            availabilityView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.available));
        }else{
            availabilityView.setText("Out of stock");
            availabilityView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.outOfStock));
        }
    }
    private  void setUpButtons(){
        ImageButton closeProductInfo=findViewById(R.id.productInfoCloseButton);
        closeProductInfo.bringToFront();
        closeProductInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //add to cart listener
        final ImageButton addToCartButton=findViewById(R.id.productInfoAddToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        //add to favourites
        final ImageButton addToFavourites=findViewById(R.id.productInfoFavouriteButton);
        addToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToFavourites();
            }
        });
    }
    private void addToCart(){
        /*
        * if not signed in, the sign in activity pops up
        * else, a dialog for picking amount pops up
        * */
        if(!User.isSignedIn()){
            Intent signInIntent=new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(signInIntent);
        }else {
            final View enterAmountView = LayoutInflater.from(this).inflate(R.layout.choose_amount_layout, null);
            new MaterialStyledDialog.Builder(this)
                    .setTitle("Choose Amount")
                    .setDescription("How many would you like?")
                    .setCustomView(enterAmountView)
                    .setIcon(R.drawable.ic_add_to_cart)
                    .setNegativeText("Cancel")
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveText("Add To Cart")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            //get value from edit name
                            MaterialEditText addAmountEditText = (MaterialEditText) enterAmountView.findViewById(R.id.addAmountEditText);
                            if (!addAmountEditText.getText().toString().isEmpty())//make sure something was added
                                compositeDisposable.add(myAPI.addToCart(User.getUserId(), myProduct.getProductId(), Integer.parseInt(addAmountEditText.getText().toString()))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<String>() {
                                            @Override
                                            public void accept(String s) throws Exception {
                                                Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
                                            }
                                        })//end of subscribeOn
                                );//end of add
                        }//end of onClick
                    }).show();//end of builder
        }
    }
    private static class GetProductAsyncTask extends AsyncTask<Integer,Void,Void>{
        private WeakReference<ProductInfoActivity> weakReference;

        GetProductAsyncTask(ProductInfoActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<ProductInfoActivity>(activity);
        }
        @Override
        protected Void doInBackground(Integer... integers) {
            final ProductInfoActivity activity=weakReference.get();
            if (activity == null || activity.isFinishing()) {
                return null;
            }
            activity.compositeDisposable.add(activity.myAPI.getProductById(integers[0])
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Gson gson = new Gson();
                            activity.myProduct = gson.fromJson(s, Product.class);
                            activity.getProductImage(activity.myProduct);
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }
    }
    private void getProductImage(final Product myProduct) {
        compositeDisposable.add(myAPI.getProductImage(myProduct.getProductId())//concat blank to turn into string
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Gson gson = new Gson();
                        ByteArray byteArray = gson.fromJson(s, ByteArray.class);
                        myProduct.setImage(byteArray.getByteArray());
                        setUpProductInfo();
                        setUpButtons();
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    private void addToFavourites(){
        compositeDisposable.add(myAPI.addToFavourites(User.getUserId(),myProduct.getProductId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Toast.makeText(getApplicationContext(), "" + s, Toast.LENGTH_SHORT).show();
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
}