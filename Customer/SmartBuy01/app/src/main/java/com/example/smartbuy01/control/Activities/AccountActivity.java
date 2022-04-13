package com.example.smartbuy01.control.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if no connection open the error activity
        if(!Shop.connectionStatus) startActivity(new Intent(getApplicationContext(), ConnectionErrorActivity.class));
        else if(User.isSignedIn()) signedIn();
        else notSignedIn();


    }
    private void notSignedIn(){
        //if not signed in, ask to sign in
        setContentView(R.layout.please_sign_in);

        Toolbar toolbar= findViewById(R.id.pleaseToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button signInButton=findViewById(R.id.pleaseSignInButton);
        TextView signInTextView=findViewById(R.id.signInToContinueTextView);
        signInTextView.setText(R.string.signInAccount);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), SignInActivity.class),2);//result being the user signed in
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        //when the activity called is done
        if(requestCode==2 && User.isSignedIn()){
            //if the user signed in, we will refresh this activity
            finish();
            startActivity(getIntent());
        }
    }
    private void signedIn(){
        //if user signed in
        setContentView(R.layout.activity_account);
        setUpToolbar();
        setUpInfo();

        Button signOutAccountButton=findViewById(R.id.signOutAccountButton);

        signOutAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.signOut();
                //empty out the suggestions
                ShopActivity.myProductSuggestions.clear();
                finish();
            }
        });
    }
    private void setUpInfo(){
        TextView nameAccountTextView=findViewById(R.id.nameAccountTextView);
        TextView emailAccountTextView=findViewById(R.id.emailAccountTextView);
        TextView createdAccountTextView=findViewById(R.id.createdAccountTextView);

        nameAccountTextView.setText(User.getName());
        emailAccountTextView.setText(User.getEmail());
        createdAccountTextView.setText(User.getDateCreated());
    }
    private void setUpToolbar(){
        Toolbar toolbar= findViewById(R.id.accountToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}