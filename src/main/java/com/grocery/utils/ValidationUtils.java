package com.grocery.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Validation Utility Class
 * Provides comprehensive input validation to prevent security vulnerabilities
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class ValidationUtils {
    
    // Regular expression patterns for validation
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    
    private static final String PHONE_PATTERN = "^[0-9]{10}$";
    
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    private static final String NAME_PATTERN = "^[a-zA-Z\\s]{2,50}$";
    
    private static final String CUSTOMER_ID_PATTERN = "^[0-9]{6}$";
    
    // Compiled patterns for better performance
    private static final Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private static final Pattern phonePattern = Pattern.compile(PHONE_PATTERN);
    private static final Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
    private static final Pattern namePattern = Pattern.compile(NAME_PATTERN);
    private static final Pattern customerIdPattern = Pattern.compile(CUSTOMER_ID_PATTERN);
    
    /**
     * Validate email address format
     * @param email Email address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = emailPattern.matcher(email.trim());
        return matcher.matches();
    }
    
    /**
     * Validate phone number format (10 digits)
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = phonePattern.matcher(phone.trim());
        return matcher.matches();
    }
    
    /**
     * Validate password strength
     * Must contain: 8+ characters, uppercase, lowercase, digit, special character
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }
    
    /**
     * Validate name format (letters and spaces only, 2-50 characters)
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = namePattern.matcher(name.trim());
        return matcher.matches();
    }
    
    /**
     * Validate customer ID format (6 digits)
     * @param customerId Customer ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidCustomerId(String customerId) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = customerIdPattern.matcher(customerId.trim());
        return matcher.matches();
    }
    
    /**
     * Validate product price (positive decimal)
     * @param price Price to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPrice(double price) {
        return price > 0;
    }
    
    /**
     * Validate product quantity (non-negative integer)
     * @param quantity Quantity to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity >= 0;
    }
    
    /**
     * Validate product ID (positive integer)
     * @param productId Product ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidProductId(int productId) {
        return productId > 0;
    }
    
    /**
     * Sanitize input string to prevent SQL injection
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove potentially dangerous characters
        String sanitized = input.trim()
            .replaceAll("[<>\"'%;()&+]", "")
            .replaceAll("--", "")
            .replaceAll("/\\*", "")
            .replaceAll("\\*/", "")
            .replaceAll("xp_", "")
            .replaceAll("sp_", "")
            .replaceAll("exec", "")
            .replaceAll("execute", "")
            .replaceAll("select", "")
            .replaceAll("insert", "")
            .replaceAll("update", "")
            .replaceAll("delete", "")
            .replaceAll("drop", "")
            .replaceAll("create", "")
            .replaceAll("alter", "")
            .replaceAll("union", "")
            .replaceAll("script", "");
            
        return sanitized;
    }
    
    /**
     * Validate address (non-empty, reasonable length)
     * @param address Address to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        String trimmed = address.trim();
        return trimmed.length() >= 10 && trimmed.length() <= 500;
    }
    
    /**
     * Validate product name (non-empty, reasonable length)
     * @param productName Product name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidProductName(String productName) {
        if (productName == null || productName.trim().isEmpty()) {
            return false;
        }
        String trimmed = productName.trim();
        return trimmed.length() >= 2 && trimmed.length() <= 100;
    }
    
    /**
     * Check if string contains only numeric characters
     * @param str String to check
     * @return true if numeric, false otherwise
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if string contains only integer characters
     * @param str String to check
     * @return true if integer, false otherwise
     */
    public static boolean isInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Get password strength description
     * @param password Password to analyze
     * @return String describing password requirements
     */
    public static String getPasswordRequirements() {
        return "Password must contain:\n" +
               "- At least 8 characters\n" +
               "- At least one uppercase letter (A-Z)\n" +
               "- At least one lowercase letter (a-z)\n" +
               "- At least one digit (0-9)\n" +
               "- At least one special character (@$!%*?&)";
    }
}
