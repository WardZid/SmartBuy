package com.example.smartbuy01.control.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.model.Product;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> {

    //attributes
    private List<Product> myProductList;
    private onItemClickListener myListener;

    //inteface for clicklistener
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        myListener=listener;
    }

    //inner holder class
    public static class ProductListViewHolder extends RecyclerView.ViewHolder{

        //views to insert info into
        public ImageView itemImageView;
        public TextView itemNameView;
        public TextView itemDescriptionView;

        public ProductListViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            //fetching correspondent views from xml
            itemImageView=itemView.findViewById(R.id.itemImageView);
            itemNameView=itemView.findViewById(R.id.itemNameTextView);
            itemDescriptionView=itemView.findViewById(R.id.itemDescriptionTextView);

            //initiate clicklistener for the whole view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){//because we need listener to call this in first method
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){//to make sure position is valid
                            listener.onItemClick(position);//passing to interface method made above
                        }
                    }
                }
            });
        }
    }

    public ProductListAdapter(List<Product> productList){
        myProductList=productList;
    }

    @NonNull
    @Override
    public ProductListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate the list item for this place in the array
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_list_item,viewGroup,false);
        return new ProductListViewHolder(v,myListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListViewHolder holder, int i) {
        //fill inflated list item with the info from the current item
        Product currentItem = myProductList.get(i);

        if(currentItem.getProductImage()==null)
            holder.itemImageView.setImageResource(R.drawable.ic_product_image);
        else
            holder.itemImageView.setImageBitmap(currentItem.getProductImage());
        holder.itemNameView.setText(currentItem.getProductName());
        holder.itemDescriptionView.setText(("$ "+currentItem.getPrice()));

    }

    @Override
    public int getItemCount() {
        return myProductList.size();
    }

    public void filterList(List<Product> filteredList) {
        myProductList = filteredList;
        notifyDataSetChanged();
    }
}
