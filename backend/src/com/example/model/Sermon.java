package com.example.model;

import java.time.LocalDateTime;

public class Sermon {
    private int id;
    private String title;
    private String description;
    private String filePath;
    private String fileType;
    private int uploadedBy;
    private LocalDateTime uploadedDate;
    private LocalDateTime updatedAt;

    public Sermon() {}

    public Sermon(String title, String description, String filePath, String fileType, int uploadedBy) {
        this.title = title;
        this.description = description;
        this.filePath = filePath;
        this.fileType = fileType;
        this.uploadedBy = uploadedBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public int getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(int uploadedBy) { this.uploadedBy = uploadedBy; }

    public LocalDateTime getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(LocalDateTime uploadedDate) { this.uploadedDate = uploadedDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
