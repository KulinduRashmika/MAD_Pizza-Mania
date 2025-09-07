package com.example.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Selection extends AppCompatActivity {

    private ImageButton adminButton, customerButton, toggleAdminButton;
    private View adminCard;  // Container for admin section
    private boolean isAdminVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection); // Make sure your XML is activity_selection.xml

        // Initialize buttons
        adminButton = findViewById(R.id.adminButton);
        customerButton = findViewById(R.id.customerButton);
        toggleAdminButton = findViewById(R.id.toggleAdminButton);
        adminCard = findViewById(R.id.adminCard); // The whole admin card (hidden by default)

        // Admin button click -> go to AdminLogin
        adminButton.setOnClickListener(v -> {
            Intent intent = new Intent(Selection.this, AdminLogin.class);
            startActivity(intent);
        });

        // Customer button click -> go to UserLogin
        customerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Selection.this, UserLogin.class);
            startActivity(intent);
        });

        // Toggle arrow button -> show/hide Admin
        toggleAdminButton.setOnClickListener(v -> toggleAdminSection());
    }

    private void toggleAdminSection() {
        if (!isAdminVisible) {
            // Show admin section with fade-in
            adminCard.setVisibility(View.VISIBLE);
            adminCard.setAlpha(0f);
            adminCard.animate().alpha(1f).setDuration(400).start();

            // Rotate arrow
            toggleAdminButton.animate().rotation(180f).setDuration(300).start();
            isAdminVisible = true;
        } else {
            // Hide admin section with fade-out
            adminCard.animate().alpha(0f).setDuration(400).withEndAction(() -> {
                adminCard.setVisibility(View.GONE);
            }).start();

            // Rotate arrow back
            toggleAdminButton.animate().rotation(0f).setDuration(300).start();
            isAdminVisible = false;
        }
    }
}
