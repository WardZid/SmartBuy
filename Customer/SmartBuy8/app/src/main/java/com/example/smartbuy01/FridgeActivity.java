package com.example.smartbuy01;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.example.smartbuy01.adapters.CartListAdapter;
import com.example.smartbuy01.adapters.FridgeListAdapter;
import com.example.smartbuy01.adapters.OrderListAdapter;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class FridgeActivity extends AppCompatActivity {
    INodeJS myAPI;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private FridgeListAdapter itemAdapter;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {//????
        if (requestCode == 2 && User.isSignedIn()) {//אם המשתמש מתחבר ו-REQUESTCODE שווה ל2 אז נפתח הדף הנוכחי
            //if the user signed in, we will refresh this activity
            finish();
            startActivity(getIntent());
        }
    }

    private void signedIn() {

        fetchFridge();
    }//end of signedIn()

    private void fetchFridge() {
        //init api
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        compositeDisposable.add(myAPI.getFridge(User.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                        System.out.println("**********************************");
                        Shop.setMyFridge(s);
                        String ss="";
                        if (!Shop.getMyFridge().isEmpty()){setUpRecyclerView();}
                        else {
                            setContentView(R.layout.empty_orders_layout);

                            //toolbar
                            Toolbar toolbar = findViewById(R.id.emptyOrdersToolbar);
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            getSupportActionBar().setDisplayShowHomeEnabled(true);
                        }
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }//end of fetchOrders()

    private void setUpRecyclerView() {
        setContentView(R.layout.activity_fridge);
        //view
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.FridgeRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager itemLayoutManager = new LinearLayoutManager(this);
        itemAdapter = new FridgeListAdapter(Shop.getMyFridge());

        recyclerView.setLayoutManager(itemLayoutManager);
        recyclerView.setAdapter(itemAdapter);

    }
}
