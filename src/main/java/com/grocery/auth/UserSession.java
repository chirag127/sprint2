package com.grocery.auth;

import java.time.LocalDateTime;

/**
 * User Session Class
 * Represents an active user session with authentication details
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class UserSession {
    
    private String sessionToken;
    private String username;
    private String userType; // "ADMIN" or "CUSTOMER"
    private String customerId; // Only for customers
    private String customerName; // Only for customers
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    
    /**
     * Constructor for UserSession
     * @param sessionToken Unique session token
     * @param username Username or email
     * @param userType Type of user (ADMIN/CUSTOMER)
     * @param customerId Customer ID (null for admin)
     */
    public UserSession(String sessionToken, String username, String userType, String customerId) {
        this.sessionToken = sessionToken;
        this.username = username;
        this.userType = userType;
        this.customerId = customerId;
        this.loginTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    /**
     * Get session token
     * @return Session token
     */
    public String getSessionToken() {
        return sessionToken;
    }
    
    /**
     * Get username
     * @return Username or email
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Get user type
     * @return User type (ADMIN/CUSTOMER)
     */
    public String getUserType() {
        return userType;
    }
    
    /**
     * Get customer ID
     * @return Customer ID (null for admin)
     */
    public String getCustomerId() {
        return customerId;
    }
    
    /**
     * Get customer name
     * @return Customer name (null for admin)
     */
    public String getCustomerName() {
        return customerName;
    }
    
    /**
     * Set customer name
     * @param customerName Customer name
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    /**
     * Get login time
     * @return Login timestamp
     */
    public LocalDateTime getLoginTime() {
        return loginTime;
    }
    
    /**
     * Get last activity time
     * @return Last activity timestamp
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    /**
     * Update last activity time
     */
    public void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    /**
     * Check if user is admin
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return "ADMIN".equals(userType);
    }
    
    /**
     * Check if user is customer
     * @return true if customer, false otherwise
     */
    public boolean isCustomer() {
        return "CUSTOMER".equals(userType);
    }
    
    /**
     * Get session duration in minutes
     * @return Session duration in minutes
     */
    public long getSessionDurationMinutes() {
        return java.time.Duration.between(loginTime, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * Get time since last activity in minutes
     * @return Minutes since last activity
     */
    public long getMinutesSinceLastActivity() {
        return java.time.Duration.between(lastActivity, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * Check if session is expired based on inactivity
     * @param timeoutMinutes Timeout in minutes
     * @return true if expired, false otherwise
     */
    public boolean isExpired(int timeoutMinutes) {
        return getMinutesSinceLastActivity() > timeoutMinutes;
    }
    
    /**
     * Get display name for the user
     * @return Display name
     */
    public String getDisplayName() {
        if (isCustomer() && customerName != null && !customerName.trim().isEmpty()) {
            return customerName;
        }
        return username;
    }
    
    /**
     * Get user role description
     * @return Role description
     */
    public String getRoleDescription() {
        return switch (userType) {
            case "ADMIN" -> "Administrator";
            case "CUSTOMER" -> "Customer";
            default -> "Unknown";
        };
    }
    
    @Override
    public String toString() {
        return String.format("UserSession{token='%s', username='%s', type='%s', customerId='%s', loginTime=%s}", 
                sessionToken.substring(0, 8) + "...", username, userType, customerId, loginTime);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        UserSession that = (UserSession) obj;
        return sessionToken.equals(that.sessionToken);
    }
    
    @Override
    public int hashCode() {
        return sessionToken.hashCode();
    }
}
