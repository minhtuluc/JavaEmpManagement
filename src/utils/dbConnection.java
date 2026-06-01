package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class dbConnection {
    private static final String BASE_URL = "jdbc:mysql://127.0.0.1:3306";
    private static final String DB_NAME = "quanlynhansu";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        // First try connecting directly to the target database
        String url = BASE_URL + "/" + DB_NAME;
        try {
            return DriverManager.getConnection(url, USER, PASSWORD);
        } catch (SQLException e) {
            int code = e.getErrorCode();
            if (code == 1049) {
                System.out.println("Database '" + DB_NAME + "' not found. Running init.sql...");
                if (initializeDatabase()) {
                    try {
                        return DriverManager.getConnection(url, USER, PASSWORD);
                    } catch (SQLException e2) {
                        System.err.println("Still cannot connect after init: " + e2.getMessage());
                    }
                }
            } else if (code == 0 || code == 2003 || code == 2002) {
                System.err.println("Không thể kết nối đến MySQL tại localhost:3306."
                    + " Hãy đảm bảo MySQL server đang chạy.");
            } else {
                System.err.println("Lỗi database (" + code + "): " + e.getMessage());
            }
        }
        return null;
    }

    private static boolean initializeDatabase() {
        File sqlFile = findInitSql();
        if (sqlFile == null) {
            System.err.println("Cannot find database/init.sql");
            return false;
        }

        try (Connection conn = DriverManager.getConnection(BASE_URL + "?allowMultiQueries=true", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE DATABASE IF NOT EXISTS " + DB_NAME
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            stmt.execute("USE " + DB_NAME);

            try {
                stmt.execute("SELECT COUNT(*) FROM Employees");
                System.out.println("Tables already exist, skipping init.");
                return true;
            } catch (SQLException e) {
                // Tables don't exist, run init.sql
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(sqlFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (trimmed.startsWith("--") || trimmed.startsWith("#") || trimmed.isEmpty()) {
                        continue;
                    }
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                System.err.println("Error reading init.sql: " + e.getMessage());
                return false;
            }

            String[] statements = sb.toString().split(";");
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (trimmed.isEmpty()) continue;

                String upper = trimmed.toUpperCase();
                if (upper.startsWith("USE ") || upper.startsWith("CREATE DATABASE")) {
                    continue;
                }

                try {
                    stmt.execute(trimmed);
                } catch (SQLException e) {
                    System.err.println("SQL error executing: " + trimmed.substring(0, Math.min(60, trimmed.length())));
                    System.err.println("Error: " + e.getMessage());
                }
            }

            System.out.println("Database initialized successfully.");
            return true;

        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static File findInitSql() {
        // Try relative to working directory
        File cwd = new File("database/init.sql");
        if (cwd.exists()) return cwd;

        // Try relative to class location (bin/ -> project root)
        try {
            java.net.URL classUrl = dbConnection.class
                    .getProtectionDomain().getCodeSource().getLocation();
            if (classUrl != null) {
                File classDir = new File(classUrl.toURI());
                File parent = classDir.isDirectory() ? classDir : classDir.getParentFile();
                // Look up from bin/ to project root
                File fromClass = new File(parent.getParentFile(), "database/init.sql");
                if (fromClass.exists()) return fromClass;
                // Handle the case where class is in project root (no bin/)
                File fromRoot = new File(parent, "database/init.sql");
                if (fromRoot.exists()) return fromRoot;
            }
        } catch (URISyntaxException e) {
            // fall through
        }
        return null;
    }
}