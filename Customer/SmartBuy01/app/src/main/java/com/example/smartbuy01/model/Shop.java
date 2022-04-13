package com.example.smartbuy01.model;

import com.example.smartbuy01.control.Retrofit.INodeJS;
import com.example.smartbuy01.control.Retrofit.RetrofitClient;
import com.google.gson.Gson;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public final class Shop {
    //server api
    private static INodeJS myAPI;
    private static CompositeDisposable compositeDisposable = new CompositeDisposable();
    public static boolean connectionStatus;

    //start of global methods
    public static void connection() {
        //start up retrofit instance
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        //if true, is returned, we are successfully connected
        //if there is no answer, th connection is invalid
        compositeDisposable.add(myAPI.checkConnection()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (s.equals("true"))
                            connectionStatus = true;
                        else
                            connectionStatus = false;
                    }//end of accept
                })//end of subscribe
        );//end of compositeDisposable.add
    }
    public static void getProductImage(final Product myProduct) {
        //get retrofit instance
        Retrofit retrofit = RetrofitClient.getScalarsInstance();
        myAPI = retrofit.create(INodeJS.class);

        //post request to server
        compositeDisposable.add(myAPI.getProductImage(myProduct.getProductId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        //convert json to bytearray
                        Gson gson = new Gson();
                        ByteArray byteArray = gson.fromJson(s, ByteArray.class);
                        myProduct.setImage(byteArray.getByteArray());
                    }//end of accept
                })//end of subscribe
            );//end of compositeDisposable.add
    }
}
