package com.example.pizzamania;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UserRegister extends AppCompatActivity {

    private EditText etName, etEmail, etAddress, etPassword, etConfirmPassword, etPhone;
    private Button btnRegister;
    private SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_regester);

        // Adjust padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SQLite helper
        sqliteHelper = new SqliteHelper(this);

        // Initialize views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Register button click
        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(name)) {
            etName.setError("Enter your name");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter your email");
            return;
        }
        // Email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email address");
            return;
        }
        if (TextUtils.isEmpty(address)) {
            etAddress.setError("Enter your address");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Enter your phone number");
            return;
        }
        // Phone validation (10 digits)
        if (!phone.matches("^[0-9]{10}$")) {
            etPhone.setError("Enter a valid 10-digit phone number");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter a password");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Insert into database
        SQLiteDatabase db = sqliteHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("address", address);
        values.put("phone", phone);

        long result = db.insert("customer", null, values);
        if (result != -1) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        } else {
            Toast.makeText(this, "Registration failed! Email may already exist.", Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
