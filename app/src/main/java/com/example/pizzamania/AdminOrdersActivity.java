package com.example.pizzamania;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class AdminOrdersActivity extends AppCompatActivity {

    SqliteHelper dbHelper;
    ListView listViewOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        listViewOrders = findViewById(R.id.listViewOrders);
        dbHelper = new SqliteHelper(this);

        loadOrders();
    }

    private void loadOrders() {
        Cursor cursor = dbHelper.getAllOrders();

        // Must match the column names in your SELECT query
        String[] from = {"customer_name", "items", "totalPrice", "orderDate", "status"};

        // Must match your order_list_item.xml IDs
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

        listViewOrders.setAdapter(adapter);
    }

}
