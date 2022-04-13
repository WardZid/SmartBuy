package com.example.smartbuy01.control.adapters;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.model.Cart;
import com.example.smartbuy01.model.Product;
import java.util.List;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListViewHolder> {

    //attributes
    private List<Cart> myCartList;
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
    public static class CartListViewHolder extends RecyclerView.ViewHolder{

        //views to insert info into
        public ImageView itemImageTextView;
        public TextView itemNameTextView;
        public TextView itemPriceTextView;
        public TextView itemTotalPriceTextView;

        public TextView itemAmountTextView;
        public ImageView itemDeleteImageView;

        public CartListViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            //fetching correspondent views from xml
            itemImageTextView=itemView.findViewById(R.id.cartItemImageView);
            itemNameTextView=itemView.findViewById(R.id.cartItemNameTextView);
            itemPriceTextView=itemView.findViewById(R.id.cartItemPriceTextView);
            itemTotalPriceTextView=itemView.findViewById(R.id.cartTotalPerItemTextView);

            itemAmountTextView=itemView.findViewById(R.id.cartItemAmountTextView);
            itemDeleteImageView=itemView.findViewById(R.id.cartItemDeleteImageView);

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
            //clicklistener for the item amount
            itemAmountTextView.setOnClickListener(new View.OnClickListener() {
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
            //delete image clicklistener
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

    public CartListAdapter(List<Cart> cartList){
        myCartList=cartList;
    }

    @NonNull
    @Override
    public CartListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate the list item for this place in the array
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cart_item,viewGroup,false);
        return new CartListViewHolder(v,myListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListViewHolder holder, int i) {
        //fill inflated list item with the info from the current item
        Cart currentItem = myCartList.get(i);
        Product myProduct=currentItem.getProductInCart();

        //set up info
        if(myProduct != null){
            //if there is no image, a generic image is used
            if(myProduct.getProductImage()==null)
                holder.itemImageTextView.setImageResource(R.drawable.ic_product_image);
            else
                holder.itemImageTextView.setImageBitmap(myProduct.getProductImage());
            holder.itemNameTextView.setText(myProduct.getProductName());
            holder.itemPriceTextView.setText(("$ "+myProduct.getPrice()));
            holder.itemTotalPriceTextView.setText(("$ "+(myProduct.getPrice()*currentItem.getAmount())));
            holder.itemAmountTextView.setText((""+currentItem.getAmount()));//i concatenate empty string so the setText() doesnt use the amount as a resource
            if(currentItem.getAmount()>myProduct.getAmount())//if the amount in cart is more than in stock
                holder.itemAmountTextView.setBackgroundColor( ContextCompat.getColor(holder.itemView.getContext(),R.color.outOfStock));
            else
                holder.itemAmountTextView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),R.drawable.md_btn_selected_dark));
        }else{
            holder.itemNameTextView.setText(R.string.itemUnavailable);
        }
    }
    @Override
    public int getItemCount() {
        return myCartList.size();
    }
}
