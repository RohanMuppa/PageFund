package com.example.app1;

public class Book {
    private String title;
    private double price;

    public Book(String title, double price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() { return title; }
    public double getPrice() { return price; }

    public void setTitle(String title) { this.title = title; }
    public void setPrice(int price) { this.price = price; }
}

