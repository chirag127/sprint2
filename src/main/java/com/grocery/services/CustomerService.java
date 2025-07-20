package com.grocery.services;

import com.grocery.database.DatabaseConnection;
import com.grocery.models.Customer;
import com.grocery.utils.SecurityUtils;
import com.grocery.utils.ValidationUtils;
import com.grocery.auth.AuthenticationManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Customer Service Class
 * Handles all customer-related operations including registration, updates, and search
 *
 * @author Chirag Singhal
 * @version 1.0
 */
public class CustomerService {

    private Scanner scanner;
    private AuthenticationManager authManager;

    public CustomerService() {
        this.scanner = new Scanner(System.in);
        this.authManager = AuthenticationManager.getInstance();
    }

    /**
     * Customer registration process (US002)
     * Handles the complete customer registration workflow
     */
    public void customerRegistration() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           CUSTOMER REGISTRATION");
        System.out.println("=".repeat(50));

        try {
            Customer customer = new Customer();

            // Get and validate full name
            String fullName = getValidatedFullName();
            customer.setFullName(fullName);

            // Get and validate email
            String email = getValidatedEmail();
            customer.setEmail(email.toLowerCase());

            // Get and validate password
            String password = getValidatedPassword();
            String hashedPassword = SecurityUtils.hashPasswordWithSalt(password);
            customer.setPassword(hashedPassword);

            // Get and validate address
            String address = getValidatedAddress();
            customer.setAddress(address);

            // Get and validate contact number
            String contactNumber = getValidatedContactNumber();
            customer.setContactNumber(contactNumber);

            // Generate unique customer ID
            String customerId = generateUniqueCustomerId();
            customer.setCustomerId(customerId);

            // Save customer to database
            if (saveCustomer(customer)) {
                System.out.println("\n" + "✓".repeat(50));
                System.out.println("    CUSTOMER REGISTRATION SUCCESSFUL!");
                System.out.println("✓".repeat(50));
                System.out.println("Customer ID: " + customerId);
                System.out.println("Name: " + fullName);
                System.out.println("Email: " + email);
                System.out.println("Contact: " + contactNumber);
                System.out.println("\nPlease remember your Customer ID and login credentials.");
            } else {
                System.out.println("\n❌ Registration failed. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error during customer registration: " + e.getMessage());
        }
    }

    /**
     * Update customer details (US003)
     * Allows customers to update their information
     */
    public void updateCustomerDetails() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("         UPDATE CUSTOMER DETAILS");
        System.out.println("=".repeat(50));

        // Check if customer is authenticated
        if (!authManager.isCustomer()) {
            System.out.println("❌ You must be logged in as a customer to update details.");
            return;
        }

        String customerId = authManager.getCurrentCustomerId();

