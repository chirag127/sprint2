package com.grocery.models;

import java.time.LocalDateTime;

/**
 * Customer Model Class
 * Represents a customer entity in the grocery ordering system
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class Customer {
    
    private String customerId;
    private String fullName;
    private String email;
    private String password;
    private String address;
    private String contactNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Default constructor
     */
    public Customer() {
    }
    
    /**
     * Constructor with all fields
     * @param customerId Customer ID
     * @param fullName Full name
     * @param email Email address
     * @param password Password (hashed)
     * @param address Address
     * @param contactNumber Contact number
     */
    public Customer(String customerId, String fullName, String email, String password, 
                   String address, String contactNumber) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.contactNumber = contactNumber;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Constructor without customer ID (for new registrations)
     * @param fullName Full name
     * @param email Email address
     * @param password Password (hashed)
     * @param address Address
     * @param contactNumber Contact number
     */
    public Customer(String fullName, String email, String password, String address, String contactNumber) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.address = address;
        this.contactNumber = contactNumber;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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
     * Get masked password for display
     * @return Masked password
     */
    public String getMaskedPassword() {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "*".repeat(Math.min(password.length(), 12));
    }
    
    /**
     * Get customer display information
     * @return Formatted customer information
     */
    public String getDisplayInfo() {
        return String.format("ID: %s | Name: %s | Email: %s | Phone: %s", 
                customerId, fullName, email, contactNumber);
    }
    
    /**
     * Get customer summary for admin view
     * @return Customer summary with masked password
     */
    public String getAdminSummary() {
        return String.format("Customer ID: %s\nName: %s\nEmail: %s\nPassword: %s\nAddress: %s\nContact: %s\nCreated: %s", 
                customerId, fullName, email, getMaskedPassword(), address, contactNumber, 
                createdAt != null ? createdAt.toString() : "N/A");
    }
    
    /**
     * Validate customer data
     * @return true if all data is valid, false otherwise
     */
    public boolean isValid() {
        return fullName != null && !fullName.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               address != null && !address.trim().isEmpty() &&
               contactNumber != null && !contactNumber.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("Customer{id='%s', name='%s', email='%s', phone='%s'}", 
                customerId, fullName, email, contactNumber);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Customer customer = (Customer) obj;
        return customerId != null ? customerId.equals(customer.customerId) : customer.customerId == null;
    }
    
    @Override
    public int hashCode() {
        return customerId != null ? customerId.hashCode() : 0;
    }
}
