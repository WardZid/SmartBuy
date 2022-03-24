package com.example.smartbuy01;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class ProductInfoActivity extends AppCompatActivity {

    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private Product myProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_info);

        Intent intent = getIntent();
        int indexOfItemPassed=intent.getIntExtra(ProductsActivity.CLICKED_PRODUCT_INDEX,-1);
        if(indexOfItemPassed < 0) //will never happen
            finish();

        myProduct= Shop.getProductsInShop().get(indexOfItemPassed);//get product we need to show info

        setUpProductInfo();
        setUpButtons();

        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);
    }
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
    private void setUpProductInfo(){
        ImageView imageView=findViewById(R.id.productInfoImageImageView);
        TextView nameView= findViewById(R.id.productInfoNameTextView);
        TextView weightView=findViewById(R.id.productInfoWeightTextView);
        TextView priceView=findViewById(R.id.productInfoPriceTextView);
        TextView availabilityView=findViewById(R.id.productInfoAvailabilityTextView);

        imageView.setImageResource(myProduct.getProductImage());//מסוג INT
        nameView.setText(myProduct.getProductName());//מסוג STRING
        weightView.setText((myProduct.getWeight()+"kg (per unit)"));
        priceView.setText(("$ "+myProduct.getPrice()));

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
        closeProductInfo.bringToFront();//לציג כפתור היוצא מהדף מעל תמונה
        closeProductInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ImageButton addToCartButton=findViewById(R.id.productInfoAddToCartButton);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }
    private void addToCart(){
        /*
        * if not signed in, the sign in activity pops up
        * else, a dialog for picking amount pops up
        * */
        if(!User.isSignedIn()){
            Intent signInIntent=new Intent(getApplicationContext(),SignInActivity.class);
            startActivity(signInIntent);
        }else {
            final View enterAmountView = LayoutInflater.from(this).inflate(R.layout.cart_choose_amount_layout, null);
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
                            if (!addAmountEditText.getText().toString().isEmpty())//
                                //isEmpty אם ריק
                                // אם לא ריק
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
}
