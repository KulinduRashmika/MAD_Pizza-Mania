package com.example.pizzamania;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateOrderStatusActivity extends AppCompatActivity {

    Spinner spinnerStatus;
    Button btnUpdateStatus;
    SqliteHelper dbHelper;
    int orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_order_status);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        dbHelper = new SqliteHelper(this);

        // ------------------ EXTRA ------------------
        orderId = getIntent().getIntExtra("ORDER_ID", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String[] statusOptions = {"Pending", "In Progress", "Delivered", "Cancelled"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        btnUpdateStatus.setOnClickListener(v -> {
            String selectedStatus = spinnerStatus.getSelectedItem().toString();
            boolean updated = dbHelper.updateOrderStatus(orderId, selectedStatus);
            if (updated) {
                Toast.makeText(this, "Order status updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
