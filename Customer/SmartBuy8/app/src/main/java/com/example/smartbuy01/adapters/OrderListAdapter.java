package com.example.smartbuy01.adapters;

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

    private List<Order> myOrderList;
    private onItemClickListener myListener;

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        myListener=listener;
    }

    public static class OrderListViewHolder extends RecyclerView.ViewHolder{

        public TextView orderIdTextView;
        public TextView orderStatusTextView;

        public OrderListViewHolder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);

            orderIdTextView=itemView.findViewById(R.id.orderIdTextView);
            orderStatusTextView=itemView.findViewById(R.id.orderStatusTextView);

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
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_item,viewGroup,false);
        OrderListViewHolder olvh = new OrderListViewHolder(v,myListener);
        return olvh;
    }

    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int i) {
        Order currentItem = myOrderList.get(i);

        holder.orderIdTextView.setText(("Order ID: "+currentItem.getOrderId()));
        holder.orderStatusTextView.setText(currentItem.getOrderStatus());

    }

    @Override
    public int getItemCount() {
        return myOrderList.size();
    }
}
