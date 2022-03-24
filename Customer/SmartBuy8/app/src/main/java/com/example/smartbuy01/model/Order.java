package com.example.smartbuy01.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("order_status")
    private String orderStatus;
    @SerializedName("date_of_order")
    private String dateOfOrder;
    @SerializedName("products_in_order")
    private List<ProductInOrderItem> productsInOrder;
    public class ProductInOrderItem{
        @SerializedName("product_id")
        private int productId;
        @SerializedName("amount_in_order")
        private int amountInOrder;
        public ProductInOrderItem(int productId,int amountInOrder){
            this.productId=productId;
            this.amountInOrder=amountInOrder;
        }

        public int getProductId() {
            return productId;
        }

        public int getAmountInOrder() {
            return amountInOrder;
        }
    }
    public class ProductsInOrder{
        @SerializedName("products_in_order")
        private List<ProductInOrderItem> productsInOrder;

        public void setProductsInOrder(List<ProductInOrderItem> productInOrder) {
            this.productsInOrder = productInOrder;
        }

        public List<ProductInOrderItem> getProductsInOrder() {
            return productsInOrder;
        }//-----------
    }
    public Order(int orderId, String orderStatus,String dateOfOrder){
        setOrderId(orderId);
        setOrderStatus(orderStatus);
        setDateOfOrder(dateOfOrder);
    }

    public void setProductsInOrder(List<ProductInOrderItem> productsInOrder) {
        this.productsInOrder = productsInOrder;
    }
    public void setProductInOrder(String JSONString){
        Gson gson = new Gson();
        ProductsInOrder productsInOrder = gson.fromJson(JSONString,ProductsInOrder.class );
        this.productsInOrder=productsInOrder.getProductsInOrder();
    }

    public List<ProductInOrderItem> getProductsInOrder() {
        return productsInOrder;
    }

    public void setOrderId(int orderId){
        this.orderId=orderId;
    }
    public int getOrderId(){
        return orderId;
    }
    public void setOrderStatus(String orderStatus){
        this.orderStatus=orderStatus;
    }
    public String getOrderStatus(){
        return orderStatus;
    }
    public void setDateOfOrder(String dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
    }
    public String getDateOfOrder() {
        return dateOfOrder;
    }
}
