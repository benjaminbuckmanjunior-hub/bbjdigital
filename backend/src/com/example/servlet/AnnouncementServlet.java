package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.example.dao.AnnouncementDAO;
import com.example.model.Announcement;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/announcements/*")
public class AnnouncementServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = request.getPathInfo();
            AnnouncementDAO announcementDao = new AnnouncementDAO();
            Gson gson = new Gson();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all announcements
                List<Announcement> announcements = announcementDao.getAllAnnouncements();
                JsonArray jsonArray = new JsonArray();
                for (Announcement announcement : announcements) {
                    JsonObject obj = gson.toJsonTree(announcement).getAsJsonObject();
                    jsonArray.add(obj);
                }
                response.getWriter().write(jsonArray.toString());
            } else {
                // Get specific announcement
                int announcementId = Integer.parseInt(pathInfo.substring(1));
                Announcement announcement = announcementDao.getAnnouncementById(announcementId);
                if (announcement != null) {
                    response.getWriter().write(gson.toJson(announcement));
                } else {
                    response.setStatus(404);
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Announcement not found");
                    response.getWriter().write(jsonResponse.toString());
                }
            }
        } catch (Exception e) {
            response.setStatus(500);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace();
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            Announcement announcement = gson.fromJson(reader, Announcement.class);
            
            AnnouncementDAO announcementDao = new AnnouncementDAO();
            if (announcementDao.addAnnouncement(announcement)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Announcement created successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to create announcement");
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (Exception e) {
            response.setStatus(500);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace();
        }
    }
    
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            Announcement announcement = gson.fromJson(reader, Announcement.class);
            
            AnnouncementDAO announcementDao = new AnnouncementDAO();
            if (announcementDao.updateAnnouncement(announcement)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Announcement updated successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update announcement");
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (Exception e) {
            response.setStatus(500);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace();
        }
    }
    
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = request.getPathInfo();
            int announcementId = Integer.parseInt(pathInfo.substring(1));
            
            AnnouncementDAO announcementDao = new AnnouncementDAO();
            if (announcementDao.deleteAnnouncement(announcementId)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Announcement deleted successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to delete announcement");
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (Exception e) {
            response.setStatus(500);
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace();
        }
    }
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }
}
