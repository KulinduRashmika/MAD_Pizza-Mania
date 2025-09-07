package com.example.pizzamania;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class AdminOrdersActivity extends AppCompatActivity {

    SqliteHelper dbHelper;
    ListView listViewOrders;
    Spinner spinnerStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        listViewOrders = findViewById(R.id.listViewOrders);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        dbHelper = new SqliteHelper(this);

        // ------------------ Spinner Setup ------------------
        String[] statusOptions = {"All", "Pending", "In Progress", "Delivered", "Cancelled"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statusOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                loadOrders(selectedStatus); // reload orders based on selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // ------------------ Item Click ------------------
        listViewOrders.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(AdminOrdersActivity.this, UpdateOrderStatusActivity.class);
            intent.putExtra("ORDER_ID", (int) id); // Pass order ID
            startActivity(intent);
        });

        // Load default (Pending) orders
        spinnerStatus.setSelection(1);
    }

    private void loadOrders(String status) {
        Cursor cursor;
        if (status.equals("All")) {
            cursor = dbHelper.getAllOrders();
        } else {
            cursor = dbHelper.getOrdersByStatus(status);
        }

        String[] from = {"customer_name", "items", "totalPrice", "orderDate", "status"};
        int[] to = {
                R.id.txtCustomer,
                R.id.txtItems,
                R.id.txtTotalPrice,
                R.id.txtOrderDate,
                R.id.txtStatus
        };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.order_list_item,
                cursor,
                from,
                to,
                0
        );

        // ------------------ Combine customer info ------------------
        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            int viewId = view.getId();
            if (viewId == R.id.txtCustomer) {
                String customerInfo =
                        cursor1.getString(cursor1.getColumnIndexOrThrow("customer_name")) +
                                "\nEmail: " + cursor1.getString(cursor1.getColumnIndexOrThrow("email")) +
                                "\nPhone: " + cursor1.getString(cursor1.getColumnIndexOrThrow("phone")) +
                                "\nAddress: " + cursor1.getString(cursor1.getColumnIndexOrThrow("address"));
                ((android.widget.TextView) view).setText(customerInfo);
                return true;
            }
            return false;
        });

        listViewOrders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh orders when returning from UpdateOrderStatusActivity
        loadOrders(spinnerStatus.getSelectedItem().toString());
    }
}
