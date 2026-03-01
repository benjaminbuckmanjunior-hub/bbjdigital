package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.example.dao.MemberDAO;
import com.example.model.Member;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
            
            String name = jsonRequest.get("name").getAsString();
            String password = jsonRequest.get("password").getAsString();
            
            // Auto-generate email from name
            String email = generateEmail(name);
            
            MemberDAO memberDao = new MemberDAO();
            
            // Check if email already exists
            if (memberDao.getMemberByEmail(email) != null) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Email already exists");
                response.setStatus(400);
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            
            Member member = new Member(name, email, password);
            if (memberDao.addMember(member)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Registration successful");
                jsonResponse.addProperty("email", email);
                response.getWriter().write(jsonResponse.toString());
            } else {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Registration failed");
                response.setStatus(500);
                response.getWriter().write(jsonResponse.toString());
            }
        } catch (Exception e) {
            JsonObject jsonResponse = new JsonObject();
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Server error: " + e.getMessage());
            response.setStatus(500);
            response.getWriter().write(jsonResponse.toString());
            e.printStackTrace();
        }
    }
    
    private String generateEmail(String name) {
        long timestamp = System.nanoTime();
        return name.toLowerCase().replaceAll("\\s+", ".") + timestamp + "@bbj-church.local";
    }
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }
}
