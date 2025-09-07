package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Orders extends AppCompatActivity {

    SqliteHelper dbHelper;
    ListView listViewOrders;
    String sessionEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        listViewOrders = findViewById(R.id.listViewOrders);
        dbHelper = new SqliteHelper(this);

        // Get logged-in customer email from session
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        sessionEmail = sharedPreferences.getString("email", null);

        // Load orders
        loadOrders();

        // ✅ Setup bottom navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(Orders.this, CustomerDashboard.class));
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    startActivity(new Intent(Orders.this, CartActivity.class));
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    // Already here
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(Orders.this, ProfileActivity.class));
                    return true;
                }

                return false;
            }
        });

        // Highlight current page
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);
    }

    private void loadOrders() {
        int customerId = -1;
        Cursor cursorCustomer = dbHelper.getCustomerByEmail(sessionEmail);
        if (cursorCustomer != null && cursorCustomer.moveToFirst()) {
            customerId = cursorCustomer.getInt(cursorCustomer.getColumnIndexOrThrow("customer_ID"));
            cursorCustomer.close();
        }

        ArrayList<String> ordersList = new ArrayList<>();
        Cursor cursor = dbHelper.getOrdersByCustomer(customerId);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String orderInfo = "Order #" + cursor.getInt(cursor.getColumnIndexOrThrow("order_ID"))
                        + "\nItems: " + cursor.getString(cursor.getColumnIndexOrThrow("items"))
                        + "\nTotal: Rs. " + cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice"))
                        + "\nDate: " + cursor.getString(cursor.getColumnIndexOrThrow("orderDate"))
                        + "\nStatus: " + cursor.getString(cursor.getColumnIndexOrThrow("status"));
                ordersList.add(orderInfo);
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ordersList);
        listViewOrders.setAdapter(adapter);
    }
}
