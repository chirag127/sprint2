package com.grocery.services;

import com.grocery.database.DatabaseConnection;
import com.grocery.models.Product;
import com.grocery.utils.ValidationUtils;
import com.grocery.auth.AuthenticationManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Product Service Class
 * Handles all product-related operations including registration, updates, deletion, and search
 *
 * @author Chirag Singhal
 * @version 1.0
 */
public class ProductService {

    private Scanner scanner;
    private AuthenticationManager authManager;

    public ProductService() {
        this.scanner = new Scanner(System.in);
        this.authManager = AuthenticationManager.getInstance();
    }

    /**
     * Product registration process (US007)
     * Handles the complete product registration workflow - Admin only
     */
    public void productRegistration() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           PRODUCT REGISTRATION");
        System.out.println("=".repeat(50));

        // Check if admin is authenticated
        if (!authManager.isAdmin()) {
            System.out.println("❌ Access denied. Admin privileges required.");
            return;
        }

        try {
            Product product = new Product();

            // Get and validate product name
            String productName = getValidatedProductName();
            product.setProductName(productName);

            // Get and validate price
            double price = getValidatedPrice();
            product.setPrice(price);

            // Get and validate quantity
            int quantity = getValidatedQuantity();
            product.setQuantity(quantity);

            // Save product to database
            if (saveProduct(product)) {
                System.out.println("\n" + "✓".repeat(50));
                System.out.println("    PRODUCT REGISTRATION SUCCESSFUL!");
                System.out.println("✓".repeat(50));
                System.out.println("Product ID: " + product.getProductId());
                System.out.println("Name: " + productName);
                System.out.println("Price: " + product.getFormattedPrice());
                System.out.println("Quantity: " + quantity);
            } else {
                System.out.println("\n❌ Product registration failed. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error during product registration: " + e.getMessage());
        }
    }

    /**
     * Update product details (US008)
     * Allows admin to update product information
     */
    public void updateProduct() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           UPDATE PRODUCT");
        System.out.println("=".repeat(50));

        // Check if admin is authenticated
        if (!authManager.isAdmin()) {
            System.out.println("❌ Access denied. Admin privileges required.");
            return;
        }

        try {
            // Get product ID to update
            int productId = getValidatedProductIdForUpdate();
            if (productId == -1) return;

            // Load current product details
            Product product = getProductById(productId);
            if (product == null) {
                System.out.println("❌ Product not found.");
                return;
            }

            System.out.println("\nCurrent Product Details:");
            System.out.println(product.getAdminDisplayInfo());

            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Product Name");
            System.out.println("2. Price");
            System.out.println("3. Quantity");
            System.out.println("4. Reserved Quantity");
            System.out.println("5. Customer ID (for reservation)");
            System.out.println("6. Cancel");

            System.out.print("\nEnter your choice (1-6): ");
            String choice = scanner.nextLine().trim();

            boolean updated = false;

            switch (choice) {
                case "1":
                    String newName = getValidatedProductName();
                    product.setProductName(newName);
                    updated = updateProductInDatabase(product);
                    if (updated) System.out.println("✓ Product name updated successfully.");
                    break;

                case "2":
                    double newPrice = getValidatedPrice();
                    product.setPrice(newPrice);
                    updated = updateProductInDatabase(product);
                    if (updated) System.out.println("✓ Product price updated successfully.");
                    break;

                case "3":
                    int newQuantity = getValidatedQuantity();
                    product.setQuantity(newQuantity);
                    updated = updateProductInDatabase(product);
                    if (updated) System.out.println("✓ Product quantity updated successfully.");
                    break;

                case "4":
                    int newReserved = getValidatedReservedQuantity(product.getQuantity());
                    product.setReserved(newReserved);
                    updated = updateProductInDatabase(product);
                    if (updated) System.out.println("✓ Reserved quantity updated successfully.");
                    break;

                case "5":
                    String newCustomerId = getValidatedCustomerIdForReservation();
                    product.setCustomerId(newCustomerId);
                    updated = updateProductInDatabase(product);
                    if (updated) System.out.println("✓ Customer ID updated successfully.");
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
            System.err.println("Error updating product: " + e.getMessage());
        }
    }

    /**
     * Delete product (US009)
     * Allows admin to delete a product
     */
    public void deleteProduct() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           DELETE PRODUCT");
        System.out.println("=".repeat(50));

        // Check if admin is authenticated
        if (!authManager.isAdmin()) {
            System.out.println("❌ Access denied. Admin privileges required.");
            return;
        }

