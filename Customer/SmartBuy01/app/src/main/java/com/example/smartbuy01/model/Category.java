package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    //attributes
    @SerializedName("product_type")
    private String category;
    //setters and getters
    public Category(String category){
        this.category=category;
    }

    public String getCategory() {
        return category;
    }
    //methods
    @Override
    public String toString() {
        //the underscore is replaced with a space for more presentation
        return category.replace('_',' ');
    }
}
