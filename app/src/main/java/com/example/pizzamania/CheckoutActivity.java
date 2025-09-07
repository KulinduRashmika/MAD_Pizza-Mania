package com.example.pizzamania;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = "PayHere Demo";

    TextView txtCustomerName, txtCustomerPhone, txtCustomerAddress, txtTotalPrice;
    ListView listViewCheckout;
    Button btnPlaceOrder, btnCancel;

    CartAdapter adapter;
    SqliteHelper dbHelper;
    String sessionEmail;
    long currentOrderId = -1; // To track the last inserted order

    // PayHere launcher
    private final ActivityResultLauncher<Intent> payHereLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        Serializable serializable = data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (serializable instanceof PHResponse) {
                            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) serializable;
                            if (response.isSuccess()) {
                                Log.d(TAG, "✅ Payment Success: " + response.getData());
                                Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();

                                // Update order status -> Paid
                                if (currentOrderId != -1) {
                                    dbHelper.updateOrderStatus(currentOrderId, "Paid");
                                }

                                // Clear cart
                                CartManager.getInstance().clearCart();
                                adapter.notifyDataSetChanged();

                                // Go to Orders page
                                startActivity(new Intent(CheckoutActivity.this, Orders.class));
                                finish();
                            } else {
                                Log.d(TAG, "❌ Payment Failed: " + response);
                                Toast.makeText(this, "Payment Failed!", Toast.LENGTH_LONG).show();

                                if (currentOrderId != -1) {
                                    dbHelper.updateOrderStatus(currentOrderId, "Failed");
                                }
                            }
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "⚠ User Cancelled the payment", Toast.LENGTH_LONG).show();
                    if (currentOrderId != -1) {
                        dbHelper.updateOrderStatus(currentOrderId, "Cancelled");
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Bind Views
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCustomerPhone = findViewById(R.id.txtCustomerPhone);
        txtCustomerAddress = findViewById(R.id.txtCustomerAddress);
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        listViewCheckout = findViewById(R.id.listViewCheckout);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new SqliteHelper(this);

        // Get logged-in email from session
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        sessionEmail = sharedPreferences.getString("email", null);

        // Load customer info
        loadCustomerInfo(sessionEmail);

        // Show cart items in read-only mode
        adapter = new CartAdapter(this, CartManager.getInstance().getCartItems()) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                // Hide increase/decrease/remove buttons
                View btnIncrease = view.findViewById(R.id.btnIncrease);
                View btnDecrease = view.findViewById(R.id.btnDecrease);
                View btnRemove = view.findViewById(R.id.btnRemove);
                if (btnIncrease != null) btnIncrease.setVisibility(View.GONE);
                if (btnDecrease != null) btnDecrease.setVisibility(View.GONE);
                if (btnRemove != null) btnRemove.setVisibility(View.GONE);
                return view;
            }
        };
        listViewCheckout.setAdapter(adapter);

        // Show total price
        txtTotalPrice.setText("Total: Rs. " + CartManager.getInstance().getTotalPrice());

        // Place Order button
        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        // Cancel button
        btnCancel.setOnClickListener(v -> finish());
    }

    private void placeOrder() {
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            Toast.makeText(this, "Cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get customer ID
        Cursor cursor = dbHelper.getCustomerByEmail(sessionEmail);
        int customerId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            customerId = cursor.getInt(cursor.getColumnIndexOrThrow("customer_ID"));
            cursor.close();
        }

        // Build items summary
        StringBuilder itemsSummary = new StringBuilder();
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            itemsSummary.append(item.getName()).append(" x").append(item.getQuantity()).append(", ");
        }

        // Insert order into DB with Pending status
        double totalPrice = CartManager.getInstance().getTotalPrice();
        String date = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        currentOrderId = dbHelper.insertOrder(customerId, itemsSummary.toString(), totalPrice, date, "Pending");

        if (currentOrderId != -1) {
            Toast.makeText(this, "✅ Order saved successfully!", Toast.LENGTH_LONG).show();
            initiatePayment(totalPrice); // Proceed to PayHere
        } else {
            Toast.makeText(this, "❌ Failed to save order!", Toast.LENGTH_LONG).show();
        }
    }

    private void initiatePayment(double amount) {
        InitRequest req = new InitRequest();
        req.setMerchantId("1231912");       // Your Merchant ID
        req.setCurrency("LKR");
        req.setAmount(amount);
        req.setOrderId("ORDER-" + System.currentTimeMillis()); // Unique order ID
        req.setItemsDescription("PizzaMania Order");
        req.setCustom1("Customer Email: " + sessionEmail);

        // Customer details
        req.getCustomer().setFirstName("Pizza");
        req.getCustomer().setLastName("Customer");
        req.getCustomer().setEmail("demo@email.com");
        req.getCustomer().setPhone("+94740000000");
        req.getCustomer().getAddress().setAddress("Default Address");
        req.getCustomer().getAddress().setCity("Colombo");
        req.getCustomer().getAddress().setCountry("Sri Lanka");

        // Add cart items
        for (CartItem item : CartManager.getInstance().getCartItems()) {
            req.getItems().add(new Item(null, item.getName(), item.getQuantity(), item.getPrice()));
        }

        req.setNotifyUrl(" "); // Optional: backend listener

        Intent intent = new Intent(this, PHMainActivity.class);
        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
        payHereLauncher.launch(intent);
    }

    private void loadCustomerInfo(String email) {
        Cursor cursor = dbHelper.getCustomerByEmail(email);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("customer_name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));

            txtCustomerName.setText("Name: " + name);
            txtCustomerPhone.setText("Phone: " + phone);
            txtCustomerAddress.setText("Address: " + address);

            cursor.close();
        }
    }
}
