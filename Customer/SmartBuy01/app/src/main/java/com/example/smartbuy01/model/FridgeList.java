package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FridgeList {
    //attr
    @SerializedName("fridge")
    private List<Fridge> fridgeList;
    //setters/getters
    public List<Fridge> getFridgeList() {
        return fridgeList;
    }

    public void setFridgeList(List<Fridge> fridgeList) {
        this.fridgeList = fridgeList;
    }
}
