package com.example.pizzamania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class OrderAdapterView extends RecyclerView.Adapter<OrderAdapterView.OrderViewHolder> {

    private ArrayList<String> ordersList;

    public OrderAdapterView(ArrayList<String> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cus_order_list_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        String orderInfo = ordersList.get(position);

        // Split orderInfo into lines
        String[] lines = orderInfo.split("\n");

        holder.txtOrderID.setText(lines[0]);
        holder.txtItems.setText(lines[1]);
        holder.txtTotalPrice.setText(lines[2]);
        holder.txtOrderDate.setText(lines[3]);
        holder.txtStatus.setText(lines[4]);
        holder.txtCustomer.setText(lines[5]);
        holder.txtEmail.setText(lines[6]);
        holder.txtPhone.setText(lines[7]);
        holder.txtAddress.setText(lines[8]);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderID, txtItems, txtTotalPrice, txtOrderDate, txtStatus;
        TextView txtCustomer, txtEmail, txtPhone, txtAddress;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderID = itemView.findViewById(R.id.txtOrderID);
            txtItems = itemView.findViewById(R.id.txtItems);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtCustomer = itemView.findViewById(R.id.txtCustomer);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtAddress = itemView.findViewById(R.id.txtAddress);
        }
    }
}
