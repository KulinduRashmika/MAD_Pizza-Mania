package com.example.pizzamania;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class CustomerDashboard extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> productNames, productDescriptions;
    ArrayList<byte[]> productImages;
    ArrayList<Double> smallPrices, mediumPrices, largePrices;
    SqliteHelper dbHelper;
    Spinner branchSpinner; // add spinner reference

    // For search filtering
    ArrayList<String> allProductNames, allProductDescriptions;
    ArrayList<byte[]> allProductImages;
    ArrayList<Double> allSmallPrices, allMediumPrices, allLargePrices;
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        RecyclerView bannerRecyclerView = findViewById(R.id.bannerRecyclerView);
        bannerRecyclerView.setLayoutManager(
                new GridLayoutManager(this, 1, RecyclerView.HORIZONTAL, false)
        );

        ArrayList<Banner> banners = new ArrayList<>();
        banners.add(new Banner("50% OFF on Pizzas!", R.drawable.offer1));
        banners.add(new Banner("Buy 1 Get 1 Free!", R.drawable.offer2));
        banners.add(new Banner("Free Drink with Every Order", R.drawable.offer3));

        BannerAdapter bannerAdapter = new BannerAdapter(this, banners);
        bannerRecyclerView.setAdapter(bannerAdapter);


        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        dbHelper = new SqliteHelper(this);

        branchSpinner = findViewById(R.id.customerBranchSpinner); // initialize spinner

        // Setup branch spinner
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Colombo", "Galle"});
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        // Load products for the initially selected branch
        loadProductsByBranch(branchSpinner.getSelectedItem().toString());

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String branch = branchSpinner.getSelectedItem().toString();
                loadProductsByBranch(branch);
                // Also update search reference
                copyAllProductsForSearch();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        EditText searchBar = findViewById(R.id.searchBar);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Bottom navigation bar logic
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    // Already in home (dashboard)
                    return true;
                } else if (itemId == R.id.nav_cart) {
                    Intent cartIntent = new Intent(CustomerDashboard.this, CartActivity.class);
                    startActivity(cartIntent);
                    return true;
                } else if (itemId == R.id.nav_orders) {
                    Intent ordersIntent = new Intent(CustomerDashboard.this, Orders.class);
                    startActivity(ordersIntent);
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    Intent profileIntent = new Intent(CustomerDashboard.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    return true;
                }

                return false;
            }
        });
    }

    // Load products for selected branch
    private void loadProductsByBranch(String branch) {
        productNames = new ArrayList<>();
        productDescriptions = new ArrayList<>();
        productImages = new ArrayList<>();
        smallPrices = new ArrayList<>();
        mediumPrices = new ArrayList<>();
        largePrices = new ArrayList<>();

        Cursor cursor = dbHelper.getProductsByBranch(branch);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                productNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                productDescriptions.add(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                productImages.add(cursor.getBlob(cursor.getColumnIndexOrThrow("image")));
                smallPrices.add(cursor.getDouble(cursor.getColumnIndexOrThrow("smallPrice")));
                mediumPrices.add(cursor.getDouble(cursor.getColumnIndexOrThrow("mediumPrice")));
                largePrices.add(cursor.getDouble(cursor.getColumnIndexOrThrow("largePrice")));
            } while (cursor.moveToNext());
            cursor.close();
        }

        // Always create a new adapter with the latest lists
        adapter = new ProductAdapter(
                this,
                productNames,
                productDescriptions,
                productImages,
                smallPrices,
                mediumPrices,
                largePrices
        );
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(CustomerDashboard.this, ProductDetailActivity.class);
            intent.putExtra("name", productNames.get(position));
            intent.putExtra("description", productDescriptions.get(position));
            intent.putExtra("smallPrice", smallPrices.get(position));
            intent.putExtra("mediumPrice", mediumPrices.get(position));
            intent.putExtra("largePrice", largePrices.get(position));
            intent.putExtra("image", productImages.get(position));
            startActivity(intent);
        });
    }

    // Copy all products for search reference (for current branch)
    private void copyAllProductsForSearch() {
        allProductNames = new ArrayList<>(productNames);
        allProductDescriptions = new ArrayList<>(productDescriptions);
        allProductImages = new ArrayList<>(productImages);
        allSmallPrices = new ArrayList<>(smallPrices);
        allMediumPrices = new ArrayList<>(mediumPrices);
        allLargePrices = new ArrayList<>(largePrices);
    }

    private void filterProducts(String query) {
        productNames.clear();
        productDescriptions.clear();
        productImages.clear();
        smallPrices.clear();
        mediumPrices.clear();
        largePrices.clear();

        if (query.isEmpty()) {
            productNames.addAll(allProductNames);
            productDescriptions.addAll(allProductDescriptions);
            productImages.addAll(allProductImages);
            smallPrices.addAll(allSmallPrices);
            mediumPrices.addAll(allMediumPrices);
            largePrices.addAll(allLargePrices);
        } else {
            String lowerQuery = query.toLowerCase();
            for (int i = 0; i < allProductNames.size(); i++) {
                if (allProductNames.get(i).toLowerCase().contains(lowerQuery) ||
                        allProductDescriptions.get(i).toLowerCase().contains(lowerQuery)) {
                    productNames.add(allProductNames.get(i));
                    productDescriptions.add(allProductDescriptions.get(i));
                    productImages.add(allProductImages.get(i));
                    smallPrices.add(allSmallPrices.get(i));
                    mediumPrices.add(allMediumPrices.get(i));
                    largePrices.add(allLargePrices.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
