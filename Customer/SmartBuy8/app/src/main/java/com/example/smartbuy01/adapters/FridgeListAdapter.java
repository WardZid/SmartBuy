package com.example.smartbuy01.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.model.Fridge;
import com.example.smartbuy01.model.Order;
import com.example.smartbuy01.model.Product;
import com.example.smartbuy01.model.Shop;

import java.util.HashSet;
import java.util.List;

public class FridgeListAdapter extends RecyclerView.Adapter<FridgeListAdapter.FridgeListViewHolder> {
    private List<Fridge> myOrderList;
    private CartListAdapter.onItemClickListener myListener;

    public interface onItemClickListener{
        void onItemClick(int position);
        void onItemChange(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(CartListAdapter.onItemClickListener listener){
        myListener=listener;
    }
    public static class FridgeListViewHolder extends RecyclerView.ViewHolder {

        public ImageView itemImageTextView;
        public TextView itemNameTextView;
        public TextView itemPriceTextView;
        public TextView itemAdeddTextView;
        public TextView itemAmountTextView;
        public ImageView itemDeleteImageView;

        public FridgeListViewHolder(@NonNull View itemView, final CartListAdapter.onItemClickListener listener) {
            super(itemView);
            itemNameTextView=itemView.findViewById(R.id.fridgeItemNameTextView);
            itemAdeddTextView=itemView.findViewById(R.id.fridgeAdeddAt);
            itemAmountTextView=itemView.findViewById(R.id.fridgeItemAmountTextView);
            itemDeleteImageView=itemView.findViewById(R.id.fridgeItemDeleteImageView);
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
            itemDeleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){//because we need listener to call this in first method
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){//to make sure position is valid
                            listener.onDeleteClick(position);//passing to interface method made above
                        }
                    }
                }
            });
        }
    }
    public FridgeListAdapter(List<Fridge> fridgeList){
        myOrderList=fridgeList;
    }
        @NonNull
    @Override
    public FridgeListAdapter.FridgeListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frideg_item,viewGroup,false);
            FridgeListViewHolder fridgeListViewHolder=new FridgeListViewHolder(v,myListener);
            return fridgeListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FridgeListAdapter.FridgeListViewHolder fridgeListViewHolder, int i) {
        Fridge currentItem = myOrderList.get(i);


            fridgeListViewHolder.itemNameTextView.setText(" " +currentItem.getProductId());
            fridgeListViewHolder.itemAmountTextView.setText(" "+currentItem.getAmountInFridge());
            fridgeListViewHolder.itemAdeddTextView.setText((currentItem.getAddedAt()));

    }

    @Override
    public int getItemCount() {
        return myOrderList.size();
    }
}
