# Installation Guide

This guide provides detailed instructions for setting up and running the Online Grocery Ordering System.

## 📋 Prerequisites

### Required Software
- **Java Development Kit (JDK) 17 or higher**
  - Download from: https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/
  - Verify installation: `java -version`

- **MySQL Server 8.0 or higher**
  - Download from: https://dev.mysql.com/downloads/mysql/
  - Alternative: Use XAMPP, WAMP, or Docker

- **Maven 3.6 or higher** (Optional but recommended)
  - Download from: https://maven.apache.org/download.cgi
  - Verify installation: `mvn -version`

- **Git** (for cloning the repository)
  - Download from: https://git-scm.com/downloads

## 🚀 Quick Start

### Option 1: Using Maven (Recommended)

1. **Clone the repository**:
   ```bash
   git clone https://github.com/chirag127/sprint2.git
   cd sprint2
   ```

2. **Set up MySQL database**:
   ```sql
   CREATE DATABASE grocery_db;
   ```

3. **Configure database connection**:
   - Copy `.env.example` to `.env`
   - Update database credentials in `.env` file

4. **Build and run**:
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.grocery.GroceryOrderingApplication"
   ```

### Option 2: Manual Compilation

1. **Download MySQL Connector/J**:
   - Download from: https://dev.mysql.com/downloads/connector/j/
   - Extract `mysql-connector-java-x.x.x.jar` to `lib/` directory

2. **Compile the application**:
   ```bash
   # Windows
   javac -cp "lib/*" -d target/classes src/main/java/com/grocery/*.java src/main/java/com/grocery/*/*.java

   # Linux/Mac
   javac -cp "lib/*" -d target/classes src/main/java/com/grocery/*.java src/main/java/com/grocery/*/*.java
   ```

3. **Run the application**:
   ```bash
   # Windows
   java -cp "target/classes;lib/*" com.grocery.GroceryOrderingApplication

   # Linux/Mac
   java -cp "target/classes:lib/*" com.grocery.GroceryOrderingApplication
   ```

## 🗄️ Database Setup

### MySQL Configuration

1. **Install MySQL Server**:
   - Download and install MySQL Server 8.0+
   - Remember the root password during installation

2. **Create Database**:
   ```sql
   mysql -u root -p
   CREATE DATABASE grocery_db;
   USE grocery_db;
   ```

3. **Configure Connection**:
   Update `src/main/resources/database.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/grocery_db
   db.username=root
   db.password=your_password_here
   ```

### Alternative: Using Docker

```bash
# Run MySQL in Docker
docker run --name mysql-grocery -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=grocery_db -p 3306:3306 -d mysql:8.0

# Update database.properties
db.url=jdbc:mysql://localhost:3306/grocery_db
db.username=root
db.password=password
```

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the project root:
```env
DB_URL=jdbc:mysql://localhost:3306/grocery_db
DB_USERNAME=root
DB_PASSWORD=your_password
```

### Database Properties

Update `src/main/resources/database.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/grocery_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.username=root
db.password=your_password_here
db.driver=com.mysql.cj.jdbc.Driver
```

## 🏃‍♂️ Running the Application

### Using Maven
```bash
# Method 1: Direct execution
mvn clean compile exec:java -Dexec.mainClass="com.grocery.GroceryOrderingApplication"

# Method 2: Package and run
mvn clean package
java -jar target/online-grocery-ordering-1.0.0-shaded.jar
```

### Using Batch/Shell Scripts
```bash
# Windows
compile.bat

# Linux/Mac
chmod +x run.sh
./run.sh
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run with Coverage
```bash
mvn clean test jacoco:report
```

### View Coverage Report
Open `target/site/jacoco/index.html` in your browser

## 🔐 Default Credentials

### Admin Login
- **Username**: `admin`
- **Password**: `admin123`

### Customer Registration
- Use option 3 from the login menu to register new customers
- Follow the validation requirements for each field

## 🛠️ Troubleshooting

### Common Issues

#### 1. "Java not found" Error
```bash
# Check Java installation
java -version

# If not installed, download from:
# https://adoptium.net/
```

#### 2. "MySQL Connection Failed"
- Verify MySQL is running: `mysql -u root -p`
- Check database exists: `SHOW DATABASES;`
- Verify credentials in `database.properties`

#### 3. "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
- Download MySQL Connector/J
- Add JAR to classpath or `lib/` directory

#### 4. "Maven not found"
- Download Maven from: https://maven.apache.org/download.cgi
- Add to PATH environment variable
- Or use manual compilation method

#### 5. "Access denied for user"
```sql
# Reset MySQL password
ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
```

### Database Issues

#### Reset Database
```sql
DROP DATABASE IF EXISTS grocery_db;
CREATE DATABASE grocery_db;
```

#### Check Tables
```sql
USE grocery_db;
SHOW TABLES;
DESCRIBE customers;
DESCRIBE products;
DESCRIBE orders;
DESCRIBE admin;
```

### Performance Issues

#### Increase Memory
```bash
java -Xmx512m -jar target/online-grocery-ordering-1.0.0-shaded.jar
```

#### Database Optimization
```sql
# Add indexes for better performance
CREATE INDEX idx_customer_email ON customers(email);
CREATE INDEX idx_product_name ON products(product_name);
CREATE INDEX idx_order_customer ON orders(customer_id);
```

## 📁 Directory Structure After Setup

```
sprint2/
├── src/
│   ├── main/
│   │   ├── java/com/grocery/
│   │   └── resources/
│   └── test/
├── target/
│   ├── classes/
│   └── online-grocery-ordering-1.0.0-shaded.jar
├── lib/
│   └── mysql-connector-java-x.x.x.jar
├── .env
├── pom.xml
├── README.md
└── CHANGELOG.md
```

## 🔄 Updates and Maintenance

### Update Dependencies
```bash
mvn versions:display-dependency-updates
mvn versions:use-latest-versions
```

### Backup Database
```bash
mysqldump -u root -p grocery_db > backup.sql
```

### Restore Database
```bash
mysql -u root -p grocery_db < backup.sql
```

## 📞 Support

If you encounter any issues:

1. Check this installation guide
2. Review the [README.md](README.md) file
3. Check the [CHANGELOG.md](CHANGELOG.md) for known issues
4. Open an issue on [GitHub](https://github.com/chirag127/sprint2/issues)

## 🎯 Next Steps

After successful installation:

1. Login with admin credentials
2. Register some test customers
3. Add sample products
4. Test the complete workflow
5. Explore all menu options

---

**Last Updated**: 2025-07-20T05:52:41.418Z  
**Version**: 1.0.0