        try {
            // Load current customer details
            Customer customer = getCustomerById(customerId);
            if (customer == null) {
                System.out.println("❌ Customer not found.");
                return;
            }

            System.out.println("\nCurrent Details:");
            System.out.println("1. Full Name: " + customer.getFullName());
            System.out.println("2. Email: " + customer.getEmail());
            System.out.println("3. Address: " + customer.getAddress());
            System.out.println("4. Contact Number: " + customer.getContactNumber());
            System.out.println("5. Password: " + customer.getMaskedPassword());

            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Full Name");
            System.out.println("2. Email");
            System.out.println("3. Address");
            System.out.println("4. Contact Number");
            System.out.println("5. Password");
            System.out.println("6. Cancel");

            System.out.print("\nEnter your choice (1-6): ");
            String choice = scanner.nextLine().trim();

            boolean updated = false;

            switch (choice) {
                case "1":
                    String newName = getValidatedFullName();
                    customer.setFullName(newName);
                    updated = updateCustomerInDatabase(customer);
                    if (updated) System.out.println("✓ Full name updated successfully.");
                    break;

                case "2":
                    String newEmail = getValidatedEmailForUpdate(customer.getEmail());
                    customer.setEmail(newEmail.toLowerCase());
                    updated = updateCustomerInDatabase(customer);
                    if (updated) System.out.println("✓ Email updated successfully.");
                    break;

                case "3":
                    String newAddress = getValidatedAddress();
                    customer.setAddress(newAddress);
                    updated = updateCustomerInDatabase(customer);
                    if (updated) System.out.println("✓ Address updated successfully.");
                    break;

                case "4":
                    String newContact = getValidatedContactNumber();
                    customer.setContactNumber(newContact);
                    updated = updateCustomerInDatabase(customer);
                    if (updated) System.out.println("✓ Contact number updated successfully.");
                    break;

                case "5":
                    String newPassword = getValidatedPassword();
                    String hashedPassword = SecurityUtils.hashPasswordWithSalt(newPassword);
                    customer.setPassword(hashedPassword);
                    updated = updateCustomerInDatabase(customer);
                    if (updated) System.out.println("✓ Password updated successfully.");
                    break;

                case "6":
                    System.out.println("Update cancelled.");
                    return;

                default:
                    System.out.println("❌ Invalid choice. Please select 1-6.");
                    return;
            }

            if (!updated) {
                System.out.println("❌ Update failed. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error updating customer details: " + e.getMessage());
        }
    }

    /**
     * Search customer by name (US005) - Admin only
     * Allows administrators to search for customers by name
     */
    public void searchCustomerByName() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           CUSTOMER SEARCH");
        System.out.println("=".repeat(50));

        // Check if admin is authenticated
        if (!authManager.isAdmin()) {
            System.out.println("❌ Access denied. Admin privileges required.");
            return;
        }

        System.out.print("Enter customer name to search: ");
        String searchName = scanner.nextLine().trim();

        if (searchName.isEmpty()) {
            System.out.println("❌ Please enter a valid name to search.");
            return;
        }

        // Sanitize input
        searchName = ValidationUtils.sanitizeInput(searchName);

