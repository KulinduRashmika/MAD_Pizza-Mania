package com.example.pizzamania;

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

        // Filter orders by status from top spinner
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                loadOrders(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Load all orders by default
        loadOrders("All");
    }

    private void loadOrders(String status) {
        Cursor cursor = status.equals("All") ? dbHelper.getAllOrders() : dbHelper.getOrdersByStatus(status);

        String[] from = {"customer_name", "email", "phone", "address", "items", "totalPrice", "orderDate", "status"};
        int[] to = {R.id.txtCustomerName, R.id.txtCustomerEmail, R.id.txtCustomerPhone, R.id.txtCustomerAddress,
                R.id.txtOrderItems, R.id.txtTotalPrice, R.id.txtOrderDate, R.id.spinnerOrderStatus};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.order_list_item,
                cursor,
                from,
                to,
                0
        );

        adapter.setViewBinder((view, cursor1, columnIndex) -> {
            if(view instanceof Spinner){
                Spinner spinner = (Spinner) view;

                ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                        AdminOrdersActivity.this,
                        R.array.order_status_options,
                        android.R.layout.simple_spinner_item);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);

                String currentStatus = cursor1.getString(columnIndex);
                int spinnerPosition = spinnerAdapter.getPosition(currentStatus);
                spinner.setSelection(spinnerPosition);

                long orderId = cursor1.getLong(cursor1.getColumnIndexOrThrow("_id"));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String newStatus = parent.getItemAtPosition(position).toString();
                        dbHelper.updateOrderStatus(orderId, newStatus);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                return true;
            }
            return false;
        });

        listViewOrders.setAdapter(adapter);
    }
}
