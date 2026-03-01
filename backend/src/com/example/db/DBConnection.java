package com.example.db;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    
    public static Connection getConnection() {
        try {
            // Load configuration based on environment
            String env = System.getenv("ENVIRONMENT");
            env = env != null ? env : "local";
            
            String url, username, password;
            
            if ("production".equals(env)) {
                // TiDB Cloud Production
                url = "jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/bbj?useSSL=true&serverTimezone=UTC";
                username = "nrMPj1ECajN3NtY.root";
                password = "Gt19N5jWU7BMaDl5";
            } else {
                // Local Development
                url = "jdbc:mysql://localhost:1532/bbj?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                username = "root";
                password = "fire@1532";
            }
            
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ Database connected (" + env + ")");
            return conn;
        } catch(Exception e) {
            System.err.println("✗ Database connection failed!");
            e.printStackTrace();
        }
        return null;
    }
    
    // Test connection
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
