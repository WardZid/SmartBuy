package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

public final class Fridge {

        @SerializedName("product_id")
        private int productId;
        @SerializedName("amount_in_fridge")
        private int amountInFridge;
        @SerializedName("added_at")
    private String addedAt;

        public Fridge(int productId,int amountInFridge,String addedAt){
            setProductId(productId);
            setAmountInFridge(amountInFridge);
            setAddedAt(addedAt);
        }

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

    @Override
    public String toString(){
        return  productId+" "+amountInFridge+" "+addedAt;
    }
}
