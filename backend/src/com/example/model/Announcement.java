package com.example.model;

import java.time.LocalDateTime;

public class Announcement {
    private int id;
    private String title;
    private String message;
    private int createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime updatedAt;

    public Announcement() {}

    public Announcement(String title, String message, int createdBy) {
        this.title = title;
        this.message = message;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
