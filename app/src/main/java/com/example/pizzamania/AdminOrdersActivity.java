package com.example.pizzamania;

import android.database.Cursor;
import android.net.http.UrlRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        loadOrders("Pending");

        // ✅ Handle spinner selection
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                loadOrders(selectedStatus); // reload orders for selected status
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void loadOrders(String status) {
        Cursor cursor;
        if(status.equals("All")){
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

        listViewOrders.setAdapter(adapter);
    }

}
