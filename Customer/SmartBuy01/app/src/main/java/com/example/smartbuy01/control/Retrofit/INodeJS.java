package com.example.smartbuy01.control.Retrofit;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface INodeJS {
    //interface used to communicate with the server using http requests
    @GET("/connection/")
    Observable<String> checkConnection();

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
    Observable<String> getAllProducts();

    @POST("/product/")
    @FormUrlEncoded
    Observable<String> getProductById(@Field("product_id") int product_id);

    @GET("/categories/")
    Observable<String> getAllCategories();

    @POST("/products_by_type/")
    @FormUrlEncoded
    Observable<String> getProductsByType(@Field("product_type") String productType);

    @POST("/cart/")
    @FormUrlEncoded
    Observable<String> getCartProducts(@Field("user_id") String userId);

    @POST("/products_in_cart/")
    @FormUrlEncoded
    Observable<String> getProductsInCart(@Field("user_id") String userId);

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

    @POST("/products_in_order_summary/")
    @FormUrlEncoded
    Observable<String> getProductsInOrderSummary(@Field("order_id") int orderId);

    @POST("/products_in_order/")
    @FormUrlEncoded
    Observable<String> getProductsInOrder(@Field("order_id") int orderId);

    @POST("/fridge/")
    @FormUrlEncoded
    Observable<String> getFridge(@Field("user_id") String userId);

    @POST("/alter_fridge/")
    @FormUrlEncoded
    Observable<String> alterFridge(@Field("user_id") String userId,
                                 @Field("product_id") int productId,
                                 @Field("amount_to_set") int amount);

    @POST("/delete_from_fridge/")
    @FormUrlEncoded
    Observable<String> deleteFromFridge(@Field("user_id") String userId,
                                      @Field("product_id") int productId);

    @POST("/products_in_fridge/")
    @FormUrlEncoded
    Observable<String> getProductsInFridge(@Field("user_id") String userId);

    @GET("/best_sellers/")
    Observable<String> getBestSellers();

    @POST("/suggestions/")
    @FormUrlEncoded
    Observable<String> getSuggestions(@Field("user_id") String userId);


    @POST("/favourites/")
    @FormUrlEncoded
    Observable<String> getFavourites(@Field("user_id") String userId);

    @POST("/add_to_favourites/")
    @FormUrlEncoded
    Observable<String> addToFavourites(@Field("user_id") String userId,
                                   @Field("product_id") int productId);

    @POST("/delete_from_favourites/")
    @FormUrlEncoded
    Observable<String> deleteFromFavourites(@Field("user_id") String userId,
                                        @Field("product_id") int productId);
    @POST("/blob_image/")
    @FormUrlEncoded
    Observable<String> getProductImage(@Field("product_id") int productId);
}