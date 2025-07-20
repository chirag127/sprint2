package com.grocery.database;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

/**
 * Database Connection Utility Class
 * Handles secure database connections with SQL injection prevention
 * 
 * @author Chirag Singhal
 * @version 1.0
 */
public class DatabaseConnection {
    private static final String CONFIG_FILE = "/database.properties";
    private static Connection connection = null;
    private static Properties dbProperties = null;
    
    // Database configuration
    private static String DB_URL;
    private static String DB_USERNAME;
    private static String DB_PASSWORD;
    private static String DB_DRIVER;
    
    static {
        loadDatabaseProperties();
    }
    
    /**
     * Load database properties from configuration file
     */
    private static void loadDatabaseProperties() {
        try {
            dbProperties = new Properties();
            InputStream inputStream = DatabaseConnection.class.getResourceAsStream(CONFIG_FILE);
            
            if (inputStream != null) {
                dbProperties.load(inputStream);
                DB_URL = dbProperties.getProperty("db.url", "jdbc:mysql://localhost:3306/grocery_db");
                DB_USERNAME = dbProperties.getProperty("db.username", "root");
                DB_PASSWORD = dbProperties.getProperty("db.password", "");
                DB_DRIVER = dbProperties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                // Default configuration if properties file not found
                DB_URL = "jdbc:mysql://localhost:3306/grocery_db";
                DB_USERNAME = "root";
                DB_PASSWORD = "";
                DB_DRIVER = "com.mysql.cj.jdbc.Driver";
            }
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            // Use default values
            DB_URL = "jdbc:mysql://localhost:3306/grocery_db";
            DB_USERNAME = "root";
            DB_PASSWORD = "";
            DB_DRIVER = "com.mysql.cj.jdbc.Driver";
        }
    }
    
    /**
     * Establish database connection
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName(DB_DRIVER);
                connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
                
                // Set connection properties for security
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + e.getMessage());
        }
    }
    
    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Execute prepared statement with parameters to prevent SQL injection
     * @param query SQL query with placeholders
     * @param parameters Parameters for the query
     * @return ResultSet
     * @throws SQLException if query execution fails
     */
    public static ResultSet executeQuery(String query, Object... parameters) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        
        // Set parameters safely
        for (int i = 0; i < parameters.length; i++) {
            pstmt.setObject(i + 1, parameters[i]);
        }
        
        return pstmt.executeQuery();
    }
    
    /**
     * Execute update/insert/delete operations with parameters
     * @param query SQL query with placeholders
     * @param parameters Parameters for the query
     * @return Number of affected rows
     * @throws SQLException if query execution fails
     */
    public static int executeUpdate(String query, Object... parameters) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(query);
        
        // Set parameters safely
        for (int i = 0; i < parameters.length; i++) {
            pstmt.setObject(i + 1, parameters[i]);
        }
        
        int result = pstmt.executeUpdate();
        conn.commit(); // Commit transaction
        return result;
    }
    
    /**
     * Initialize database tables
     */
    public static void initializeDatabase() {
        try {
            createTables();
            insertDefaultData();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
    
    /**
     * Create necessary database tables
     * @throws SQLException if table creation fails
     */
    private static void createTables() throws SQLException {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        
        // Create customers table
        String createCustomersTable = """
            CREATE TABLE IF NOT EXISTS customers (
                customer_id VARCHAR(6) PRIMARY KEY,
                full_name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                address TEXT NOT NULL,
                contact_number VARCHAR(10) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
        """;
        
        // Create products table
        String createProductsTable = """
            CREATE TABLE IF NOT EXISTS products (
                product_id INT AUTO_INCREMENT PRIMARY KEY,
                product_name VARCHAR(100) NOT NULL,
                price DECIMAL(10,2) NOT NULL,
                quantity INT NOT NULL DEFAULT 0,
                reserved INT NOT NULL DEFAULT 0,
                customer_id VARCHAR(6),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL
            )
        """;
        
        // Create orders table
        String createOrdersTable = """
            CREATE TABLE IF NOT EXISTS orders (
                order_id INT AUTO_INCREMENT PRIMARY KEY,
                customer_id VARCHAR(6) NOT NULL,
                product_id INT NOT NULL,
                order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                order_amount DECIMAL(10,2) NOT NULL,
                quantity_ordered INT NOT NULL,
                FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
                FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
            )
        """;
        
        // Create admin table
        String createAdminTable = """
            CREATE TABLE IF NOT EXISTS admin (
                admin_id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        stmt.execute(createCustomersTable);
        stmt.execute(createProductsTable);
        stmt.execute(createOrdersTable);
        stmt.execute(createAdminTable);
        
        conn.commit();
        stmt.close();
    }
    
    /**
     * Insert default admin data
     * @throws SQLException if insertion fails
     */
    private static void insertDefaultData() throws SQLException {
        // Insert default admin user
        String checkAdmin = "SELECT COUNT(*) FROM admin WHERE username = ?";
        ResultSet rs = executeQuery(checkAdmin, "admin");
        
        if (rs.next() && rs.getInt(1) == 0) {
            String insertAdmin = "INSERT INTO admin (username, password) VALUES (?, ?)";
            executeUpdate(insertAdmin, "admin", "admin123");
        }
        rs.close();
    }
}
