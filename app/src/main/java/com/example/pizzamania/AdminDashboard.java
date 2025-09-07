package com.example.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashboard extends AppCompatActivity {

    Button btnAddProduct, btnViewProducts, btnViewOrders, btnUpdateDelivery, btnViewDeliveries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard); // layout file must be saved correctly

        // Bind views
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnViewProducts = findViewById(R.id.btnViewProducts);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        btnUpdateDelivery = findViewById(R.id.btnUpdateDelivery);
        btnViewDeliveries = findViewById(R.id.btnViewDeliveries);

        // Button actions
        btnAddProduct.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, ProductAddActivity.class)));
        btnViewProducts.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, ProductList.class)));
        btnViewOrders.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, AdminOrdersActivity.class)));
//        btnUpdateDelivery.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, UpdateDeliveryActivity.class)));
//        btnViewDeliveries.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, ViewDeliveriesActivity.class)));
    }
}