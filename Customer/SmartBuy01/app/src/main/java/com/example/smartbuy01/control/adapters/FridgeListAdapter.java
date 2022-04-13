package com.example.smartbuy01.control.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.model.Fridge;
import com.example.smartbuy01.model.Product;

import java.util.List;

public class FridgeListAdapter extends RecyclerView.Adapter<FridgeListAdapter.FridgeListViewHolder> {

    //attributes
    private List<Fridge> myFridgeList;
    private onItemClickListener myListener;

    //inteface for clicklistener
    public interface onItemClickListener{
        void onItemClick(int position);
        void onItemChange(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        myListener=listener;
    }

    //inner holder class
    public static class FridgeListViewHolder extends RecyclerView.ViewHolder{

        //views to insert info into
        public ImageView fridgeImageView;
        public TextView fridgeNameTextView;
        public TextView fridgeDateBoughtTextView;
        public TextView fridgeAmountTextView;
        public ImageView fridgeDeleteImageView;

        public FridgeListViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            //fetching correspondent views from xml
            fridgeImageView=itemView.findViewById(R.id.fridgeItemImageView);
            fridgeNameTextView=itemView.findViewById(R.id.fridgeItemNameTextView);
            fridgeDateBoughtTextView=itemView.findViewById(R.id.fridgeDateTextView);
            fridgeAmountTextView=itemView.findViewById(R.id.fridgeItemAmountTextView);
            fridgeDeleteImageView=itemView.findViewById(R.id.fridgeItemDeleteImageView);

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
            //initiate clicklistener for amount view
            fridgeAmountTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){//because we need listener to call this in first method
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){//to make sure position is valid
                            listener.onItemChange(position);//passing to interface method made above
                        }
                    }
                }
            });
            //initiate clicklistener for the delete button
            fridgeDeleteImageView.setOnClickListener(new View.OnClickListener() {
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
        myFridgeList=fridgeList;
    }

    @NonNull
    @Override
    public FridgeListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate the list item for this place in the array
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fridge_item,viewGroup,false);
        return new FridgeListViewHolder(v,myListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FridgeListViewHolder holder, int i) {
        //fill inflated list item with the info from the current item
        Fridge currentItem = myFridgeList.get(i);
        Product myProduct=currentItem.getProductInFridge();

        if(myProduct.getProductImage()==null)
            holder.fridgeImageView.setImageResource(R.drawable.ic_product_image);
        else
            holder.fridgeImageView.setImageBitmap(myProduct.getProductImage());
        holder.fridgeNameTextView.setText(myProduct.getProductName());
        holder.fridgeDateBoughtTextView.setText(currentItem.getAddedAt());
        holder.fridgeAmountTextView.setText(""+currentItem.getAmountInFridge());//concat with empty string to turn into string
    }

    @Override
    public int getItemCount() {
        return myFridgeList.size();
    }
}
