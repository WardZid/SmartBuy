package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderList {
    @SerializedName("orders")
    private List<Order> orderList;

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }
}
