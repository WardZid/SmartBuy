package com.example.smartbuy01.Retrofit;
import com.example.smartbuy01.model.ProductList;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface INodeJS {
    @GET("/connection/")
    Call<String> checkConnection();

    @POST("/register/")
    @FormUrlEncoded
    Observable<String> registerUser(@Field("email") String email,
                                    @Field("name") String name,
                                    @Field("password") String Password);

    @POST("/signin/")
    @FormUrlEncoded
    Observable<String> signInUser(@Field("email") String email,
                                  @Field("password") String Password);


    @GET("/products/")
    Call<ProductList> getAllProducts();

    @POST("/cart/")
    @FormUrlEncoded
    Observable<String> getCartProducts(@Field("user_id") String userId);

    @POST("/add_to_cart/")
    @FormUrlEncoded
    Observable<String> addToCart(@Field("user_id") String userId,
                                 @Field("product_id") int productId,
                                 @Field("amount_to_add") int amount);

    @POST("/alter_cart/")
    @FormUrlEncoded
    Observable<String> alterCart(@Field("user_id") String userId,
                                 @Field("product_id") int productId,
                                 @Field("amount_to_set") int amount);

    @POST("/delete_from_cart/")
    @FormUrlEncoded
    Observable<String> deleteFromCart(@Field("user_id") String userId,
                                 @Field("product_id") int productId);

    @POST("/new_order/")
    @FormUrlEncoded
    Observable<String> sendOrder(@Field("user_id") String userId);

    @POST("/orders/")
    @FormUrlEncoded
    Observable<String> getOrders(@Field("user_id") String userId);

    @POST("/products_in_order/")
    @FormUrlEncoded
    Observable<String> getProductsInOrder(@Field("order_id") int orderId);

    @POST("/fridge/")
    @FormUrlEncoded
    Observable<String> getFridge(@Field("user_id") String userId);
}

