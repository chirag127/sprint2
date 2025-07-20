package com.grocery.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecurityUtils class
 * Tests password hashing, security validation, and SQL injection prevention
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
class SecurityUtilsTest {

    @Test
    @DisplayName("Test password hashing with salt")
    void testHashPassword() {
        String password = "TestPassword123!";
        String salt = "testSalt";
        
        String hash1 = SecurityUtils.hashPassword(password, salt);
        String hash2 = SecurityUtils.hashPassword(password, salt);
        
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(hash1, hash2); // Same password and salt should produce same hash
        assertNotEquals(password, hash1); // Hash should be different from original password
    }

    @Test
    @DisplayName("Test salt generation")
    void testGenerateSalt() {
        String salt1 = SecurityUtils.generateSalt();
        String salt2 = SecurityUtils.generateSalt();
        
        assertNotNull(salt1);
        assertNotNull(salt2);
        assertNotEquals(salt1, salt2); // Different salts should be generated
        assertTrue(salt1.length() > 0);
        assertTrue(salt2.length() > 0);
    }

    @Test
    @DisplayName("Test password hashing with auto-generated salt")
    void testHashPasswordWithSalt() {
        String password = "TestPassword123!";
        
        String hashedPassword1 = SecurityUtils.hashPasswordWithSalt(password);
        String hashedPassword2 = SecurityUtils.hashPasswordWithSalt(password);
        
        assertNotNull(hashedPassword1);
        assertNotNull(hashedPassword2);
        assertNotEquals(hashedPassword1, hashedPassword2); // Different salts should produce different hashes
        assertTrue(hashedPassword1.contains(":")); // Should contain salt:hash format
        assertTrue(hashedPassword2.contains(":")); // Should contain salt:hash format
    }

    @Test
    @DisplayName("Test password verification")
    void testVerifyPassword() {
        String password = "TestPassword123!";
        String hashedPassword = SecurityUtils.hashPasswordWithSalt(password);
        
        assertTrue(SecurityUtils.verifyPassword(password, hashedPassword));
        assertFalse(SecurityUtils.verifyPassword("WrongPassword", hashedPassword));
        assertFalse(SecurityUtils.verifyPassword("", hashedPassword));
        assertFalse(SecurityUtils.verifyPassword(null, hashedPassword));
    }

    @Test
    @DisplayName("Test password verification with invalid hash format")
    void testVerifyPasswordInvalidFormat() {
        String password = "TestPassword123!";
        
        assertFalse(SecurityUtils.verifyPassword(password, "invalid-hash"));
        assertFalse(SecurityUtils.verifyPassword(password, ""));
        assertFalse(SecurityUtils.verifyPassword(password, null));
        assertFalse(SecurityUtils.verifyPassword(password, "only-one-part"));
    }

    @Test
    @DisplayName("Test customer ID generation")
    void testGenerateCustomerId() {
        String customerId1 = SecurityUtils.generateCustomerId();
        String customerId2 = SecurityUtils.generateCustomerId();
        
        assertNotNull(customerId1);
        assertNotNull(customerId2);
        assertEquals(6, customerId1.length());
        assertEquals(6, customerId2.length());
        assertTrue(customerId1.matches("\\d{6}")); // Should be 6 digits
        assertTrue(customerId2.matches("\\d{6}")); // Should be 6 digits
        
        // Should be different (very high probability)
        assertNotEquals(customerId1, customerId2);
    }

    @Test
    @DisplayName("Test password masking")
    void testMaskPassword() {
        assertEquals("", SecurityUtils.maskPassword(""));
        assertEquals("", SecurityUtils.maskPassword(null));
        assertEquals("*", SecurityUtils.maskPassword("a"));
        assertEquals("********", SecurityUtils.maskPassword("password"));
        assertEquals("************", SecurityUtils.maskPassword("TestPassword"));
    }

    @Test
    @DisplayName("Test SQL injection pattern detection")
    void testContainsSQLInjectionPatterns() {
        // Test malicious inputs
        assertTrue(SecurityUtils.containsSQLInjectionPatterns("SELECT * FROM users"));
        assertTrue(SecurityUtils.containsSQLInjectionPatterns("'; DROP TABLE users; --"));
        assertTrue(SecurityUtils.containsSQLInjectionPatterns("admin' OR '1'='1"));
        assertTrue(SecurityUtils.containsSQLInjectionPatterns("UNION SELECT password FROM admin"));
        assertTrue(SecurityUtils.containsSQLInjectionPatterns("exec xp_cmdshell"));
        assertTrue(SecurityUtils.containsSQLInjectionPatterns("<script>alert('xss')</script>"));
        
        // Test safe inputs
        assertFalse(SecurityUtils.containsSQLInjectionPatterns("john.doe@example.com"));
        assertFalse(SecurityUtils.containsSQLInjectionPatterns("John Doe"));
        assertFalse(SecurityUtils.containsSQLInjectionPatterns("123 Main Street"));
        assertFalse(SecurityUtils.containsSQLInjectionPatterns("Password123!"));
        assertFalse(SecurityUtils.containsSQLInjectionPatterns(null));
    }