        try {
            // Get product ID to delete
            int productId = getValidatedProductIdForUpdate();
            if (productId == -1) return;

            // Load product details for confirmation
            Product product = getProductById(productId);
            if (product == null) {
                System.out.println("❌ Product not found.");
                return;
            }

            System.out.println("\nProduct to be deleted:");
            System.out.println(product.getAdminDisplayInfo());

            // Confirm deletion
            System.out.print("\nAre you sure you want to delete this product? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (!confirmation.equals("yes") && !confirmation.equals("y")) {
                System.out.println("Product deletion cancelled.");
                return;
            }

            // Delete product from database
            if (deleteProductFromDatabase(productId)) {
                System.out.println("\n✓ Product deleted successfully.");
            } else {
                System.out.println("\n❌ Product deletion failed. Please try again.");
            }

        } catch (Exception e) {
            System.err.println("Error deleting product: " + e.getMessage());
        }
    }

    /**
     * Search product by name (US006)
     * Allows customers to search for products by name
     */
    public void searchProductByName() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           PRODUCT SEARCH");
        System.out.println("=".repeat(50));

        // Check if user is authenticated (customer or admin)
        if (!authManager.isAuthenticated()) {
            System.out.println("❌ Please log in to search products.");
            return;
        }

        System.out.print("Enter product name to search: ");
        String searchName = scanner.nextLine().trim();

        if (searchName.isEmpty()) {
            System.out.println("❌ Please enter a valid product name to search.");
            return;
        }

        // Sanitize input
        searchName = ValidationUtils.sanitizeInput(searchName);

        try {
            List<Product> products = searchProductsByName(searchName);

            if (products.isEmpty()) {
                System.out.println("❌ Product not found.");
            } else {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("                    SEARCH RESULTS");
                System.out.println("=".repeat(80));

                for (int i = 0; i < products.size(); i++) {
                    Product product = products.get(i);
                    System.out.println("\n--- Product " + (i + 1) + " ---");

                    if (authManager.isAdmin()) {
                        System.out.println(product.getAdminDisplayInfo());
                    } else {
                        System.out.println(product.getCustomerDisplayInfo());

                        // Show add to cart option for customers
                        if (product.isInStock()) {
                            System.out.println("Status: Available for purchase");
                            System.out.println("[ Add to Cart ] - Contact admin to place order");
                        } else {
                            System.out.println("Status: Out of Stock");
                        }
                    }
                }

                System.out.println("\n" + "=".repeat(80));
                System.out.println("Total products found: " + products.size());
            }

        } catch (Exception e) {
            System.err.println("Error searching products: " + e.getMessage());
        }
    }

    // Private helper methods

    private String getValidatedProductName() {
        while (true) {
            System.out.print("Enter product name: ");
            String productName = scanner.nextLine().trim();

            if (ValidationUtils.isValidProductName(productName)) {
                return productName;
            } else {
                System.out.println("❌ Invalid product name. Please enter 2-100 characters.");
            }
        }
    }

    private double getValidatedPrice() {
        while (true) {
            System.out.print("Enter product price: $");
            String priceInput = scanner.nextLine().trim();

            if (ValidationUtils.isNumeric(priceInput)) {
                double price = Double.parseDouble(priceInput);
                if (ValidationUtils.isValidPrice(price)) {
                    return price;
                } else {
                    System.out.println("❌ Price must be greater than 0.");
                }
            } else {
                System.out.println("❌ Invalid price format. Please enter a valid number.");
            }
        }
    }

    private int getValidatedQuantity() {
        while (true) {
            System.out.print("Enter product quantity: ");
            String quantityInput = scanner.nextLine().trim();

            if (ValidationUtils.isInteger(quantityInput)) {
                int quantity = Integer.parseInt(quantityInput);
                if (ValidationUtils.isValidQuantity(quantity)) {
                    return quantity;
                } else {
                    System.out.println("❌ Quantity cannot be negative.");
                }
            } else {
                System.out.println("❌ Invalid quantity format. Please enter a valid integer.");
            }
        }
    }

    private int getValidatedProductIdForUpdate() {
        while (true) {
            System.out.print("Enter product ID to update/delete: ");
            String productIdInput = scanner.nextLine().trim();

            if (ValidationUtils.isInteger(productIdInput)) {
                int productId = Integer.parseInt(productIdInput);
                if (ValidationUtils.isValidProductId(productId)) {
                    return productId;
                } else {
                    System.out.println("❌ Product ID must be greater than 0.");
                }
            } else {
                System.out.println("❌ Invalid product ID format. Please enter a valid integer.");
            }
        }
    }

    private int getValidatedReservedQuantity(int maxQuantity) {
        while (true) {
            System.out.print("Enter reserved quantity (0-" + maxQuantity + "): ");
            String reservedInput = scanner.nextLine().trim();

            if (ValidationUtils.isInteger(reservedInput)) {
                int reserved = Integer.parseInt(reservedInput);
                if (reserved >= 0 && reserved <= maxQuantity) {
                    return reserved;
                } else {
                    System.out.println("❌ Reserved quantity must be between 0 and " + maxQuantity + ".");
                }
            } else {
                System.out.println("❌ Invalid quantity format. Please enter a valid integer.");
            }
        }
    }

    private String getValidatedCustomerIdForReservation() {
        while (true) {
            System.out.print("Enter customer ID for reservation (or 'none' to clear): ");
            String customerIdInput = scanner.nextLine().trim();

            if (customerIdInput.equalsIgnoreCase("none") || customerIdInput.isEmpty()) {
                return null;
            }

            if (ValidationUtils.isValidCustomerId(customerIdInput)) {
                // Check if customer exists
                if (customerExists(customerIdInput)) {
                    return customerIdInput;
                } else {
                    System.out.println("❌ Customer ID not found. Please enter a valid customer ID.");
                }
            } else {
                System.out.println("❌ Invalid customer ID format. Please enter 6 digits.");
            }
        }
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

    private boolean saveProduct(Product product) {
        try {
            String query = """
                INSERT INTO products (product_name, price, quantity, reserved, customer_id)
                VALUES (?, ?, ?, ?, ?)
            """;

            int rowsAffected = DatabaseConnection.executeUpdate(query,
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getReserved(),
                product.getCustomerId()
            );

            if (rowsAffected > 0) {
                // Get the generated product ID
                String selectQuery = "SELECT LAST_INSERT_ID()";
                ResultSet rs = DatabaseConnection.executeQuery(selectQuery);
                if (rs.next()) {
                    product.setProductId(rs.getInt(1));
                }
                rs.close();
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("Error saving product: " + e.getMessage());
            return false;
        }
    }

    private Product getProductById(int productId) {
        try {
            String query = "SELECT * FROM products WHERE product_id = ?";
            ResultSet rs = DatabaseConnection.executeQuery(query, productId);

            if (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setReserved(rs.getInt("reserved"));
                product.setCustomerId(rs.getString("customer_id"));

                rs.close();
                return product;
            }

            rs.close();
            return null;

        } catch (SQLException e) {
            System.err.println("Error retrieving product: " + e.getMessage());
            return null;
        }
    }

    private boolean updateProductInDatabase(Product product) {
        try {
            String query = """
                UPDATE products
                SET product_name = ?, price = ?, quantity = ?, reserved = ?, customer_id = ?, updated_at = CURRENT_TIMESTAMP
                WHERE product_id = ?
            """;

            int rowsAffected = DatabaseConnection.executeUpdate(query,
                product.getProductName(),
                product.getPrice(),
                product.getQuantity(),
                product.getReserved(),
                product.getCustomerId(),
                product.getProductId()
            );

            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating product: " + e.getMessage());
            return false;
        }
    }

    private boolean deleteProductFromDatabase(int productId) {
        try {
            String query = "DELETE FROM products WHERE product_id = ?";
            int rowsAffected = DatabaseConnection.executeUpdate(query, productId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting product: " + e.getMessage());
            return false;
        }
    }

    private List<Product> searchProductsByName(String searchName) {
        List<Product> products = new ArrayList<>();

        try {
            // Use LIKE with wildcards for case-insensitive search
            String query = "SELECT * FROM products WHERE LOWER(product_name) LIKE LOWER(?) ORDER BY product_name";
            String searchPattern = "%" + searchName + "%";

            ResultSet rs = DatabaseConnection.executeQuery(query, searchPattern);

            while (rs.next()) {
                Product product = new Product();
                product.setProductId(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setPrice(rs.getDouble("price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setReserved(rs.getInt("reserved"));
                product.setCustomerId(rs.getString("customer_id"));

                products.add(product);
            }

            rs.close();

        } catch (SQLException e) {
            System.err.println("Error searching products: " + e.getMessage());
        }

        return products;
    }
}
