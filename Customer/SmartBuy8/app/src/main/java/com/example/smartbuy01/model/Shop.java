package com.example.smartbuy01.model;

import com.example.smartbuy01.Retrofit.INodeJS;
import com.example.smartbuy01.Retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public final class Shop {
    private static INodeJS myAPI;

    private static List<Product> productsInShop;

    private static List<ProductType> productTypesInShop;

    private static List<Cart> myCart;

    private static List<Order> myOrders;
    private static List<Fridge> myFridge;

    public static boolean connectionStatus;

    public static void setProductsInShop(List<Product> productsInShop) {
        Shop.productsInShop = productsInShop;

        /*
        * this is just so the category list only contains categories of available items
        * and so no category is entered multiple times
        * */
        productTypesInShop=new ArrayList<>();
        for(int i=0;i<productsInShop.size();i++)
            if(!productTypesInShop.contains(productsInShop.get(i).getProductType())
                && productsInShop.get(i).getAmount()>0)
                productTypesInShop.add(productsInShop.get(i).getProductType());
    }
    public static List<Product> getProductsInShop() {
        return productsInShop;
    }
    public static List<ProductType> getProductTypesInShop() {
        return productTypesInShop;
    }

    public static void setMyCart(String JSONString){
        Gson gson = new Gson();
        CartList cartList = gson.fromJson(JSONString, CartList.class);
        Shop.myCart=cartList.getCart();
        System.out.println(myCart.toString());
    }
    public static List<Cart> getMyCart(){
        return myCart;
    }

    public static int getPositionOfProduct(int positionInCart){
        //to get position of product in product list
        int positionInProductList = -1;
        for(int ind=0;ind<productsInShop.size();ind++){
            if(myCart.get(positionInCart).getProductId() == productsInShop.get(ind).getProductId())
                positionInProductList=ind;
        }
        return positionInProductList;
    }

    public static void fetchProducts() {
        //*******all this is to fetch products
        Retrofit retrofit = RetrofitClient.getGSONInstance();
        myAPI = retrofit.create(INodeJS.class);

        Call<ProductList> call = myAPI.getAllProducts();
        call.enqueue(new Callback<ProductList>() {//getting response from server
            @Override
            public void onResponse(Call<ProductList> call, Response<ProductList> response) {
                ProductList body = response.body();
                List<Product> products = body.getProducts();
                Shop.setProductsInShop(products);
            }
            @Override
            public void onFailure(Call<ProductList> call, Throwable t) {
            }
        });//end of enqueque
    }

    public static void connection() {
        //*******all this is to fetch products
        Retrofit retrofit = RetrofitClient.getGSONInstance();
        myAPI = retrofit.create(INodeJS.class);

        Call<String> call = myAPI.checkConnection();
        call.enqueue(new Callback<String>() {//getting response from server
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                connectionStatus=true;
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                connectionStatus=false;
            }
        });//end of enqueque
    }

    public static int getTotalAmountInCart(){
        //gets total amount of items in cart
        int countOfItems=0;
        for (Cart cartItem: myCart) {
            countOfItems+=cartItem.getAmount();
        }
        return countOfItems;
    }

    public static int getTotalPriceInCart(){
        //gets the sum total price of everything in the cart
        int totalPrice=0;
        Product myProduct;
        for(int ind=0;ind<myCart.size();ind++){
            myProduct=productsInShop.get(getPositionOfProduct(ind));
            totalPrice+=myCart.get(ind).getAmount()*myProduct.getPrice();
        }
        return totalPrice;
    }

    public static boolean cartAmountValid(){
        fetchProducts();
        //if any of the item in the cart have an amount less than what's in the shop, i.e. not enough or out of stock
        for(int ind=0;ind<myCart.size();ind++){
            if(myCart.get(ind).getAmount()> productsInShop.get(getPositionOfProduct(ind)).getAmount())
                return false;
        }
        return true;
    }
public static void setMyFridge(String JSONString){
    Gson gson = new Gson();
    FridgeList fridgeList = gson.fromJson(JSONString, FridgeList.class);
    Shop.myFridge=fridgeList.getFridges();
    System.out.println(myFridge.toString());
}

public static List<Fridge> getMyFridge(){return myFridge;}



    public static void setMyOrders(String JSONString) {
        Gson gson = new Gson();
        OrderList orderList = gson.fromJson(JSONString, OrderList.class);
        Shop.myOrders=orderList.getOrderList();
        System.out.println(myOrders.toString());
    }

    public static List<Order> getMyOrders() {
        return myOrders;
    }

    public static Product getProductById(int productId){
        for (Product p: productsInShop) {
            if(p.getProductId()==productId)
                return p;
        }
        //if product doesn't exist
        return null;
    }
}
