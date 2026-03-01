package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.example.dao.EventDAO;
import com.example.model.Event;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/events/*")
public class EventServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = request.getPathInfo();
            String queryString = request.getQueryString();
            EventDAO eventDao = new EventDAO();
            Gson gson = new Gson();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                List<Event> events;
                if (queryString != null && queryString.contains("upcoming")) {
                    events = eventDao.getUpcomingEvents();
                } else {
                    events = eventDao.getAllEvents();
                }
                
                JsonArray jsonArray = new JsonArray();
                for (Event event : events) {
                    JsonObject obj = gson.toJsonTree(event).getAsJsonObject();
                    jsonArray.add(obj);
                }
                response.getWriter().write(jsonArray.toString());
            } else {
                int eventId = Integer.parseInt(pathInfo.substring(1));
                Event event = eventDao.getEventById(eventId);
                if (event != null) {
                    response.getWriter().write(gson.toJson(event));
                } else {
                    response.setStatus(404);
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Event not found");
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
            Event event = gson.fromJson(reader, Event.class);
            
            EventDAO eventDao = new EventDAO();
            if (eventDao.addEvent(event)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Event created successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to create event");
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
            Event event = gson.fromJson(reader, Event.class);
            
            EventDAO eventDao = new EventDAO();
            if (eventDao.updateEvent(event)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Event updated successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update event");
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
            int eventId = Integer.parseInt(pathInfo.substring(1));
            
            EventDAO eventDao = new EventDAO();
            if (eventDao.deleteEvent(eventId)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Event deleted successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to delete event");
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
