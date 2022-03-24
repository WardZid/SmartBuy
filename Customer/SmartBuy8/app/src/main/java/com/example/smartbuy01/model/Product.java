package com.example.smartbuy01.model;

import com.example.smartbuy01.R;
import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("product_id")
    private int productId;
    @SerializedName("product_name")
    private String productName;
    @SerializedName("product_type")
    private ProductType productType;
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

    public Product(int productId,String productName,String productType,int amount,double weight,double price,String area, int row) {
        setProductId(productId);
        setProductName(productName);
        setProductType(productType);
        setAmount(amount);
        setWeight(weight);
        setPrice(price);
        setArea(area);
        setRow(row);
    }
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
    public ProductType getProductType() {
        return productType;
    }
    public void setProductType(ProductType productType) {
        this.productType=productType;
    }
    public void setProductType(String productType) {
        this.productType=ProductType.valueOf(productType);
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
    public int getProductImage(){
        return R.drawable.ic_product_image;
    }
    public boolean isAvailable(){
        if(amount==0) return false;
        return true;
    }
}