    @Test
    @DisplayName("Test database input sanitization")
    void testSanitizeForDatabase() {
        assertEquals("", SecurityUtils.sanitizeForDatabase(null));
        assertEquals("John Doe", SecurityUtils.sanitizeForDatabase("John Doe"));
        assertEquals("John''s Store", SecurityUtils.sanitizeForDatabase("John's Store"));
        assertEquals("Say \\\"Hello\\\"", SecurityUtils.sanitizeForDatabase("Say \"Hello\""));
        assertEquals("Path\\\\to\\\\file", SecurityUtils.sanitizeForDatabase("Path\\to\\file"));
    }

    @Test
    @DisplayName("Test session token validation")
    void testIsValidSessionToken() {
        // Test valid tokens
        String validToken = SecurityUtils.generateSessionToken();
        assertTrue(SecurityUtils.isValidSessionToken(validToken));
        
        // Test invalid tokens
        assertFalse(SecurityUtils.isValidSessionToken(""));
        assertFalse(SecurityUtils.isValidSessionToken(null));
        assertFalse(SecurityUtils.isValidSessionToken("invalid-token"));
        assertFalse(SecurityUtils.isValidSessionToken("short"));
    }

    @Test
    @DisplayName("Test session token generation")
    void testGenerateSessionToken() {
        String token1 = SecurityUtils.generateSessionToken();
        String token2 = SecurityUtils.generateSessionToken();
        
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotEquals(token1, token2); // Should generate different tokens
        assertTrue(SecurityUtils.isValidSessionToken(token1));
        assertTrue(SecurityUtils.isValidSessionToken(token2));
    }

    @Test
    @DisplayName("Test password strength calculation")
    void testGetPasswordStrength() {
        // Very weak passwords
        assertEquals(0, SecurityUtils.getPasswordStrength(""));
        assertEquals(0, SecurityUtils.getPasswordStrength(null));
        assertEquals(0, SecurityUtils.getPasswordStrength("password"));
        assertEquals(0, SecurityUtils.getPasswordStrength("123456"));
        
        // Weak passwords
        assertEquals(1, SecurityUtils.getPasswordStrength("Password"));
        assertEquals(1, SecurityUtils.getPasswordStrength("password123"));
        
        // Fair passwords
        assertEquals(2, SecurityUtils.getPasswordStrength("Password123"));
        
        // Good passwords
        assertEquals(3, SecurityUtils.getPasswordStrength("Password123!"));
        
        // Strong passwords
        assertEquals(4, SecurityUtils.getPasswordStrength("MyVerySecurePassword123!"));
    }

    @Test
    @DisplayName("Test password strength descriptions")
    void testGetPasswordStrengthDescription() {
        assertEquals("Very Weak", SecurityUtils.getPasswordStrengthDescription(0));
        assertEquals("Weak", SecurityUtils.getPasswordStrengthDescription(1));
        assertEquals("Fair", SecurityUtils.getPasswordStrengthDescription(2));
        assertEquals("Good", SecurityUtils.getPasswordStrengthDescription(3));
        assertEquals("Strong", SecurityUtils.getPasswordStrengthDescription(4));
        assertEquals("Unknown", SecurityUtils.getPasswordStrengthDescription(5));
        assertEquals("Unknown", SecurityUtils.getPasswordStrengthDescription(-1));
    }

    @Test
    @DisplayName("Test password strength with common patterns")
    void testPasswordStrengthWithCommonPatterns() {
        // Common passwords should have reduced strength
        int normalStrength = SecurityUtils.getPasswordStrength("MySecurePass123!");
        int commonStrength = SecurityUtils.getPasswordStrength("password123!");
        
        assertTrue(normalStrength > commonStrength);
        
        // Test other common patterns
        assertTrue(SecurityUtils.getPasswordStrength("qwerty123!") < 4);
        assertTrue(SecurityUtils.getPasswordStrength("123456Aa!") < 4);
    }
}
