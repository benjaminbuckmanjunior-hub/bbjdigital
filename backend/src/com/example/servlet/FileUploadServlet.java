package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.example.service.B2FileUploadService;
import com.example.dao.SermonDAO;
import com.example.model.Sermon;
import java.io.File;
import java.io.IOException;

@WebServlet("/api/sermons/upload")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 524288000, maxRequestSize = 524288000)
public class FileUploadServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            // Get form parameters
            String title = request.getParameter("title");
            String description = request.getParameter("description");
            Part filePart = request.getPart("file");
            int uploadedBy = Integer.parseInt(request.getParameter("uploadedBy"));
            
            if (title == null || filePart == null) {
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("message", "Missing required fields");
                response.setStatus(400);
                response.getWriter().write(error.toString());
                return;
            }
            
            // Get file extension
            String fileName = filePart.getSubmittedFileName();
            String fileType = getFileType(fileName);
            
            // Create temporary file
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempFile = new File(tempDir, System.currentTimeMillis() + "_" + fileName);
            filePart.write(tempFile.getAbsolutePath());
            
            // Upload to B2
            String fileUrl = B2FileUploadService.uploadFile(tempFile);
            
            // Save to database
            Sermon sermon = new Sermon(title, description, fileUrl, fileType, uploadedBy);
            SermonDAO sermonDao = new SermonDAO();
            
            if (sermonDao.addSermon(sermon)) {
                // Clean up temp file
                tempFile.delete();
                
                JsonObject success = new JsonObject();
                success.addProperty("success", true);
                success.addProperty("message", "Sermon uploaded successfully");
                success.addProperty("fileUrl", fileUrl);
                response.getWriter().write(success.toString());
            } else {
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("message", "Failed to save sermon to database");
                response.setStatus(500);
                response.getWriter().write(error.toString());
            }
        } catch (Exception e) {
            JsonObject error = new JsonObject();
            error.addProperty("success", false);
            error.addProperty("message", "Upload failed: " + e.getMessage());
            response.setStatus(500);
            response.getWriter().write(error.toString());
            e.printStackTrace();
        }
    }
    
    private String getFileType(String fileName) {
        if (fileName.toLowerCase().endsWith(".mp3")) {
            return "mp3";
        } else if (fileName.toLowerCase().endsWith(".mp4")) {
            return "mp4";
        }
        return "unknown";
    }
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }
}