        try {
            List<Customer> customers = searchCustomersByName(searchName);

            if (customers.isEmpty()) {
                System.out.println("❌ Customer not found.");
            } else {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("                    SEARCH RESULTS");
                System.out.println("=".repeat(80));

                for (int i = 0; i < customers.size(); i++) {
                    Customer customer = customers.get(i);
                    System.out.println("\n--- Customer " + (i + 1) + " ---");
                    System.out.println(customer.getAdminSummary());
                }

                System.out.println("\n" + "=".repeat(80));
                System.out.println("Total customers found: " + customers.size());
            }

        } catch (Exception e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }
    }

    // Private helper methods

    private String getValidatedFullName() {
        while (true) {
            System.out.print("Enter full name: ");
            String fullName = scanner.nextLine().trim();

            if (ValidationUtils.isValidName(fullName)) {
                return fullName;
            } else {
                System.out.println("❌ Invalid name. Please enter 2-50 characters, letters and spaces only.");
            }
        }
    }

    private String getValidatedEmail() {
        while (true) {
            System.out.print("Enter email address: ");
            String email = scanner.nextLine().trim().toLowerCase();

            if (!ValidationUtils.isValidEmail(email)) {
                System.out.println("❌ Invalid email format. Please enter a valid email address.");
                continue;
            }

            // Check if email already exists
            if (authManager.emailExists(email)) {
                System.out.println("❌ Email already registered. Please use a different email address.");
                continue;
            }

            return email;
        }
    }

    private String getValidatedEmailForUpdate(String currentEmail) {
        while (true) {
            System.out.print("Enter new email address: ");
            String email = scanner.nextLine().trim().toLowerCase();

            if (!ValidationUtils.isValidEmail(email)) {
                System.out.println("❌ Invalid email format. Please enter a valid email address.");
                continue;
            }

            // Check if email already exists (but allow current email)
            if (!email.equals(currentEmail) && authManager.emailExists(email)) {
                System.out.println("❌ Email already registered. Please use a different email address.");
                continue;
            }

            return email;
        }
    }

    private String getValidatedPassword() {
        while (true) {
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            if (ValidationUtils.isValidPassword(password)) {
                System.out.print("Confirm password: ");
                String confirmPassword = scanner.nextLine();

                if (password.equals(confirmPassword)) {
                    return password;
                } else {
                    System.out.println("❌ Passwords do not match. Please try again.");
                }
            } else {
                System.out.println("❌ Password does not meet requirements:");
                System.out.println(ValidationUtils.getPasswordRequirements());
            }
        }
    }

    private String getValidatedAddress() {
        while (true) {
            System.out.print("Enter full address: ");
            String address = scanner.nextLine().trim();

            if (ValidationUtils.isValidAddress(address)) {
                return address;
            } else {
                System.out.println("❌ Invalid address. Please enter 10-500 characters.");
            }
        }
    }

    private String getValidatedContactNumber() {
        while (true) {
            System.out.print("Enter contact number (10 digits): ");
            String contactNumber = scanner.nextLine().trim();

            if (ValidationUtils.isValidPhone(contactNumber)) {
                return contactNumber;
            } else {
                System.out.println("❌ Invalid contact number. Please enter exactly 10 digits.");
            }
        }
    }

    private String generateUniqueCustomerId() {
        String customerId;
        int attempts = 0;

        do {
            customerId = SecurityUtils.generateCustomerId();
            attempts++;

            if (attempts > 100) {
                throw new RuntimeException("Unable to generate unique customer ID after 100 attempts");
            }
        } while (customerIdExists(customerId));

        return customerId;
    }

    private boolean customerIdExists(String customerId) {
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
            System.err.println("Error checking customer ID existence: " + e.getMessage());
            return true; // Assume exists to be safe
        }
    }

    private boolean saveCustomer(Customer customer) {
        try {
            String query = """
                INSERT INTO customers (customer_id, full_name, email, password, address, contact_number)
                VALUES (?, ?, ?, ?, ?, ?)
            """;

            int rowsAffected = DatabaseConnection.executeUpdate(query,
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAddress(),
                customer.getContactNumber()
            );

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error saving customer: " + e.getMessage());
            return false;
        }
    }

    private Customer getCustomerById(String customerId) {
        try {
            String query = "SELECT * FROM customers WHERE customer_id = ?";
            ResultSet rs = DatabaseConnection.executeQuery(query, customerId);

            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPassword(rs.getString("password"));
                customer.setAddress(rs.getString("address"));
                customer.setContactNumber(rs.getString("contact_number"));

                rs.close();
                return customer;
            }

            rs.close();
            return null;

        } catch (SQLException e) {
            System.err.println("Error retrieving customer: " + e.getMessage());
            return null;
        }
    }

    private boolean updateCustomerInDatabase(Customer customer) {
        try {
            String query = """
                UPDATE customers
                SET full_name = ?, email = ?, password = ?, address = ?, contact_number = ?, updated_at = CURRENT_TIMESTAMP
                WHERE customer_id = ?
            """;

            int rowsAffected = DatabaseConnection.executeUpdate(query,
                customer.getFullName(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getAddress(),
                customer.getContactNumber(),
                customer.getCustomerId()
            );

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    private List<Customer> searchCustomersByName(String searchName) {
        List<Customer> customers = new ArrayList<>();

        try {
            // Use LIKE with wildcards for case-insensitive search
            String query = "SELECT * FROM customers WHERE LOWER(full_name) LIKE LOWER(?) ORDER BY full_name";
            String searchPattern = "%" + searchName + "%";

            ResultSet rs = DatabaseConnection.executeQuery(query, searchPattern);

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getString("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setEmail(rs.getString("email"));
                customer.setPassword(rs.getString("password"));
                customer.setAddress(rs.getString("address"));
                customer.setContactNumber(rs.getString("contact_number"));

                customers.add(customer);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("Error searching customers: " + e.getMessage());
        }

        return customers;
    }
}
