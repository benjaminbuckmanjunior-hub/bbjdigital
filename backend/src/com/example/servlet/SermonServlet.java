package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.example.dao.SermonDAO;
import com.example.model.Sermon;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/sermons/*")
public class SermonServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = request.getPathInfo();
            SermonDAO sermonDao = new SermonDAO();
            Gson gson = new Gson();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all sermons
                List<Sermon> sermons = sermonDao.getAllSermons();
                JsonArray jsonArray = new JsonArray();
                for (Sermon sermon : sermons) {
                    JsonObject obj = gson.toJsonTree(sermon).getAsJsonObject();
                    jsonArray.add(obj);
                }
                response.getWriter().write(jsonArray.toString());
            } else {
                // Get specific sermon
                int sermonId = Integer.parseInt(pathInfo.substring(1));
                Sermon sermon = sermonDao.getSermonById(sermonId);
                if (sermon != null) {
                    response.getWriter().write(gson.toJson(sermon));
                } else {
                    response.setStatus(404);
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Sermon not found");
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
            Sermon sermon = gson.fromJson(reader, Sermon.class);
            
            SermonDAO sermonDao = new SermonDAO();
            if (sermonDao.addSermon(sermon)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Sermon uploaded successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to upload sermon");
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
            Sermon sermon = gson.fromJson(reader, Sermon.class);
            
            SermonDAO sermonDao = new SermonDAO();
            if (sermonDao.updateSermon(sermon)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Sermon updated successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update sermon");
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
            int sermonId = Integer.parseInt(pathInfo.substring(1));
            
            SermonDAO sermonDao = new SermonDAO();
            if (sermonDao.deleteSermon(sermonId)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Sermon deleted successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to delete sermon");
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
