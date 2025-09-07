package com.example.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLogin extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin;

    // demo credentials (change to secure storage later)
    private final String ADMIN_EMAIL = "admin";
    private final String ADMIN_PASSWORD = "12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String e = editEmail.getText().toString().trim();
            String p = editPassword.getText().toString().trim();

            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (e.equals(ADMIN_EMAIL) && p.equals(ADMIN_PASSWORD)) {
                // open product add screen
                startActivity(new Intent(AdminLogin.this, AdminDashboard.class));
                finish();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
