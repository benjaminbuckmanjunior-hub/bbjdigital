package com.example.controller;

import org.springframework.web.bind.annotation.*;
import com.example.dao.MemberDAO;
import com.example.model.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RegisterController {

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String name = request.get("name");
            
            if (name == null || name.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Name is required");
                return response;
            }

            // Generate auto email
            String email = generateAutoEmail(name);
            String password = generatePassword();

            Member member = new Member(name, email, password);
            MemberDAO memberDao = new MemberDAO();
            
            if (memberDao.addMember(member)) {
                // fetch the inserted member to obtain generated ID
                Member saved = memberDao.getMemberByEmail(email);
                response.put("success", true);
                response.put("message", "Registration successful");
                response.put("email", email);
                response.put("password", password);
                if (saved != null) {
                    response.put("userId", saved.getId());
                }
            } else {
                response.put("success", false);
                response.put("message", "Registration failed");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Server error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return response;
    }

    private String generateAutoEmail(String name) {
        String sanitized = name.toLowerCase().replaceAll("\\s+", ".");
        // use corporate domain for church members
        return sanitized + "@bbj.com";
    }

    private String generatePassword() {
        Random rand = new Random();
        return "pass" + rand.nextInt(10000);
    }
}
