package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

public final class User {
    @SerializedName("user_id")
    private static String userId; //unique user id

    @SerializedName("name")
    private static String name;

    @SerializedName("email")
    private static String email;

    @SerializedName("created_at")
    private static String dateCreated;

    private static boolean signedIn;
    private User(String uId,String name,String email,String userCreated){//unused
        this.userId=uId;
        this.name=name;
        this.email=email;
        dateCreated=userCreated;
        signedIn=true;
    }
    public static void setUser(String userID,String uName, String uEmail,String userCreated){
        userId=userID;
        name=uName;
        email=uEmail;
        dateCreated=userCreated;
        signedIn=true;
    }
    public static void signOut(){
        userId=null;
        name=null;
        email=null;
        dateCreated=null;
        signedIn=false;
    }
    public static boolean isSignedIn(){
        return signedIn;
    }
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
}
