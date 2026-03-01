package com.example.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties = new Properties();
    private static final String ENV = System.getenv("ENVIRONMENT") != null 
        ? System.getenv("ENVIRONMENT") 
        : "local";  // local, staging, production

    static {
        try {
            String configFile = "/config-" + ENV + ".properties";
            InputStream input = ConfigManager.class.getResourceAsStream(configFile);
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Config file not found: " + configFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static String getEnvironment() {
        return ENV;
    }

    // Database Properties
    public static String getDbUrl() {
        return getProperty("db.url");
    }

    public static String getDbUsername() {
        return getProperty("db.username");
    }

    public static String getDbPassword() {
        return getProperty("db.password");
    }

    // Backblaze B2 Properties
    public static String getB2KeyId() {
        return getProperty("b2.keyId");
    }

    public static String getB2ApplicationKey() {
        return getProperty("b2.applicationKey");
    }

    public static String getB2BucketName() {
        return getProperty("b2.bucketName");
    }

    // File Upload Properties
    public static String getUploadDir() {
        return getProperty("upload.dir", "/uploads");
    }

    public static long getMaxFileSize() {
        String size = getProperty("upload.maxSize", "524288000"); // 500MB default
        return Long.parseLong(size);
    }
}
