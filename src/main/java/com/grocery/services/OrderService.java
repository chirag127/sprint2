package com.grocery.services;

import com.grocery.database.DatabaseConnection;
import com.grocery.models.Order;
import com.grocery.auth.AuthenticationManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Service Class
 * Handles all order-related operations including order history retrieval
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class OrderService {
    
    private AuthenticationManager authManager;
    
    public OrderService() {
        this.authManager = AuthenticationManager.getInstance();
    }
    
    /**
     * Get customer order details (US004)
     * Retrieves order history for the authenticated customer
     */
    public void getCustomerOrderDetails() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         CUSTOMER ORDER HISTORY");
        System.out.println("=".repeat(50));
        
        // Check if customer is authenticated
        if (!authManager.isCustomer()) {
            System.out.println("❌ You must be logged in as a customer to view order history.");
            return;
        }
        
        String customerId = authManager.getCurrentCustomerId();
        String customerName = authManager.getCurrentSession().getCustomerName();
        
        try {
            List<Order> orders = getOrdersByCustomerId(customerId);
            
            if (orders.isEmpty()) {
                System.out.println("\n📦 No orders found for your account.");
                System.out.println("Start shopping to see your order history here!");
            } else {
                System.out.println("\nCustomer ID: " + customerId);
                System.out.println("Customer Name: " + (customerName != null ? customerName : "N/A"));
                System.out.println("\n" + "=".repeat(80));
                System.out.println("                    ORDER HISTORY");
                System.out.println("=".repeat(80));
                
                double totalSpent = 0.0;
                int totalItems = 0;
                
                for (int i = 0; i < orders.size(); i++) {
                    Order order = orders.get(i);
                    System.out.println("\n--- Order " + (i + 1) + " ---");
                    System.out.println(order.getOrderHistoryDisplay());
                    
                    totalSpent += order.getOrderAmount();
                    totalItems += order.getQuantityOrdered();
                }
                
                System.out.println("=".repeat(80));
                System.out.println("SUMMARY:");
                System.out.println("Total Orders: " + orders.size());
                System.out.println("Total Items Ordered: " + totalItems);
                System.out.println("Total Amount Spent: " + String.format("$%.2f", totalSpent));
                System.out.println("Average Order Value: " + String.format("$%.2f", totalSpent / orders.size()));
                
                // Show recent activity
                long recentOrders = orders.stream()
                    .mapToLong(Order::getDaysSinceOrder)
                    .filter(days -> days <= 30)
                    .count();
                
                if (recentOrders > 0) {
                    System.out.println("Recent Activity: " + recentOrders + " orders in the last 30 days");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error retrieving order history: " + e.getMessage());
        }
    }
    
    /**
     * Get customer order details by customer ID (Admin function)
     * Allows admin to view any customer's order history
     * @param customerId Customer ID to search for
     */
    public void getCustomerOrderDetailsByAdmin(String customerId) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("      CUSTOMER ORDER DETAILS (ADMIN)");
        System.out.println("=".repeat(50));
        
        // Check if admin is authenticated
        if (!authManager.isAdmin()) {
            System.out.println("❌ Access denied. Admin privileges required.");
            return;
        }
        
        try {
            // Validate customer exists
            if (!customerExists(customerId)) {
                System.out.println("❌ Customer ID not found: " + customerId);
                return;
            }
            
            List<Order> orders = getOrdersByCustomerId(customerId);
            String customerName = getCustomerNameById(customerId);
            
            if (orders.isEmpty()) {
                System.out.println("\n📦 No orders found for Customer ID: " + customerId);
                System.out.println("Customer Name: " + (customerName != null ? customerName : "N/A"));
            } else {
                System.out.println("\nCustomer ID: " + customerId);
                System.out.println("Customer Name: " + (customerName != null ? customerName : "N/A"));
                System.out.println("\n" + "=".repeat(80));
                System.out.println("                    ORDER DETAILS");
                System.out.println("=".repeat(80));
                
                double totalSpent = 0.0;
                int totalItems = 0;
                
                for (int i = 0; i < orders.size(); i++) {
                    Order order = orders.get(i);
                    System.out.println("\n--- Order " + (i + 1) + " ---");
                    System.out.println(order.getAdminOrderDetails());
                    
                    totalSpent += order.getOrderAmount();
                    totalItems += order.getQuantityOrdered();
                }
                
                System.out.println("=".repeat(80));
                System.out.println("CUSTOMER SUMMARY:");
                System.out.println("Total Orders: " + orders.size());
                System.out.println("Total Items Ordered: " + totalItems);
                System.out.println("Total Amount Spent: " + String.format("$%.2f", totalSpent));
                System.out.println("Average Order Value: " + String.format("$%.2f", totalSpent / orders.size()));
                
                // Customer value analysis
                if (totalSpent > 1000) {
                    System.out.println("Customer Tier: Premium Customer 🌟");
                } else if (totalSpent > 500) {
                    System.out.println("Customer Tier: Valued Customer ⭐");
                } else {
                    System.out.println("Customer Tier: Regular Customer");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error retrieving customer order details: " + e.getMessage());
        }
    }
    
    /**
     * Create a new order (for future enhancement)
     * @param customerId Customer ID
     * @param productId Product ID
     * @param quantity Quantity to order
     * @param unitPrice Unit price of the product
     * @return true if order created successfully, false otherwise
     */
    public boolean createOrder(String customerId, int productId, int quantity, double unitPrice) {
        try {
            double orderAmount = quantity * unitPrice;
            Order order = new Order(customerId, productId, orderAmount, quantity);
            
            String query = """
                INSERT INTO orders (customer_id, product_id, order_amount, quantity_ordered, order_date) 
                VALUES (?, ?, ?, ?, ?)
            """;
            
            int rowsAffected = DatabaseConnection.executeUpdate(query,
                order.getCustomerId(),
                order.getProductId(),
                order.getOrderAmount(),
                order.getQuantityOrdered(),
                Timestamp.valueOf(order.getOrderDate())
            );
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get all orders (Admin function)
     * @return List of all orders in the system
     */
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        
        try {
            String query = """
                SELECT o.order_id, o.customer_id, c.full_name as customer_name, 
                       o.product_id, p.product_name, o.order_date, o.order_amount, o.quantity_ordered
                FROM orders o
                LEFT JOIN customers c ON o.customer_id = c.customer_id
                LEFT JOIN products p ON o.product_id = p.product_id
                ORDER BY o.order_date DESC
            """;
            
            ResultSet rs = DatabaseConnection.executeQuery(query);
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setProductId(rs.getInt("product_id"));
                order.setProductName(rs.getString("product_name"));
                
                Timestamp timestamp = rs.getTimestamp("order_date");
                if (timestamp != null) {
                    order.setOrderDate(timestamp.toLocalDateTime());
                }
                
                order.setOrderAmount(rs.getDouble("order_amount"));
                order.setQuantityOrdered(rs.getInt("quantity_ordered"));
                
                orders.add(order);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    // Private helper methods
    
    private List<Order> getOrdersByCustomerId(String customerId) {
        List<Order> orders = new ArrayList<>();
        
        try {
            String query = """
                SELECT o.order_id, o.customer_id, c.full_name as customer_name, 
                       o.product_id, p.product_name, o.order_date, o.order_amount, o.quantity_ordered
                FROM orders o
                LEFT JOIN customers c ON o.customer_id = c.customer_id
                LEFT JOIN products p ON o.product_id = p.product_id
                WHERE o.customer_id = ?
                ORDER BY o.order_date DESC
            """;
            
            ResultSet rs = DatabaseConnection.executeQuery(query, customerId);
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomerId(rs.getString("customer_id"));
                order.setCustomerName(rs.getString("customer_name"));
                order.setProductId(rs.getInt("product_id"));
                order.setProductName(rs.getString("product_name"));
                
                Timestamp timestamp = rs.getTimestamp("order_date");
                if (timestamp != null) {
                    order.setOrderDate(timestamp.toLocalDateTime());
                }
                
                order.setOrderAmount(rs.getDouble("order_amount"));
                order.setQuantityOrdered(rs.getInt("quantity_ordered"));
                
                orders.add(order);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error retrieving orders for customer " + customerId + ": " + e.getMessage());
        }
        
        return orders;
    }
    
    private boolean customerExists(String customerId) {
        try {
            String query = "SELECT COUNT(*) FROM customers WHERE customer_id = ?";
            ResultSet rs = DatabaseConnection.executeQuery(query, customerId);
            
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                return count > 0;
            }
            
            rs.close();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error checking customer existence: " + e.getMessage());
            return false;
        }
    }
    
    private String getCustomerNameById(String customerId) {
        try {
            String query = "SELECT full_name FROM customers WHERE customer_id = ?";
            ResultSet rs = DatabaseConnection.executeQuery(query, customerId);
            
            if (rs.next()) {
                String name = rs.getString("full_name");
                rs.close();
                return name;
            }
            
            rs.close();
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error retrieving customer name: " + e.getMessage());
            return null;
        }
    }
}
