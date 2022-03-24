package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Cart {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("product_id")
    private int productId;
    @SerializedName("amount_in_cart")
    private int amountInCart;

    public Cart(int productId, int amount) {
        this.productId = productId;
        this.amountInCart = amount;
    }
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

    @Override
    public String toString(){
        return userId+" "+ productId+" "+amountInCart;
    }
}
