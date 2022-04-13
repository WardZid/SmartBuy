package com.example.smartbuy01.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.gson.annotations.SerializedName;
public class Product {
    //attributes
    @SerializedName("product_id")
    private int productId;
    @SerializedName("product_name")
    private String productName;
    @SerializedName("product_type")
    private String productType;
    @SerializedName("amount")
    private int amount;
    @SerializedName("weight")
    private double weight;
    @SerializedName("price")
    private double price;
    @SerializedName("area")
    private String area;
    @SerializedName("row")
    private int row;
    //image byte array
    private byte[] image;
    //constructor
    public Product(int productId, String productName, String productType, int amount, double weight, double price, String area, int row) {
        setProductId(productId);
        setProductName(productName);
        setProductType(productType);
        setAmount(amount);
        setWeight(weight);
        setPrice(price);
        setArea(area);
        setRow(row);
    }
    //getters/setters
    public int getProductId() {
        return productId;
    }
    public void setProductId(int productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductType() {
        return productType;
    }
    public void setProductType(String productType) {
        this.productType=productType;
    }
    public int getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public String getArea() {
        return area;
    }
    public void setArea(String area) {
        this.area = area;
    }
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public void setImage(byte[] byteArray){
       this.image=byteArray;
    }
    public Bitmap getProductImage(){
        //byte array will be converted ot a bitmap image before it is presented
        if(image==null) return null;
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    //methods
    public boolean isAvailable(){
        //if the product is in stock
        if(amount==0) return false;
        return true;
    }
}
