package com.example.pizzamania;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {

    Button btnAddProduct, btnViewProducts, btnViewOrders, btnViewDeliveries, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Bind views
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnViewProducts = findViewById(R.id.btnViewProducts);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnViewDeliveries = findViewById(R.id.btnViewDeliveries);// this is Show Location button
        btnLogout = findViewById(R.id.btnLogout); // Make sure you add this button in XML

        // Button actions
        btnAddProduct.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, ProductAddActivity.class)));
        btnViewProducts.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, ProductList.class)));
        btnViewOrders.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, AdminOrdersActivity.class)));
        btnViewDeliveries.setOnClickListener(v -> Toast.makeText(AdminDashboard.this, "Feature not implemented", Toast.LENGTH_SHORT).show());

        // Logout button action
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(AdminDashboard.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        // Clear any session data here if needed
                        Toast.makeText(AdminDashboard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminDashboard.this, Selection.class));
                        finish();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }
}
