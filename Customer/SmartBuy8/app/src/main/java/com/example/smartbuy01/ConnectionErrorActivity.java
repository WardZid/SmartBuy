package com.example.smartbuy01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.smartbuy01.model.Shop;

public class ConnectionErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_error);

        Button retryButton=findViewById(R.id.retryButton);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkConnection();
            }
        });
    }
    @Override
    public void onBackPressed(){
        checkConnection();
    }
    private void checkConnection(){
        Shop.connection();
        if(Shop.connectionStatus)
            finish();
    }
}
