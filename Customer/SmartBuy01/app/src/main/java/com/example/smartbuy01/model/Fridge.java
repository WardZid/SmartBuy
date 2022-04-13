package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

public final class Fridge {
    //attributes
    @SerializedName("product_id")
    private int productId;
    @SerializedName("amount_in_fridge")
    private int amountInFridge;
    @SerializedName("added_at")
    private String addedAt;
    //product
    private Product productInFridge;
    //constructors
    public Fridge(int productId, int amountInFridge, String addedAt){
        setProductId(productId);
        setAmountInFridge(amountInFridge);
        setAddedAt(addedAt);
    }
    //setters/getters
    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }

    public void setAmountInFridge(int amountInFridge) {
        this.amountInFridge = amountInFridge;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAmountInFridge() {
        return amountInFridge;
    }

    public int getProductId() {
        return productId;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public Product getProductInFridge() {
        return productInFridge;
    }
    public void setProductInFridge(Product productInFridge) {
        this.productInFridge = productInFridge;
    }
    //methods
    @Override
    public String toString(){
        return  productId+" "+amountInFridge+" "+addedAt;
    }
}
