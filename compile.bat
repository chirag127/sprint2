@echo off
echo ========================================
echo  Online Grocery Ordering System
echo  Compilation and Execution Script
echo ========================================

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher and add it to your PATH
    pause
    exit /b 1
)

echo Java found. Checking version...
java -version

REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: Maven is not installed or not in PATH
    echo You can still compile manually with javac
    goto :manual_compile
)

echo Maven found. Using Maven to build...
mvn clean compile
if %errorlevel% neq 0 (
    echo ERROR: Maven compilation failed
    pause
    exit /b 1
)

echo.
echo ========================================
echo  Compilation successful!
echo ========================================
echo.
echo To run the application:
echo   mvn exec:java -Dexec.mainClass="com.grocery.GroceryOrderingApplication"
echo.
echo Or package and run:
echo   mvn clean package
echo   java -jar target/online-grocery-ordering-1.0.0-shaded.jar
echo.
pause
exit /b 0

:manual_compile
echo.
echo ========================================
echo  Manual Compilation Instructions
echo ========================================
echo.
echo 1. Download MySQL Connector/J from:
echo    https://dev.mysql.com/downloads/connector/j/
echo.
echo 2. Extract the JAR file to a 'lib' directory
echo.
echo 3. Compile with:
echo    javac -cp "lib/*" -d target/classes src/main/java/com/grocery/*.java src/main/java/com/grocery/*/*.java
echo.
echo 4. Run with:
echo    java -cp "target/classes;lib/*" com.grocery.GroceryOrderingApplication
echo.
echo 5. Make sure MySQL is running and database 'grocery_db' exists
echo.
pause
