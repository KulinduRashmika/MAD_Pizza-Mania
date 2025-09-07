package com.example.pizzamania;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqliteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "PizzaMania.db";
    private static final int DB_VERSION = 6;

    public SqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // ---------------- Customers ----------------
        String customerTable = "CREATE TABLE customer (" +
                "customer_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_name TEXT, " +
                "email TEXT UNIQUE, " +
                "password TEXT, " +
                "address TEXT, " +
                "phone TEXT)";
        db.execSQL(customerTable);

        // ---------------- Admin ----------------
        String adminTable = "CREATE TABLE admin (" +
                "admin_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT)";
        db.execSQL(adminTable);

        // ---------------- Products ----------------
        String productTable = "CREATE TABLE product (" +
                "product_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "description TEXT, " +
                "smallPrice REAL, " +
                "mediumPrice REAL, " +
                "largePrice REAL, " +
                "image BLOB, " +
                "branch TEXT)";
        db.execSQL(productTable);

        // ---------------- Cart ----------------
        String cartTable = "CREATE TABLE cart (" +
                "cart_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_ID INTEGER, " +
                "product_ID INTEGER, " +
                "quantity INTEGER, " +
                "FOREIGN KEY(customer_ID) REFERENCES customer(customer_ID), " +
                "FOREIGN KEY(product_ID) REFERENCES product(product_ID))";
        db.execSQL(cartTable);

        // ---------------- Orders ----------------
        String ordersTable = "CREATE TABLE orders (" +
                "order_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_ID INTEGER, " +
                "items TEXT, " +
                "totalPrice REAL, " +
                "orderDate TEXT, " +
                "status TEXT, " +
                "FOREIGN KEY(customer_ID) REFERENCES customer(customer_ID))";
        db.execSQL(ordersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS customer");
        db.execSQL("DROP TABLE IF EXISTS admin");
        db.execSQL("DROP TABLE IF EXISTS product");
        db.execSQL("DROP TABLE IF EXISTS cart");
        db.execSQL("DROP TABLE IF EXISTS orders");
        onCreate(db);
    }

    // ---------------- Customers ----------------
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM customer WHERE email=? AND password=?",
                new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public Cursor getCustomerByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM customer WHERE email=?", new String[]{email});
    }

    public long insertCustomer(String name, String email, String password, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("address", address);
        values.put("phone", phone);
        long result = db.insert("customer", null, values);
        if (result == -1) Log.e("DB_ERROR", "Failed to insert customer: " + email);
        return result;
    }

    public int updateCustomer(int customerId, String name, String email, String address, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_name", name);
        values.put("email", email);
        values.put("address", address);
        values.put("phone", phone);
        return db.update("customer", values, "customer_ID=?", new String[]{String.valueOf(customerId)});
    }

    // ---------------- Admin ----------------
    public boolean checkAdmin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM admin WHERE username=? AND password=?",
                new String[]{username, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // ---------------- Products ----------------
    public long insertProduct(String name, String description, double smallPrice, double mediumPrice,
                              double largePrice, byte[] image, String branch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("smallPrice", smallPrice);
        values.put("mediumPrice", mediumPrice);
        values.put("largePrice", largePrice);
        values.put("image", image);
        values.put("branch", branch);
        long result = db.insert("product", null, values);
        if (result == -1) Log.e("DB_ERROR", "Failed to insert product: " + name);
        else Log.d("DB_SUCCESS", "Inserted product [" + name + "] ID: " + result);
        return result;
    }

    public Cursor getProductsByBranch(String branch) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM product WHERE branch=?", new String[]{branch});
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM product", null);
    }

    public Cursor getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM product WHERE product_ID=?", new String[]{String.valueOf(id)});
    }

    public int updateProduct(int id, String name, String description,
                             double smallPrice, double mediumPrice, double largePrice,
                             byte[] image, String branch) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("description", description);
        values.put("smallPrice", smallPrice);
        values.put("mediumPrice", mediumPrice);
        values.put("largePrice", largePrice);
        values.put("image", image);
        values.put("branch", branch);
        return db.update("product", values, "product_ID=?", new String[]{String.valueOf(id)});
    }

    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("product", "product_ID=?", new String[]{String.valueOf(id)});
    }

    // ---------------- Cart ----------------
    public long addToCart(int customerId, int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_ID", customerId);
        values.put("product_ID", productId);
        values.put("quantity", quantity);
        return db.insert("cart", null, values);
    }

    public Cursor getCartItems(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT c.cart_ID, p.name, p.smallPrice, c.quantity, p.image " +
                        "FROM cart c JOIN product p ON c.product_ID = p.product_ID " +
                        "WHERE c.customer_ID = ?",
                new String[]{String.valueOf(customerId)}
        );
    }

    public int updateCartItemQuantity(int cartId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        return db.update("cart", values, "cart_ID=?", new String[]{String.valueOf(cartId)});
    }

    public void clearCart(int customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "customer_ID=?", new String[]{String.valueOf(customerId)});
    }

    public int deleteCartItem(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("cart", "cart_ID=?", new String[]{String.valueOf(cartId)});
    }

    // ---------------- Orders ----------------
    public long insertOrder(int customerId, String items, double totalPrice, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_ID", customerId);
        values.put("items", items);
        values.put("totalPrice", totalPrice);
        values.put("orderDate", date);
        values.put("status", status);
        return db.insert("orders", null, values);
    }

    public Cursor getOrdersByCustomer(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM orders WHERE customer_ID=? ORDER BY orderDate DESC",
                new String[]{String.valueOf(customerId)});
    }

    public int updateOrderStatus(long orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        return db.update("orders", values, "order_ID=?", new String[]{String.valueOf(orderId)});
    }
    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT o.order_ID AS _id, c.customer_name, o.items, o.totalPrice, o.orderDate, o.status " +
                        "FROM orders o " +
                        "JOIN customer c ON o.customer_ID = c.customer_ID " +
                        "ORDER BY o.orderDate DESC",
                null
        );
    }

}