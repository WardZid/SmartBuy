package com.example.smartbuy01.model;

import com.google.gson.annotations.SerializedName;

public class ByteArray {
    //class used as a json deserializer byte array into a byte array the can b formatted into a bitmapimage
    @SerializedName("image")
    private byte[] byteArray;
    public void setByteArray(byte[] byteArray) {
        this.byteArray = byteArray;
    }

    public byte[] getByteArray() {
        return byteArray;
    }
}
