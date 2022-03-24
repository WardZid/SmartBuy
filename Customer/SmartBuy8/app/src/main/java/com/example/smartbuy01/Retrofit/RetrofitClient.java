package com.example.smartbuy01.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit scalarsInstance;
    private static Retrofit GSONInstance;

    public static Retrofit getScalarsInstance(){
        if(scalarsInstance == null)
            scalarsInstance = new Retrofit.Builder()//25.46.82.42
                    .baseUrl("http://25.46.82.42:3000/") //in emulater 127.0.0.1 will change to 10.0.2.2
                    //3000שרת
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return scalarsInstance;
    }
    public static Retrofit getGSONInstance(){
        if(GSONInstance == null) {
            GSONInstance = new Retrofit.Builder()//25.46.82.42
                    .baseUrl("http://25.46.82.42:3000/") //in emulater 127.0.0.1 will change to 10.0.2.2
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return GSONInstance;
    }
}
