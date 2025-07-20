package com.grocery.auth;

import com.grocery.database.DatabaseConnection;
import com.grocery.utils.SecurityUtils;
import com.grocery.utils.ValidationUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Manager Class
 * Handles user authentication and session management
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class AuthenticationManager {
    
    private static AuthenticationManager instance;
    private Map<String, UserSession> activeSessions;
    private UserSession currentSession;
    
    // Private constructor for singleton pattern
    private AuthenticationManager() {
        this.activeSessions = new HashMap<>();
        this.currentSession = null;
    }
    
    /**
     * Get singleton instance of AuthenticationManager
     * @return AuthenticationManager instance
     */
    public static synchronized AuthenticationManager getInstance() {
        if (instance == null) {
            instance = new AuthenticationManager();
        }
        return instance;
    }
    
    /**
     * Authenticate admin user
     * @param username Admin username
     * @param password Admin password
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticateAdmin(String username, String password) {
        try {
            // Validate input
            if (username == null || password == null || 
                username.trim().isEmpty() || password.trim().isEmpty()) {
                return false;
            }
            
            // Sanitize input to prevent SQL injection
            username = ValidationUtils.sanitizeInput(username.trim());
            
            // Check for SQL injection patterns
            if (SecurityUtils.containsSQLInjectionPatterns(username) || 
                SecurityUtils.containsSQLInjectionPatterns(password)) {
                System.out.println("Security Alert: Potential SQL injection attempt detected!");
                return false;
            }
            
            // Query database for admin credentials
            String query = "SELECT username, password FROM admin WHERE username = ?";
            ResultSet rs = DatabaseConnection.executeQuery(query, username);
            
            if (rs.next()) {
                String storedUsername = rs.getString("username");
                String storedPassword = rs.getString("password");
                
                // For admin, we use simple password comparison (as per requirements)
                // In production, this should also be hashed
                if (storedUsername.equals(username) && storedPassword.equals(password)) {
                    // Create admin session
                    String sessionToken = SecurityUtils.generateSessionToken();
                    UserSession session = new UserSession(sessionToken, username, "ADMIN", null);
                    activeSessions.put(sessionToken, session);
                    currentSession = session;
                    
                    rs.close();
                    return true;
                }
            }
            
            rs.close();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Database error during admin authentication: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Authenticate customer user
     * @param email Customer email
     * @param password Customer password
     * @return true if authentication successful, false otherwise
     */
    public boolean authenticateCustomer(String email, String password) {
        try {
            // Validate input
            if (email == null || password == null || 
                email.trim().isEmpty() || password.trim().isEmpty()) {
                return false;
            }
            
            // Validate email format
            if (!ValidationUtils.isValidEmail(email)) {
                return false;
            }
            
            // Sanitize input
            email = ValidationUtils.sanitizeInput(email.trim().toLowerCase());
            
            // Check for SQL injection patterns
            if (SecurityUtils.containsSQLInjectionPatterns(email) || 
                SecurityUtils.containsSQLInjectionPatterns(password)) {
                System.out.println("Security Alert: Potential SQL injection attempt detected!");
                return false;
            }
            
            // Query database for customer credentials
            String query = "SELECT customer_id, full_name, email, password FROM customers WHERE email = ?";
            ResultSet rs = DatabaseConnection.executeQuery(query, email);
            
            if (rs.next()) {
                String customerId = rs.getString("customer_id");
                String fullName = rs.getString("full_name");
                String storedEmail = rs.getString("email");
                String storedPassword = rs.getString("password");
                
                // Verify password (assuming hashed passwords)
                if (SecurityUtils.verifyPassword(password, storedPassword)) {
                    // Create customer session
                    String sessionToken = SecurityUtils.generateSessionToken();
                    UserSession session = new UserSession(sessionToken, storedEmail, "CUSTOMER", customerId);
                    session.setCustomerName(fullName);
                    activeSessions.put(sessionToken, session);
                    currentSession = session;
                    
                    rs.close();
                    return true;
                }
            }
            
            rs.close();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Database error during customer authentication: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if user is currently authenticated
     * @return true if authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return currentSession != null && isValidSession(currentSession.getSessionToken());
    }
    
    /**
     * Check if current user is admin
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return isAuthenticated() && "ADMIN".equals(currentSession.getUserType());
    }
    
    /**
     * Check if current user is customer
     * @return true if customer, false otherwise
     */
    public boolean isCustomer() {
        return isAuthenticated() && "CUSTOMER".equals(currentSession.getUserType());
    }
    
    /**
     * Get current user session
     * @return Current UserSession or null if not authenticated
     */
    public UserSession getCurrentSession() {
        if (isAuthenticated()) {
            return currentSession;
        }
        return null;
    }
    
    /**
     * Get current customer ID
     * @return Customer ID if customer is authenticated, null otherwise
     */
    public String getCurrentCustomerId() {
        if (isCustomer()) {
            return currentSession.getCustomerId();
        }
        return null;
    }
    
    /**
     * Get current username/email
     * @return Username/email if authenticated, null otherwise
     */
    public String getCurrentUsername() {
        if (isAuthenticated()) {
            return currentSession.getUsername();
        }
        return null;
    }
    
    /**
     * Logout current user
     */
    public void logout() {
        if (currentSession != null) {
            activeSessions.remove(currentSession.getSessionToken());
            currentSession = null;
        }
    }
    
    /**
     * Validate session token
     * @param sessionToken Session token to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidSession(String sessionToken) {
        if (sessionToken == null || !SecurityUtils.isValidSessionToken(sessionToken)) {
            return false;
        }
        
        UserSession session = activeSessions.get(sessionToken);
        if (session == null) {
            return false;
        }
        
        // Check if session has expired (optional - implement session timeout)
        // For now, sessions don't expire during application runtime
        return true;
    }
    
    /**
     * Clear all sessions (for application shutdown)
     */
    public void clearAllSessions() {
        activeSessions.clear();
        currentSession = null;
    }
    
    /**
     * Get total active sessions count
     * @return Number of active sessions
     */
    public int getActiveSessionsCount() {
        return activeSessions.size();
    }
    
    /**
     * Force logout user by session token
     * @param sessionToken Session token to invalidate
     */
    public void forceLogout(String sessionToken) {
        activeSessions.remove(sessionToken);
        if (currentSession != null && currentSession.getSessionToken().equals(sessionToken)) {
            currentSession = null;
        }
    }
    
    /**
     * Check if email exists in customer database
     * @param email Email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        try {
            if (!ValidationUtils.isValidEmail(email)) {
                return false;
            }
            
            String query = "SELECT COUNT(*) FROM customers WHERE email = ?";
            ResultSet rs = DatabaseConnection.executeQuery(query, email.trim().toLowerCase());
            
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                return count > 0;
            }
            
            rs.close();
            return false;
            
        } catch (SQLException e) {
            System.err.println("Database error checking email existence: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Validate login attempt and provide appropriate error message
     * @param username Username/email
     * @param password Password
     * @param userType Expected user type ("ADMIN" or "CUSTOMER")
     * @return Error message or null if successful
     */
    public String validateLoginAttempt(String username, String password, String userType) {
        // Basic validation
        if (username == null || username.trim().isEmpty()) {
            return "Username/Email cannot be empty.";
        }
        
        if (password == null || password.trim().isEmpty()) {
            return "Password cannot be empty.";
        }
        
        // Check for security threats
        if (SecurityUtils.containsSQLInjectionPatterns(username) || 
            SecurityUtils.containsSQLInjectionPatterns(password)) {
            return "Invalid characters detected in input.";
        }
        
        // Validate email format for customers
        if ("CUSTOMER".equals(userType) && !ValidationUtils.isValidEmail(username)) {
            return "Please enter a valid email address.";
        }
        
        return null; // No validation errors
    }
}
