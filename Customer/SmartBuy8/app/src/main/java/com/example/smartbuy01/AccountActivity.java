package com.example.smartbuy01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartbuy01.model.Shop;
import com.example.smartbuy01.model.User;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Shop.connectionStatus) startActivity(new Intent(getApplicationContext(),ConnectionErrorActivity.class));
        else if(User.isSignedIn()) signedIn();
        else notSignedIn();


    }
    private void notSignedIn(){
        setContentView(R.layout.please_sign_in);

        Toolbar toolbar= findViewById(R.id.pleaseToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Button signInButton=(Button)findViewById(R.id.pleaseSignInButton);
        TextView signInTextView=(TextView) findViewById(R.id.signInToContinueTextView);
        signInTextView.setText(R.string.signInAccount);

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
        }
    }
    private void signedIn(){
        setContentView(R.layout.activity_account);
        setUpToolbar();
        setUpInfo();

        Button signOutAccountButton=findViewById(R.id.signOutAccountButton);

        signOutAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.signOut();
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
}
