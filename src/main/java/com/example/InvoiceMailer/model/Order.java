package com.example.InvoiceMailer.model;

public class Order {
    private final String name;
    private final String description;
    private final int quantity;
    private final double price;
    private final double priceWithVAT;


    public Order(final String name, final String description, final int quantity, final double price) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.priceWithVAT = price * 1.23;
    }

    public Order(final OrderEntity order) {
        this.name = order.getProductName();
        this.description = order.getProductDescription();
        this.quantity = order.getQuantity();
        this.price = order.getPrice();
        this.priceWithVAT = price * 1.23;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public double getPriceWithVAT() {
        return priceWithVAT;
    }

    public double getVAT() {
        return priceWithVAT - price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
