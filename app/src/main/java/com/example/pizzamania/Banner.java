package com.example.pizzamania;

public class Banner {
    private String title;
    private int imageResId;

    public Banner(String title, int imageResId) {
        this.title = title;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }
}
