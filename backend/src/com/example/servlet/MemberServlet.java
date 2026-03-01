package com.example.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.example.dao.MemberDAO;
import com.example.model.Member;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/members/*")
public class MemberServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        
        try {
            String pathInfo = request.getPathInfo();
            MemberDAO memberDao = new MemberDAO();
            Gson gson = new Gson();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // Get all members
                List<Member> members = memberDao.getAllMembers();
                JsonArray jsonArray = new JsonArray();
                for (Member member : members) {
                    JsonObject obj = gson.toJsonTree(member).getAsJsonObject();
                    jsonArray.add(obj);
                }
                response.getWriter().write(jsonArray.toString());
            } else {
                // Get specific member
                int memberId = Integer.parseInt(pathInfo.substring(1));
                Member member = memberDao.getMemberById(memberId);
                if (member != null) {
                    response.getWriter().write(gson.toJson(member));
                } else {
                    response.setStatus(404);
                    JsonObject jsonResponse = new JsonObject();
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Member not found");
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
            Member member = gson.fromJson(reader, Member.class);
            
            MemberDAO memberDao = new MemberDAO();
            if (memberDao.addMember(member)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Member added successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to add member");
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
            Member member = gson.fromJson(reader, Member.class);
            
            MemberDAO memberDao = new MemberDAO();
            if (memberDao.updateMember(member)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Member updated successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to update member");
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
            int memberId = Integer.parseInt(pathInfo.substring(1));
            
            MemberDAO memberDao = new MemberDAO();
            if (memberDao.deleteMember(memberId)) {
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Member deleted successfully");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.setStatus(500);
                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("success", false);
                jsonResponse.addProperty("message", "Failed to delete member");
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
