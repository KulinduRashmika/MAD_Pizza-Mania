package com.example.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {

    private Button btnAddProduct, btnViewProducts, btnViewOrders, btnShowMap, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard); // Make sure this layout exists

        // Bind views
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnViewProducts = findViewById(R.id.btnViewProducts);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnShowMap = findViewById(R.id.showMap); // Button in XML for showing map
        btnLogout = findViewById(R.id.btnLogout);

        // Button actions
        btnAddProduct.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboard.this, ProductAddActivity.class)));

        btnViewProducts.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboard.this, ProductList.class)));

        btnViewOrders.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboard.this, AdminOrdersActivity.class)));

        btnShowMap.setOnClickListener(v ->
                startActivity(new Intent(AdminDashboard.this, MapsActivity.class)));

        btnLogout.setOnClickListener(v -> new AlertDialog.Builder(AdminDashboard.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(AdminDashboard.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AdminDashboard.this, Selection.class));
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show());
    }
}
