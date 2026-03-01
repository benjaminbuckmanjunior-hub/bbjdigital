package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.example.dao.MemberDAO;
import com.example.dao.AdminDAO;
import com.example.model.Member;
import com.example.model.Admin;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            BufferedReader reader = request.getReader();
            Gson gson = new Gson();
            JsonObject jsonRequest = gson.fromJson(reader, JsonObject.class);
            
            String email = jsonRequest.get("email").getAsString();
            String password = jsonRequest.get("password").getAsString();
            String userType = jsonRequest.get("userType").getAsString(); // "member" or "admin"
            
            JsonObject jsonResponse = new JsonObject();
            
            if ("member".equals(userType)) {
                MemberDAO memberDao = new MemberDAO();
                if (memberDao.verifyMemberLogin(email, password)) {
                    Member member = memberDao.getMemberByEmail(email);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Login successful");
                    jsonResponse.addProperty("userId", member.getId());
                    jsonResponse.addProperty("userType", "member");
                    jsonResponse.addProperty("name", member.getName());
                    response.getWriter().write(jsonResponse.toString());
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Invalid email or password");
                    response.setStatus(401);
                    response.getWriter().write(jsonResponse.toString());
                }
            } else if ("admin".equals(userType)) {
                AdminDAO adminDao = new AdminDAO();
                if (adminDao.verifyAdminLogin(email, password)) {
                    Admin admin = adminDao.getAdminByEmail(email);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Login successful");
                    jsonResponse.addProperty("userId", admin.getId());
                    jsonResponse.addProperty("userType", "admin");
                    jsonResponse.addProperty("name", admin.getName());
                    response.getWriter().write(jsonResponse.toString());
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Invalid email or password");
                    response.setStatus(401);
                    response.getWriter().write(jsonResponse.toString());
                }
            } else {
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Invalid user type");
                response.setStatus(400);
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
    
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(200);
    }
}
