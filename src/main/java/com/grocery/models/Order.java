package com.grocery.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Order Model Class
 * Represents an order entity in the grocery ordering system
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class Order {
    
    private int orderId;
    private String customerId;
    private String customerName;
    private int productId;
    private String productName;
    private LocalDateTime orderDate;
    private double orderAmount;
    private int quantityOrdered;
    
    /**
     * Default constructor
     */
    public Order() {
    }
    
    /**
     * Constructor with all fields
     * @param orderId Order ID
     * @param customerId Customer ID
     * @param customerName Customer name
     * @param productId Product ID
     * @param productName Product name
     * @param orderDate Order date
     * @param orderAmount Order amount
     * @param quantityOrdered Quantity ordered
     */
    public Order(int orderId, String customerId, String customerName, int productId, 
                String productName, LocalDateTime orderDate, double orderAmount, int quantityOrdered) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.productId = productId;
        this.productName = productName;
        this.orderDate = orderDate;
        this.orderAmount = orderAmount;
        this.quantityOrdered = quantityOrdered;
    }
    
    /**
     * Constructor for new orders
     * @param customerId Customer ID
     * @param productId Product ID
     * @param orderAmount Order amount
     * @param quantityOrdered Quantity ordered
     */
    public Order(String customerId, int productId, double orderAmount, int quantityOrdered) {
        this.customerId = customerId;
        this.productId = productId;
        this.orderAmount = orderAmount;
        this.quantityOrdered = quantityOrdered;
        this.orderDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
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
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public double getOrderAmount() {
        return orderAmount;
    }
    
    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }
    
    public int getQuantityOrdered() {
        return quantityOrdered;
    }
    
    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }
    
    /**
     * Get formatted order amount
     * @return Order amount formatted as currency
     */
    public String getFormattedOrderAmount() {
        return String.format("$%.2f", orderAmount);
    }
    
    /**
     * Get formatted order date
     * @return Order date formatted as string
     */
    public String getFormattedOrderDate() {
        if (orderDate == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderDate.format(formatter);
    }
    
    /**
     * Get order summary for customer view
     * @return Formatted order summary
     */
    public String getCustomerOrderSummary() {
        return String.format("Order #%d | %s | Qty: %d | Amount: %s | Date: %s", 
                orderId, productName != null ? productName : "Product ID: " + productId, 
                quantityOrdered, getFormattedOrderAmount(), getFormattedOrderDate());
    }
    
    /**
     * Get detailed order information for admin view
     * @return Detailed order information
     */
    public String getAdminOrderDetails() {
        return String.format("""
                Order ID: %d
                Customer ID: %s
                Customer Name: %s
                Product ID: %d
                Product Name: %s
                Quantity Ordered: %d
                Order Amount: %s
                Order Date: %s
                """, 
                orderId, customerId, customerName != null ? customerName : "N/A", 
                productId, productName != null ? productName : "N/A", 
                quantityOrdered, getFormattedOrderAmount(), getFormattedOrderDate());
    }
    
    /**
     * Get order display for customer order history
     * @return Customer-friendly order display
     */
    public String getOrderHistoryDisplay() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(orderId).append("\n");
        sb.append("Product: ").append(productName != null ? productName : "Product ID: " + productId).append("\n");
        sb.append("Quantity: ").append(quantityOrdered).append("\n");
        sb.append("Amount: ").append(getFormattedOrderAmount()).append("\n");
        sb.append("Date: ").append(getFormattedOrderDate()).append("\n");
        return sb.toString();
    }
    
    /**
     * Calculate unit price
     * @return Unit price per item
     */
    public double getUnitPrice() {
        if (quantityOrdered <= 0) {
            return 0.0;
        }
        return orderAmount / quantityOrdered;
    }
    
    /**
     * Get formatted unit price
     * @return Unit price formatted as currency
     */
    public String getFormattedUnitPrice() {
        return String.format("$%.2f", getUnitPrice());
    }
    
    /**
     * Check if order is valid
     * @return true if order data is valid, false otherwise
     */
    public boolean isValid() {
        return customerId != null && !customerId.trim().isEmpty() &&
               productId > 0 &&
               orderAmount > 0 &&
               quantityOrdered > 0 &&
               orderDate != null;
    }
    
    /**
     * Get order status based on date (for future enhancement)
     * @return Order status
     */
    public String getOrderStatus() {
        if (orderDate == null) {
            return "Unknown";
        }
        
        // For now, all orders are considered "Completed"
        // This can be enhanced to include different statuses like Pending, Processing, etc.
        return "Completed";
    }
    
    /**
     * Check if order was placed today
     * @return true if order was placed today, false otherwise
     */
    public boolean isOrderedToday() {
        if (orderDate == null) {
            return false;
        }
        return orderDate.toLocalDate().equals(LocalDateTime.now().toLocalDate());
    }
    
    /**
     * Get days since order was placed
     * @return Number of days since order
     */
    public long getDaysSinceOrder() {
        if (orderDate == null) {
            return 0;
        }
        return java.time.Duration.between(orderDate, LocalDateTime.now()).toDays();
    }
    
    @Override
    public String toString() {
        return String.format("Order{id=%d, customerId='%s', productId=%d, amount=%.2f, quantity=%d, date=%s}", 
                orderId, customerId, productId, orderAmount, quantityOrdered, 
                orderDate != null ? orderDate.toString() : "null");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Order order = (Order) obj;
        return orderId == order.orderId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(orderId);
    }
}
