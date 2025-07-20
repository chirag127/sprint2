package com.grocery.models;

import java.time.LocalDateTime;

/**
 * Product Model Class
 * Represents a product entity in the grocery ordering system
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class Product {
    
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private int reserved;
    private String customerId; // Customer who reserved the product
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor
     */
    public Product() {
    }
    
    /**
     * Constructor with all fields
     * @param productId Product ID
     * @param productName Product name
     * @param price Product price
     * @param quantity Available quantity
     * @param reserved Reserved quantity
     * @param customerId Customer ID who reserved
     */
    public Product(int productId, String productName, double price, int quantity, 
                  int reserved, String customerId) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.reserved = reserved;
        this.customerId = customerId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor without product ID (for new products)
     * @param productName Product name
     * @param price Product price
     * @param quantity Available quantity
     */
    public Product(String productName, double price, int quantity) {
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.reserved = 0;
        this.customerId = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = LocalDateTime.now();
    }
    
    public int getReserved() {
        return reserved;
    }
    
    public void setReserved(int reserved) {
        this.reserved = reserved;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Get available quantity (total - reserved)
     * @return Available quantity
     */
    public int getAvailableQuantity() {
        return Math.max(0, quantity - reserved);
    }
    
    /**
     * Check if product is in stock
     * @return true if available quantity > 0, false otherwise
     */
    public boolean isInStock() {
        return getAvailableQuantity() > 0;
    }
    
    /**
     * Check if product is reserved
     * @return true if reserved > 0, false otherwise
     */
    public boolean isReserved() {
        return reserved > 0;
    }
    
    /**
     * Get formatted price string
     * @return Price formatted as currency
     */
    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }
    
    /**
     * Get product display information for customers
     * @return Formatted product information
     */
    public String getCustomerDisplayInfo() {
        return String.format("ID: %d | %s | Price: %s | Available: %d", 
                productId, productName, getFormattedPrice(), getAvailableQuantity());
    }
    
    /**
     * Get product display information for admin
     * @return Detailed product information for admin
     */
    public String getAdminDisplayInfo() {
        return String.format("Product ID: %d\nName: %s\nPrice: %s\nTotal Quantity: %d\nReserved: %d\nAvailable: %d\nReserved by Customer: %s\nCreated: %s", 
                productId, productName, getFormattedPrice(), quantity, reserved, 
                getAvailableQuantity(), (customerId != null ? customerId : "None"), 
                createdAt != null ? createdAt.toString() : "N/A");
    }
    
    /**
     * Get product summary for search results
     * @return Product summary
     */
    public String getSearchSummary() {
        String status = isInStock() ? "In Stock" : "Out of Stock";
        if (isReserved()) {
            status += " (Partially Reserved)";
        }
        
        return String.format("%s - %s - %s [%s]", 
                productName, getFormattedPrice(), status, getAvailableQuantity() + " available");
    }
    
    /**
     * Reserve quantity for a customer
     * @param reserveQuantity Quantity to reserve
     * @param customerId Customer ID
     * @return true if reservation successful, false otherwise
     */
    public boolean reserveQuantity(int reserveQuantity, String customerId) {
        if (reserveQuantity <= 0 || reserveQuantity > getAvailableQuantity()) {
            return false;
        }
        
        this.reserved += reserveQuantity;
        this.customerId = customerId;
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Release reserved quantity
     * @param releaseQuantity Quantity to release
     * @return true if release successful, false otherwise
     */
    public boolean releaseReservedQuantity(int releaseQuantity) {
        if (releaseQuantity <= 0 || releaseQuantity > reserved) {
            return false;
        }
        
        this.reserved -= releaseQuantity;
        if (this.reserved == 0) {
            this.customerId = null;
        }
        this.updatedAt = LocalDateTime.now();
        return true;
    }
    
    /**
     * Validate product data
     * @return true if all data is valid, false otherwise
     */
    public boolean isValid() {
        return productName != null && !productName.trim().isEmpty() &&
               price > 0 &&
               quantity >= 0 &&
               reserved >= 0 &&
               reserved <= quantity;
    }
    
    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.2f, quantity=%d, reserved=%d}", 
                productId, productName, price, quantity, reserved);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Product product = (Product) obj;
        return productId == product.productId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }
}
