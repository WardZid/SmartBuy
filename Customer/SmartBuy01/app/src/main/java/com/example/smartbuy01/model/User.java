package com.example.smartbuy01.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class User {
    //attributes
    @SerializedName("user_id")
    private static String userId; //unique user id
    @SerializedName("name")
    private static String name;
    @SerializedName("email")
    private static String email;
    @SerializedName("created_at")
    private static String dateCreated;
    //sign in status
    private static boolean signedIn;

    //user related lists
    private static List<Cart> myCart;
    private static List<Order> myOrders;
    private static List<Fridge> myFridge;

    //static setter
    public static void setUser(String userID,String uName, String uEmail,String userCreated){
        userId=userID;
        name=uName;
        email=uEmail;
        dateCreated=userCreated;
        signedIn=true;
    }
    public static void signOut(){
        //assign null to everything to sign out
        userId=null;
        name=null;
        email=null;
        dateCreated=null;
        signedIn=false;
        if(myCart!=null)myCart.clear();
        if(myOrders!=null)myOrders.clear();
        if(myFridge!=null)myFridge.clear();
    }
    public static boolean isSignedIn(){
        return signedIn;
    }
    //setters and getters
    public static String getUserId(){
        return userId;
    }
    public static String getName(){
        return name;
    }
    public static String getEmail() {
        return email;
    }
    public static String getDateCreated() {
        return dateCreated;
    }
    public static void setMyCart(String JSONString) {
        //convert from json to generic java object
        Gson gson = new Gson();
        CartList cartList = gson.fromJson(JSONString, CartList.class);
        myCart = cartList.getCart();
    }

    public static List<Cart> getMyCart() {
        return myCart;
    }

    public static void setMyOrders(String JSONString) {
        //convert from json to generic java object
        Gson gson = new Gson();
        OrderList orderList = gson.fromJson(JSONString, OrderList.class);
        myOrders = orderList.getOrderList();
    }

    public static List<Order> getMyOrders() {
        return myOrders;
    }

    public static List<Fridge> getMyFridge() {
        return myFridge;
    }

    public static void setMyFridge(String JSONString) {
        //convert from json to generic java object
        Gson gson = new Gson();
        FridgeList fridgeList = gson.fromJson(JSONString, FridgeList.class);
        myFridge = fridgeList.getFridgeList();
    }
}
