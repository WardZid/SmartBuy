package com.example.smartbuy01.control.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartbuy01.R;
import com.example.smartbuy01.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {

    //attributes
    private List<Order> myOrderList;
    private onItemClickListener myListener;

    //inteface for clicklistener
    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        myListener=listener;
    }

    //inner holder class
    public static class OrderListViewHolder extends RecyclerView.ViewHolder{

        //views to insert info into
        public TextView orderIdTextView;
        public TextView orderStatusTextView;

        public OrderListViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            //fetching correspondent views from xml
            orderIdTextView=itemView.findViewById(R.id.orderIdTextView);
            orderStatusTextView=itemView.findViewById(R.id.orderStatusTextView);

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

    public OrderListAdapter(List<Order> orderList){
        myOrderList=orderList;
    }

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflate the list item for this place in the array
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item,viewGroup,false);
        return new OrderListViewHolder(v,myListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int i) {
        //fill inflated list item with the info from the current item
        Order currentItem = myOrderList.get(i);

        holder.orderIdTextView.setText(("Order ID: "+currentItem.getOrderId()));
        holder.orderStatusTextView.setText(currentItem.getOrderStatus());

    }

    @Override
    public int getItemCount() {
        return myOrderList.size();
    }
}
