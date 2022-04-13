package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.example.smartbuy01.control.adapters.FridgeListAdapter;
import com.example.smartbuy01.model.ByteArray;
import com.example.smartbuy01.model.FridgeList;
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

public class FridgeActivity extends AppCompatActivity {
    //server api
    private INodeJS myAPI;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    //list adapter
    private FridgeListAdapter itemAdapter;

    //prevent memory leakage
    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
    @Override
    public boolean onSupportNavigateUp() {
        //override the toolbar back button
        onBackPressed();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading
        setContentView(R.layout.please_wait);
        //check connection first
        if (!Shop.connectionStatus)
            startActivity(new Intent(getApplicationContext(), ConnectionErrorActivity.class));
        else if (User.isSignedIn()) signedIn();
        else notSignedIn();
    }
    private void notSignedIn() {
        //tells user to sign in to view his orders
        setContentView(R.layout.please_sign_in);

        Toolbar toolbar = findViewById(R.id.pleaseToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//???
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button signInButton = (Button) findViewById(R.id.pleaseSignInButton);
        TextView signInTextView = (TextView) findViewById(R.id.signInToContinueTextView);
        signInTextView.setText(R.string.signInOrders);//כדי שיוצג ה-TEXT

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SignInActivity.class), 2);//result being the user signed in
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        //on activityresult called finish
        if (requestCode == 2 && User.isSignedIn()) {
            //if the user signed in, we will refresh this activity
            finish();
            startActivity(getIntent());
        }
    }
    private void signedIn() {
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        fetchFridge();
    }//end of signedIn()
    private void fetchFridge(){
        //post request
        compositeDisposable.add(myAPI.getFridge(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        User.setMyFridge(s);
                        if(!User.getMyFridge().isEmpty()) fetchProductsInFridge();
                        else {
                            setContentView(R.layout.empty_fridge_layout);

                            //toolbar
                            Toolbar toolbar= findViewById(R.id.emptyFridgeToolbar);
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setTitle("Your Fridge");
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);
                        }
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }//end of fetchfridge()
    private void fetchProductsInFridge(){
        setContentView(R.layout.activity_fridge);
        //fetching the products in fridge
        compositeDisposable.add(myAPI.getProductsInFridge(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //convert json to gjo
                        Gson gson = new Gson();
                        ProductList productList = gson.fromJson(s, ProductList.class);
                        //match the correct product with the matching fridge item
                        for(int i=0;i<User.getMyFridge().size();i++){
                            for(int k=0;k<productList.getProducts().size();k++){
                                if(User.getMyFridge().get(i).getProductId()==productList.getProducts().get(k).getProductId()) {
                                    User.getMyFridge().get(i).setProductInFridge(productList.getProducts().get(k));
                                    break;
                                }
                            }
                        }
                        //set up view
                        setUpRecyclerView();
                        setUpToolbar();
                        getImages();
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    private void setUpToolbar(){
        //toolbar
        Toolbar toolbar= findViewById(R.id.fridgeToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Fridge");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    private void setUpRecyclerView() {
        setContentView(R.layout.activity_fridge);
        //set up card list view
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.FridgeRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        itemAdapter = new FridgeListAdapter(User.getMyFridge());

        recyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.setAdapter(itemAdapter);

        //set up listeners
        itemAdapter.setOnItemClickListener(new FridgeListAdapter.onItemClickListener() {
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
    }
    private void itemClicked(int position){
        //get id and start info activity with it
        Intent productInfoIntent=new Intent(getApplicationContext(), ProductInfoActivity.class);
        int product_id=User.getMyFridge().get(position).getProductInFridge().getProductId();
        productInfoIntent.putExtra("Product_ID",product_id);
        startActivityForResult(productInfoIntent,3);
    }
    private void itemChanged(final int position){
        final View enterAmountView= LayoutInflater.from(this).inflate(R.layout.choose_amount_layout,null);

        //ask for new amount using pop up
        new MaterialStyledDialog.Builder(this)
                .setTitle("Choose Amount")
                .setDescription("*Choosing 0 will remove item.")
                .setCustomView(enterAmountView)
                .setIcon(R.drawable.ic_fridge)
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
                            compositeDisposable.add(myAPI.alterFridge(User.getUserId(),User.getMyFridge().get(position).getProductId(),Integer.parseInt(addAmountEditText.getText().toString()))
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
        //remove item from db
        compositeDisposable.add(myAPI.deleteFromFridge(User.getUserId(),User.getMyFridge().get(position).getProductId())
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
        itemAdapter.notifyItemChanged(position);
    }
    private void removeItem(int position){
        //did not implement this on onResume because the server is too slow (PPTP)
        User.getMyFridge().remove(position);//removes from User.myFridge list
        itemAdapter.notifyItemRemoved(position);//removes from recyclerView
        if(User.getMyFridge().isEmpty())
            fetchFridge();
    }
    private void getImages(){
        //seperate thread for every image fetch
        for(int i=0;i<User.getMyFridge().size();i++){
            GetProductImagesAsyncTask task=new GetProductImagesAsyncTask(this);
            task.execute(new MyImageTaskParams(User.getMyFridge().get(i).getProductInFridge(),i));
        }
    }
    private static class MyImageTaskParams {
        //bundling params for asynctask
        Product p;
        int index;
        MyImageTaskParams(Product p,int index) {
            this.p=p;
            this.index=index;
        }
    }
    //inner thread class
    private static class GetProductImagesAsyncTask extends AsyncTask<MyImageTaskParams,Void,Void> {
        private WeakReference<FridgeActivity> weakReference;
        private Product p;
        private int index;
        GetProductImagesAsyncTask(FridgeActivity activity){
            //weak reference to prevent memory leaks
            weakReference=new WeakReference<FridgeActivity>(activity);
        }

        protected void onPreExecute(MyImageTaskParams params) {
            //unloading params
            p=params.p;
            index=params.index;
        }

        @Override
        protected Void doInBackground(MyImageTaskParams... myImageTaskParams) {
            onPreExecute(myImageTaskParams[0]);
            final FridgeActivity activity=weakReference.get();
            //make sure activity is still running
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
                                //convert to byte array
                                Gson gson = new Gson();
                                ByteArray byteArray = gson.fromJson(s, ByteArray.class);
                                if(byteArray.getByteArray().length>0) {
                                    p.setImage(byteArray.getByteArray());
                                }else
                                    p.setImage(null);
                                //update list after image set
                                activity.itemAdapter.notifyItemChanged(index);
                            }
                        }//end of accept
                    })//end of subscribe
            );//end of compositeDisposable.add
            return null;
        }
    }

}
