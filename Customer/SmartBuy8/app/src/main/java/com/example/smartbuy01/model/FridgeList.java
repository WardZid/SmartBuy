package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FridgeList {
    @SerializedName("fridge")
    private List<Fridge> fridges;

    public List<Fridge> getFridges() {
        return fridges;
    }

    public void setFridges(List<Fridge> fridges) {
        this.fridges = fridges;
    }
}
