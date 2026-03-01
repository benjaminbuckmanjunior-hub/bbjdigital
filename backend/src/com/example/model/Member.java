package com.example.model;

import java.time.LocalDateTime;

public class Member {
    private int id;
    private String name;
    private String email;
    private String password;
    private String status;
    private LocalDateTime joinedDate;
    private LocalDateTime updatedAt;

    public Member() {}

    public Member(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = "active";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDateTime joinedDate) { this.joinedDate = joinedDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
