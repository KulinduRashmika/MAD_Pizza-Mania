package com.example.pizzamania;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Orders extends AppCompatActivity {

    SqliteHelper dbHelper;
    ListView listViewOrders;
    Spinner spinnerStatus;
    String sessionEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        listViewOrders = findViewById(R.id.listViewOrders);
        spinnerStatus = findViewById(R.id.spinnerStatus); // Add Spinner in XML
        dbHelper = new SqliteHelper(this);

        // Get logged-in customer email from session
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        sessionEmail = sharedPreferences.getString("email", null);

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
                loadOrders(selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Setup bottom navigation
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
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    startActivity(new Intent(Orders.this, ProfileActivity.class));
                    return true;
                }

                return false;
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.nav_orders);

        // Load default orders (All)
        spinnerStatus.setSelection(0);
    }

    private void loadOrders(String status) {
        int customerId = -1;
        Cursor cursorCustomer = dbHelper.getCustomerByEmail(sessionEmail);
        if (cursorCustomer != null && cursorCustomer.moveToFirst()) {
            customerId = cursorCustomer.getInt(cursorCustomer.getColumnIndexOrThrow("customer_ID"));
            cursorCustomer.close();
        }

        ArrayList<String> ordersList = new ArrayList<>();
        Cursor cursor;
        if (status.equals("All")) {
            cursor = dbHelper.getOrdersByCustomer(customerId);
        } else {
            cursor = dbHelper.getOrdersByCustomerAndStatus(customerId, status); // Need to add this in SQLiteHelper
        }

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String displayStatus = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                switch (displayStatus) {
                    case "Pending": displayStatus = "⏳ Pending"; break;
                    case "In Progress": displayStatus = "🚚 In Progress"; break;
                    case "Delivered": displayStatus = "✅ Delivered"; break;
                    case "Cancelled": displayStatus = "❌ Cancelled"; break;
                }

                String orderInfo =
                        "Order #" + cursor.getInt(cursor.getColumnIndexOrThrow("order_ID")) +
                                "\nItems: " + cursor.getString(cursor.getColumnIndexOrThrow("items")) +
                                "\nTotal: Rs. " + cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice")) +
                                "\nDate: " + cursor.getString(cursor.getColumnIndexOrThrow("orderDate")) +
                                "\nStatus: " + displayStatus +
                                "\n\n👤 Customer Details:" +
                                "\nName: " + cursor.getString(cursor.getColumnIndexOrThrow("customer_name")) +
                                "\nEmail: " + cursor.getString(cursor.getColumnIndexOrThrow("email")) +
                                "\nPhone: " + cursor.getString(cursor.getColumnIndexOrThrow("phone")) +
                                "\nAddress: " + cursor.getString(cursor.getColumnIndexOrThrow("address"));
                ordersList.add(orderInfo);
            }
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ordersList);
        listViewOrders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders(spinnerStatus.getSelectedItem().toString());
    }
}
