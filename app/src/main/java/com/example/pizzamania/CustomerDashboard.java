package com.example.pizzamania;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CustomerDashboard extends AppCompatActivity {

    // Product RecyclerView
    private RecyclerView recyclerViewProducts;
    private ArrayList<String> productNames, productDescriptions;
    private ArrayList<byte[]> productImages;
    private ArrayList<Double> smallPrices, mediumPrices, largePrices;
    private ProductAdapter adapter;

    // For search filtering
    private ArrayList<String> allProductNames, allProductDescriptions;
    private ArrayList<byte[]> allProductImages;
    private ArrayList<Double> allSmallPrices, allMediumPrices, allLargePrices;

    // Banner RecyclerView
    private RecyclerView bannerRecyclerView;
    private int currentBannerPosition = 0;
    private Handler bannerHandler = new Handler();
    private Runnable bannerRunnable;

    // Branch Spinner
    private Spinner branchSpinner;

    // Database helper
    private SqliteHelper dbHelper;

    // Search bar
    private EditText searchBar;

    // Bottom navigation
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dashboard);

        // Initialize views
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));

        bannerRecyclerView = findViewById(R.id.bannerRecyclerView);
        bannerRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        branchSpinner = findViewById(R.id.customerBranchSpinner);
        searchBar = findViewById(R.id.searchBar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        dbHelper = new SqliteHelper(this);

        // Setup banners
        setupBanners();

        // Setup branch spinner
        setupBranchSpinner();

        // Setup search bar
        setupSearchBar();

        // Setup bottom navigation
        setupBottomNavigation();
    }

    // Setup banners with auto-scroll
    private void setupBanners() {
        List<Banner> bannerList = new ArrayList<>();
        bannerList.add(new Banner("50% OFF!", R.drawable.offer2));
        bannerList.add(new Banner("Buy 1 Get 1 Free", R.drawable.offer1));
        bannerList.add(new Banner("Buy 1 Get 1 Free", R.drawable.offer3));
        bannerList.add(new Banner("Buy 1 Get 1 Free", R.drawable.offer4));
        bannerList.add(new Banner("Buy 1 Get 1 Free", R.drawable.offer5));
        bannerList.add(new Banner("Buy 1 Get 1 Free", R.drawable.offer6));

        BannerAdapter bannerAdapter = new BannerAdapter(this, bannerList);
        bannerRecyclerView.setAdapter(bannerAdapter);

        // Auto-scroll banners every 5 seconds
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerAdapter.getItemCount() == 0) return;

                currentBannerPosition++;
                if (currentBannerPosition >= bannerAdapter.getItemCount()) {
                    currentBannerPosition = 0;
                }
                bannerRecyclerView.smoothScrollToPosition(currentBannerPosition);

                bannerHandler.postDelayed(this, 4000);
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerHandler != null && bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
        }
    }

    // Setup branch spinner
    private void setupBranchSpinner() {
        ArrayAdapter<String> branchAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Colombo", "Galle"});
        branchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branchSpinner.setAdapter(branchAdapter);

        // Load products for initial branch
        loadProductsByBranch(branchSpinner.getSelectedItem().toString());
        copyAllProductsForSearch();

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String branch = branchSpinner.getSelectedItem().toString();
                loadProductsByBranch(branch);
                copyAllProductsForSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // Setup search bar
    private void setupSearchBar() {
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
    }

    // Setup bottom navigation
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true; // Already on dashboard
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(CustomerDashboard.this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(CustomerDashboard.this, Orders.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(CustomerDashboard.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    // Load products by branch
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

        adapter = new ProductAdapter(
                this,
                productNames,
                productDescriptions,
                productImages,
                smallPrices,
                mediumPrices,
                largePrices
        );
        recyclerViewProducts.setAdapter(adapter);

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

    // Copy products for search filtering
    private void copyAllProductsForSearch() {
        allProductNames = new ArrayList<>(productNames);
        allProductDescriptions = new ArrayList<>(productDescriptions);
        allProductImages = new ArrayList<>(productImages);
        allSmallPrices = new ArrayList<>(smallPrices);
        allMediumPrices = new ArrayList<>(mediumPrices);
        allLargePrices = new ArrayList<>(largePrices);
    }

    // Filter products based on search query
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
