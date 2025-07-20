package com.grocery.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Security Utility Class
 * Provides password hashing and security-related utilities
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class SecurityUtils {
    
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Hash password using SHA-256 with salt
     * @param password Plain text password
     * @param salt Salt for hashing
     * @return Hashed password
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Convert to Base64 for storage
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage());
        }
    }
    
    /**
     * Generate random salt for password hashing
     * @return Random salt string
     */
    public static String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * Hash password with auto-generated salt
     * @param password Plain text password
     * @return Hashed password with salt (format: salt:hash)
     */
    public static String hashPasswordWithSalt(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + ":" + hash;
    }
    
    /**
     * Verify password against stored hash
     * @param password Plain text password to verify
     * @param storedHash Stored hash (format: salt:hash)
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String salt = parts[0];
            String hash = parts[1];
            String computedHash = hashPassword(password, salt);
            
            return hash.equals(computedHash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate unique customer ID (6 digits)
     * @return 6-digit customer ID
     */
    public static String generateCustomerId() {
        int id = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(id);
    }
    
    /**
     * Mask password for display (show only asterisks)
     * @param password Password to mask
     * @return Masked password
     */
    public static String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "*".repeat(password.length());
    }
    
    /**
     * Check if input contains potential SQL injection patterns
     * @param input Input string to check
     * @return true if suspicious patterns found, false otherwise
     */
    public static boolean containsSQLInjectionPatterns(String input) {
        if (input == null) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        String[] sqlKeywords = {
            "select", "insert", "update", "delete", "drop", "create", "alter",
            "union", "exec", "execute", "script", "javascript", "vbscript",
            "onload", "onerror", "onclick", "--", "/*", "*/", "xp_", "sp_"
        };
        
        for (String keyword : sqlKeywords) {
            if (lowerInput.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Sanitize input for safe database operations
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeForDatabase(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove or escape potentially dangerous characters
        return input.trim()
            .replaceAll("'", "''")  // Escape single quotes
            .replaceAll("\"", "\\\"")  // Escape double quotes
            .replaceAll("\\\\", "\\\\\\\\");  // Escape backslashes
    }
    
    /**
     * Validate session token format
     * @param token Session token to validate
     * @return true if valid format, false otherwise
     */
    public static boolean isValidSessionToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        // Simple token format validation (Base64 encoded, reasonable length)
        try {
            byte[] decoded = Base64.getDecoder().decode(token);
            return decoded.length >= 16 && decoded.length <= 64;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Generate session token
     * @return Random session token
     */
    public static String generateSessionToken() {
        byte[] token = new byte[32];
        secureRandom.nextBytes(token);
        return Base64.getEncoder().encodeToString(token);
    }
    
    /**
     * Check password strength level
     * @param password Password to check
     * @return Strength level (0-4: Very Weak, Weak, Fair, Good, Strong)
     */
    public static int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int score = 0;
        
        // Length check
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        
        // Character variety checks
        if (password.matches(".*[a-z].*")) score++;  // Lowercase
        if (password.matches(".*[A-Z].*")) score++;  // Uppercase
        if (password.matches(".*[0-9].*")) score++;  // Digits
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++;  // Special chars
        
        // Reduce score for common patterns
        if (password.toLowerCase().contains("password") || 
            password.toLowerCase().contains("123456") ||
            password.toLowerCase().contains("qwerty")) {
            score = Math.max(0, score - 2);
        }
        
        return Math.min(4, score);
    }
    
    /**
     * Get password strength description
     * @param strength Strength level (0-4)
     * @return String description of strength
     */
    public static String getPasswordStrengthDescription(int strength) {
        return switch (strength) {
            case 0 -> "Very Weak";
            case 1 -> "Weak";
            case 2 -> "Fair";
            case 3 -> "Good";
            case 4 -> "Strong";
            default -> "Unknown";
        };
    }
}
