package com.example.smartbuy01.control.Retrofit;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofitInstance;

    public static Retrofit getScalarsInstance(){
        if(retrofitInstance == null) {//if an instance hasnt been initiated yet, a new on is built
            //okhttp client assists by giving us control over timeouts
            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(6000, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();
            retrofitInstance = new Retrofit.Builder()
                    //25.46.82.42 is the server address, a url title could be used here instead
                    .baseUrl("http://25.46.126.22:3000/")//25.46.82.42
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofitInstance;
    }
}