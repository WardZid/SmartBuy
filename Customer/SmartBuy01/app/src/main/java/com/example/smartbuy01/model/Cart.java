package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

public final class Cart {
    //attributes
    @SerializedName("user_id")
    private String userId;
    @SerializedName("product_id")
    private int productId;
    @SerializedName("amount_in_cart")
    private int amountInCart;
    //list of products in cart
    private Product productInCart;
    //constructor
    public Cart(int productId, int amount) {
        this.productId = productId;
        this.amountInCart = amount;
    }
    //setters and getters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amountInCart;
    }

    public void setAmount(int amount) {
        this.amountInCart = amount;
    }

    public void setProductInCart(Product productInCart) {
        this.productInCart = productInCart;
    }

    public Product getProductInCart() {
        return productInCart;
    }
    //methods
    @Override
    public String toString(){
        return userId+" "+ productId+" "+amountInCart;
    }
}
