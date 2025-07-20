package com.grocery.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationUtils class
 * Tests all validation methods for security and correctness
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
class ValidationUtilsTest {

    @Test
    @DisplayName("Test valid email addresses")
    void testValidEmails() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue(ValidationUtils.isValidEmail("user+tag@example.org"));
        assertTrue(ValidationUtils.isValidEmail("123@test.com"));
    }

    @Test
    @DisplayName("Test invalid email addresses")
    void testInvalidEmails() {
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
        assertFalse(ValidationUtils.isValidEmail("test@"));
        assertFalse(ValidationUtils.isValidEmail("test..test@example.com"));
    }

    @Test
    @DisplayName("Test valid phone numbers")
    void testValidPhoneNumbers() {
        assertTrue(ValidationUtils.isValidPhone("1234567890"));
        assertTrue(ValidationUtils.isValidPhone("9876543210"));
        assertTrue(ValidationUtils.isValidPhone("0123456789"));
    }

    @Test
    @DisplayName("Test invalid phone numbers")
    void testInvalidPhoneNumbers() {
        assertFalse(ValidationUtils.isValidPhone(""));
        assertFalse(ValidationUtils.isValidPhone(null));
        assertFalse(ValidationUtils.isValidPhone("123456789")); // 9 digits
        assertFalse(ValidationUtils.isValidPhone("12345678901")); // 11 digits
        assertFalse(ValidationUtils.isValidPhone("123-456-7890")); // with dashes
        assertFalse(ValidationUtils.isValidPhone("abcdefghij")); // letters
    }

    @Test
    @DisplayName("Test valid passwords")
    void testValidPasswords() {
        assertTrue(ValidationUtils.isValidPassword("Password123!"));
        assertTrue(ValidationUtils.isValidPassword("MySecure@Pass1"));
        assertTrue(ValidationUtils.isValidPassword("Test123$Pass"));
    }

    @Test
    @DisplayName("Test invalid passwords")
    void testInvalidPasswords() {
        assertFalse(ValidationUtils.isValidPassword(""));
        assertFalse(ValidationUtils.isValidPassword(null));
        assertFalse(ValidationUtils.isValidPassword("short")); // too short
        assertFalse(ValidationUtils.isValidPassword("password")); // no uppercase, digits, special chars
        assertFalse(ValidationUtils.isValidPassword("PASSWORD")); // no lowercase, digits, special chars
        assertFalse(ValidationUtils.isValidPassword("Password")); // no digits, special chars
        assertFalse(ValidationUtils.isValidPassword("Password123")); // no special chars
    }

    @Test
    @DisplayName("Test valid names")
    void testValidNames() {
        assertTrue(ValidationUtils.isValidName("John Doe"));
        assertTrue(ValidationUtils.isValidName("Mary Jane"));
        assertTrue(ValidationUtils.isValidName("Al"));
        assertTrue(ValidationUtils.isValidName("Jean Claude Van Damme"));
    }

    @Test
    @DisplayName("Test invalid names")
    void testInvalidNames() {
        assertFalse(ValidationUtils.isValidName(""));
        assertFalse(ValidationUtils.isValidName(null));
        assertFalse(ValidationUtils.isValidName("A")); // too short
        assertFalse(ValidationUtils.isValidName("John123")); // contains numbers
        assertFalse(ValidationUtils.isValidName("John@Doe")); // contains special chars
        assertFalse(ValidationUtils.isValidName("A".repeat(51))); // too long
    }

    @Test
    @DisplayName("Test valid customer IDs")
    void testValidCustomerIds() {
        assertTrue(ValidationUtils.isValidCustomerId("123456"));
        assertTrue(ValidationUtils.isValidCustomerId("000000"));
        assertTrue(ValidationUtils.isValidCustomerId("999999"));
    }

    @Test
    @DisplayName("Test invalid customer IDs")
    void testInvalidCustomerIds() {
        assertFalse(ValidationUtils.isValidCustomerId(""));
        assertFalse(ValidationUtils.isValidCustomerId(null));
        assertFalse(ValidationUtils.isValidCustomerId("12345")); // 5 digits
        assertFalse(ValidationUtils.isValidCustomerId("1234567")); // 7 digits
        assertFalse(ValidationUtils.isValidCustomerId("12345a")); // contains letter
        assertFalse(ValidationUtils.isValidCustomerId("123-456")); // contains dash
    }

    @Test
    @DisplayName("Test valid prices")
    void testValidPrices() {
        assertTrue(ValidationUtils.isValidPrice(0.01));
        assertTrue(ValidationUtils.isValidPrice(10.99));
        assertTrue(ValidationUtils.isValidPrice(1000.0));
    }

    @Test
    @DisplayName("Test invalid prices")
    void testInvalidPrices() {
        assertFalse(ValidationUtils.isValidPrice(0.0));
        assertFalse(ValidationUtils.isValidPrice(-1.0));
        assertFalse(ValidationUtils.isValidPrice(-10.99));
    }

    @Test
    @DisplayName("Test valid quantities")
    void testValidQuantities() {
        assertTrue(ValidationUtils.isValidQuantity(0));
        assertTrue(ValidationUtils.isValidQuantity(1));
        assertTrue(ValidationUtils.isValidQuantity(100));
    }

    @Test
    @DisplayName("Test invalid quantities")
    void testInvalidQuantities() {
        assertFalse(ValidationUtils.isValidQuantity(-1));
        assertFalse(ValidationUtils.isValidQuantity(-100));
    }

    @Test
    @DisplayName("Test valid product IDs")
    void testValidProductIds() {
        assertTrue(ValidationUtils.isValidProductId(1));
        assertTrue(ValidationUtils.isValidProductId(100));
        assertTrue(ValidationUtils.isValidProductId(999999));
    }

    @Test
    @DisplayName("Test invalid product IDs")
    void testInvalidProductIds() {
        assertFalse(ValidationUtils.isValidProductId(0));
        assertFalse(ValidationUtils.isValidProductId(-1));
        assertFalse(ValidationUtils.isValidProductId(-100));
    }

    @Test
    @DisplayName("Test input sanitization")
    void testSanitizeInput() {
        assertEquals("", ValidationUtils.sanitizeInput(null));
        assertEquals("John Doe", ValidationUtils.sanitizeInput("John Doe"));
        assertEquals("test", ValidationUtils.sanitizeInput("test<script>"));
        assertEquals("normaltext", ValidationUtils.sanitizeInput("normal'text"));
        assertEquals("", ValidationUtils.sanitizeInput("select * from users"));
    }

    @Test
    @DisplayName("Test valid addresses")
    void testValidAddresses() {
        assertTrue(ValidationUtils.isValidAddress("123 Main Street, City, State"));
        assertTrue(ValidationUtils.isValidAddress("Apartment 4B, 456 Oak Avenue"));
        assertTrue(ValidationUtils.isValidAddress("1234567890")); // 10 characters minimum
    }

    @Test
    @DisplayName("Test invalid addresses")
    void testInvalidAddresses() {
        assertFalse(ValidationUtils.isValidAddress(""));
        assertFalse(ValidationUtils.isValidAddress(null));
        assertFalse(ValidationUtils.isValidAddress("Short")); // less than 10 characters
        assertFalse(ValidationUtils.isValidAddress("A".repeat(501))); // more than 500 characters
    }

    @Test
    @DisplayName("Test valid product names")
    void testValidProductNames() {
        assertTrue(ValidationUtils.isValidProductName("Apple"));
        assertTrue(ValidationUtils.isValidProductName("Organic Bananas"));
        assertTrue(ValidationUtils.isValidProductName("AB")); // 2 characters minimum
    }

    @Test
    @DisplayName("Test invalid product names")
    void testInvalidProductNames() {
        assertFalse(ValidationUtils.isValidProductName(""));
        assertFalse(ValidationUtils.isValidProductName(null));
        assertFalse(ValidationUtils.isValidProductName("A")); // 1 character
        assertFalse(ValidationUtils.isValidProductName("A".repeat(101))); // more than 100 characters
    }

    @Test
    @DisplayName("Test numeric string validation")
    void testIsNumeric() {
        assertTrue(ValidationUtils.isNumeric("123"));
        assertTrue(ValidationUtils.isNumeric("123.45"));
        assertTrue(ValidationUtils.isNumeric("0"));
        assertTrue(ValidationUtils.isNumeric("-123.45"));
        
        assertFalse(ValidationUtils.isNumeric(""));
        assertFalse(ValidationUtils.isNumeric(null));
        assertFalse(ValidationUtils.isNumeric("abc"));
        assertFalse(ValidationUtils.isNumeric("12.34.56"));
    }

    @Test
    @DisplayName("Test integer string validation")
    void testIsInteger() {
        assertTrue(ValidationUtils.isInteger("123"));
        assertTrue(ValidationUtils.isInteger("0"));
        assertTrue(ValidationUtils.isInteger("-123"));
        
        assertFalse(ValidationUtils.isInteger(""));
        assertFalse(ValidationUtils.isInteger(null));
        assertFalse(ValidationUtils.isInteger("123.45"));
        assertFalse(ValidationUtils.isInteger("abc"));
    }

    @Test
    @DisplayName("Test password requirements message")
    void testGetPasswordRequirements() {
        String requirements = ValidationUtils.getPasswordRequirements();
        assertNotNull(requirements);
        assertTrue(requirements.contains("8 characters"));
        assertTrue(requirements.contains("uppercase"));
        assertTrue(requirements.contains("lowercase"));
        assertTrue(requirements.contains("digit"));
        assertTrue(requirements.contains("special character"));
    }
}
