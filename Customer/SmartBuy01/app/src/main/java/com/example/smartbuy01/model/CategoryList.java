package com.example.smartbuy01.model;

        import com.google.gson.annotations.SerializedName;
        import java.util.List;

public class CategoryList {
    //attr
    @SerializedName("categories")
    private List<Category> categories;
    //setters and getters
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
