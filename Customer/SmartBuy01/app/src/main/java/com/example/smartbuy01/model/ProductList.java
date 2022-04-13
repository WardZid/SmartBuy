package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductList {
    //attrs
    @SerializedName("products")
    private List<Product> products;
    //get/set
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
