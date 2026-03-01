package com.example.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import org.json.JSONObject;
import org.json.JSONArray;
import com.example.config.ConfigManager;

public class B2FileUploadService {
    
    private static String authToken = null;
    private static String apiUrl = null;
    private static String uploadUrl = null;
    private static String bucketId = null;
    
    private static final String B2_API_URL = "https://api.backblazeb2.com/b2api/v2";
    
    static {
        try {
            authenticate();
        } catch (Exception e) {
            System.err.println("B2 Authentication failed on startup");
            e.printStackTrace();
        }
    }

    /**
     * Authenticate with Backblaze B2
     */
    public static void authenticate() throws Exception {
        String keyId = ConfigManager.getB2KeyId();
        String appKey = ConfigManager.getB2ApplicationKey();
        
        String credentials = keyId + ":" + appKey;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        
        URL url = new URL(B2_API_URL + "/b2_authorize_account");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Basic " + encodedCredentials);
        conn.setRequestProperty("Content-Length", "0");
        
        if (conn.getResponseCode() == 200) {
            String response = new String(conn.getInputStream().readAllBytes());
            JSONObject json = new JSONObject(response);
            
            authToken = json.getString("authorizationToken");
            apiUrl = json.getString("apiUrl");
            
            // Get bucket info
            getBucketInfo();
            System.out.println("✓ B2 Authentication successful");
        } else {
            throw new Exception("B2 Authentication failed: " + conn.getResponseCode());
        }
    }

    /**
     * Get bucket ID from bucket name
     */
    private static void getBucketInfo() throws Exception {
        String bucketName = ConfigManager.getB2BucketName();
        URL url = new URL(apiUrl + "/b2api/v2/b2_list_buckets");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");
        
        String body = "{\"accountId\": \"" + apiUrl.split("/")[2] + "\"}";
        conn.getOutputStream().write(body.getBytes());
        
        if (conn.getResponseCode() == 200) {
            String response = new String(conn.getInputStream().readAllBytes());
            JSONObject json = new JSONObject(response);
            JSONArray buckets = json.getJSONArray("buckets");
            
            for (int i = 0; i < buckets.length(); i++) {
                JSONObject bucket = buckets.getJSONObject(i);
                if (bucket.getString("bucketName").equals(bucketName)) {
                    bucketId = bucket.getString("bucketId");
                    break;
                }
            }
        }
    }

    /**
     * Upload file to B2
     */
    public static String uploadFile(File file) throws Exception {
        if (authToken == null) {
            authenticate();
        }
        
        String fileName = System.currentTimeMillis() + "_" + file.getName();
        byte[] fileData = Files.readAllBytes(file.toPath());
        
        // Get upload URL
        URL url = new URL(apiUrl + "/b2api/v2/b2_get_upload_url");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");
        
        String body = "{\"bucketId\": \"" + bucketId + "\"}";
        conn.getOutputStream().write(body.getBytes());
        
        JSONObject uploadInfo = new JSONObject(new String(conn.getInputStream().readAllBytes()));
        uploadUrl = uploadInfo.getString("uploadUrl");
        String uploadAuthToken = uploadInfo.getString("authorizationToken");
        
        // Upload file
        URL uploadConnection = new URL(uploadUrl);
        HttpURLConnection uploadConn = (HttpURLConnection) uploadConnection.openConnection();
        uploadConn.setRequestMethod("POST");
        uploadConn.setRequestProperty("Authorization", uploadAuthToken);
        uploadConn.setRequestProperty("X-Bz-File-Name", fileName);
        uploadConn.setRequestProperty("X-Bz-Content-Type", "application/octet-stream");
        uploadConn.setRequestProperty("Content-Length", String.valueOf(fileData.length));
        
        uploadConn.getOutputStream().write(fileData);
        
        if (uploadConn.getResponseCode() == 200) {
            JSONObject uploadResponse = new JSONObject(new String(uploadConn.getInputStream().readAllBytes()));
            String fileUrl = "https://f000.backblazeb2.com/file/" + ConfigManager.getB2BucketName() + "/" + fileName;
            System.out.println("✓ File uploaded: " + fileUrl);
            return fileUrl;
        } else {
            throw new Exception("File upload failed: " + uploadConn.getResponseCode());
        }
    }

    /**
     * Delete file from B2
     */
    public static boolean deleteFile(String fileName) throws Exception {
        if (authToken == null) {
            authenticate();
        }
        
        URL url = new URL(apiUrl + "/b2api/v2/b2_delete_file_version");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");
        
        // Get file info first
        String listUrl = apiUrl + "/b2api/v2/b2_list_file_names?bucketId=" + bucketId + "&fileName=" + fileName;
        HttpURLConnection listConn = (HttpURLConnection) new URL(listUrl).openConnection();
        listConn.setRequestProperty("Authorization", authToken);
        
        JSONObject listResponse = new JSONObject(new String(listConn.getInputStream().readAllBytes()));
        JSONArray files = listResponse.getJSONArray("files");
        
        if (files.length() > 0) {
            JSONObject fileObj = files.getJSONObject(0);
            String fileId = fileObj.getString("fileId");
            
            String body = "{\"fileId\": \"" + fileId + "\", \"fileName\": \"" + fileName + "\"}";
            conn.getOutputStream().write(body.getBytes());
            
            return conn.getResponseCode() == 200;
        }
        return false;
    }

    /**
     * Get list of files in bucket
     */
    public static JSONArray listFiles(int limit) throws Exception {
        if (authToken == null) {
            authenticate();
        }
        
        URL url = new URL(apiUrl + "/b2api/v2/b2_list_file_names?bucketId=" + bucketId + "&maxFileCount=" + limit);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", authToken);
        
        if (conn.getResponseCode() == 200) {
            JSONObject response = new JSONObject(new String(conn.getInputStream().readAllBytes()));
            return response.getJSONArray("files");
        }
        return new JSONArray();
    }

    /**
     * Test B2 connection
     */
    public static boolean testConnection() {
        try {
            authenticate();
            return authToken != null;
        } catch (Exception e) {
            System.err.println("B2 Connection test failed: " + e.getMessage());
            return false;
        }
    }
}
