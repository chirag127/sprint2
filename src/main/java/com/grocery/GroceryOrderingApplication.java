package com.grocery;

import com.grocery.auth.AuthenticationManager;
import com.grocery.database.DatabaseConnection;
import com.grocery.services.CustomerService;
import com.grocery.services.ProductService;
import com.grocery.services.OrderService;
import com.grocery.utils.ValidationUtils;

import java.util.Scanner;

/**
 * Main Grocery Ordering Application
 * Menu-based console application for online grocery ordering system
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class GroceryOrderingApplication {
    
    private Scanner scanner;
    private AuthenticationManager authManager;
    private CustomerService customerService;
    private ProductService productService;
    private OrderService orderService;
    private boolean isRunning;
    
    public GroceryOrderingApplication() {
        this.scanner = new Scanner(System.in);
        this.authManager = AuthenticationManager.getInstance();
        this.customerService = new CustomerService();
        this.productService = new ProductService();
        this.orderService = new OrderService();
        this.isRunning = true;
    }
    
    /**
     * Main entry point of the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Initializing Online Grocery Ordering System...");
        
        try {
            // Initialize database
            DatabaseConnection.initializeDatabase();
            
            // Start application
            GroceryOrderingApplication app = new GroceryOrderingApplication();
            app.run();
            
        } catch (Exception e) {
            System.err.println("Failed to start application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Clean up database connection
            DatabaseConnection.closeConnection();
        }
    }
    
    /**
     * Main application loop
     */
    public void run() {
        displayWelcomeMessage();
        
        while (isRunning) {
            try {
                if (!authManager.isAuthenticated()) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (Exception e) {
                System.err.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
        }
        
        displayGoodbyeMessage();
    }
    
    /**
     * Display welcome message
     */
    private void displayWelcomeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    WELCOME TO ONLINE GROCERY ORDERING SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("Your one-stop solution for grocery shopping!");
        System.out.println("Please login to continue...");
    }
    
    /**
     * Display goodbye message
     */
    private void displayGoodbyeMessage() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    Good Bye User! Terminating the Program");
        System.out.println("=".repeat(50));
        System.out.println("Thank you for using Online Grocery Ordering System!");
    }
    
    /**
     * Show login menu for unauthenticated users
     */
    private void showLoginMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("           LOGIN MENU");
        System.out.println("=".repeat(40));
        System.out.println("1. Admin Login");
        System.out.println("2. Customer Login");
        System.out.println("3. Customer Registration");
        System.out.println("4. Exit");
        
        System.out.print("\nEnter your choice (1-4): ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                adminLogin();
                break;
            case "2":
                customerLogin();
                break;
            case "3":
                customerService.customerRegistration();
                break;
            case "4":
                isRunning = false;
                break;
            default:
                System.out.println("❌ You have selected an inappropriate option. Kindly select an appropriate option.");
        }
    }
    
    /**
     * Show main menu for authenticated users (US001)
     */
    private void showMainMenu() {
        String userType = authManager.getCurrentSession().getUserType();
        String displayName = authManager.getCurrentSession().getDisplayName();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("           ONLINE GROCERY ORDERING SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("Logged in as: " + displayName + " (" + userType + ")");
        System.out.println("\nPlease select an option:");
        
        if (authManager.isAdmin()) {
            showAdminMenu();
        } else {
            showCustomerMenu();
        }
    }
    
    /**
     * Show admin-specific menu options
     */
    private void showAdminMenu() {
        System.out.println("1. Customer Registration");
        System.out.println("2. Update Customer Details");
        System.out.println("3. Get Customer Order Details");
        System.out.println("4. Customer Search");
        System.out.println("5. Product Search");
        System.out.println("6. Register Product");
        System.out.println("7. Update Product");
        System.out.println("8. Delete Product");
        System.out.println("9. Exit");
        
        System.out.print("\nEnter your choice (1-9): ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                customerService.customerRegistration();
                break;
            case "2":
                // Admin can update any customer - for now, redirect to customer update
                System.out.println("Note: Admin customer update feature - please login as customer to update details.");
                break;
            case "3":
                getCustomerOrderDetailsAdmin();
                break;
            case "4":
                customerService.searchCustomerByName();
                break;
            case "5":
                productService.searchProductByName();
                break;
            case "6":
                productService.productRegistration();
                break;
            case "7":
                productService.updateProduct();
                break;
            case "8":
                productService.deleteProduct();
                break;
            case "9":
                isRunning = false;
                break;
            default:
                System.out.println("❌ You have selected an inappropriate option. Kindly select an appropriate option.");
        }
    }
    
    /**
     * Show customer-specific menu options
     */
    private void showCustomerMenu() {
        System.out.println("1. Update My Details");
        System.out.println("2. My Order History");
        System.out.println("3. Search Products");
        System.out.println("4. Logout");
        System.out.println("5. Exit");
        
        System.out.print("\nEnter your choice (1-5): ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                customerService.updateCustomerDetails();
                break;
            case "2":
                orderService.getCustomerOrderDetails();
                break;
            case "3":
                productService.searchProductByName();
                break;
            case "4":
                authManager.logout();
                System.out.println("✓ Logged out successfully.");
                break;
            case "5":
                isRunning = false;
                break;
            default:
                System.out.println("❌ You have selected an inappropriate option. Kindly select an appropriate option.");
        }
    }
    
    /**
     * Handle admin login
     */
    private void adminLogin() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("           ADMIN LOGIN");
        System.out.println("=".repeat(40));
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        // Validate login attempt
        String validationError = authManager.validateLoginAttempt(username, password, "ADMIN");
        if (validationError != null) {
            System.out.println("❌ " + validationError);
            return;
        }
        
        if (authManager.authenticateAdmin(username, password)) {
            System.out.println("✓ Admin login successful!");
            System.out.println("Welcome, " + username + "!");
        } else {
            System.out.println("❌ Please Enter Correct UserName and Password");
        }
    }
    
    /**
     * Handle customer login
     */
    private void customerLogin() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("          CUSTOMER LOGIN");
        System.out.println("=".repeat(40));
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        // Validate login attempt
        String validationError = authManager.validateLoginAttempt(email, password, "CUSTOMER");
        if (validationError != null) {
            System.out.println("❌ " + validationError);
            return;
        }
        
        if (authManager.authenticateCustomer(email, password)) {
            System.out.println("✓ Customer login successful!");
            System.out.println("Welcome, " + authManager.getCurrentSession().getDisplayName() + "!");
        } else {
            System.out.println("❌ Please Enter Correct Email and Password");
        }
    }
    
    /**
     * Get customer order details for admin
     */
    private void getCustomerOrderDetailsAdmin() {
        System.out.print("Enter Customer ID to view order details: ");
        String customerId = scanner.nextLine().trim();
        
        if (customerId.isEmpty()) {
            System.out.println("❌ Please enter a valid Customer ID.");
            return;
        }
        
        // Sanitize and validate input
        customerId = ValidationUtils.sanitizeInput(customerId);
        
        if (!ValidationUtils.isValidCustomerId(customerId)) {
            System.out.println("❌ Invalid Customer ID format. Please enter 6 digits.");
            return;
        }
        
        orderService.getCustomerOrderDetailsByAdmin(customerId);
    }
}
