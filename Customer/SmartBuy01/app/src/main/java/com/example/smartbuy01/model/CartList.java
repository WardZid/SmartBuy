package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartList {
    //attr
    @SerializedName("cart")
    private List<Cart> cart;
    //setters and getters
    public List<Cart> getCart() {
        return cart;
    }

    public void setCart(List<Cart> cart) {
        this.cart = cart;
    }
}
