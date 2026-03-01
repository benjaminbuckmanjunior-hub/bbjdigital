package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.model.Announcement;
import com.example.db.DBConnection;

public class AnnouncementDAO {

    public boolean addAnnouncement(Announcement announcement) {
        String query = "INSERT INTO announcements (title, message, created_by) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getMessage());
            stmt.setInt(3, announcement.getCreatedBy());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Announcement getAnnouncementById(int id) {
        String query = "SELECT * FROM announcements WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAnnouncement(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Announcement> getAllAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        String query = "SELECT * FROM announcements ORDER BY created_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                announcements.add(mapResultSetToAnnouncement(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return announcements;
    }

    public boolean updateAnnouncement(Announcement announcement) {
        String query = "UPDATE announcements SET title = ?, message = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, announcement.getTitle());
            stmt.setString(2, announcement.getMessage());
            stmt.setInt(3, announcement.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAnnouncement(int id) {
        String query = "DELETE FROM announcements WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Announcement mapResultSetToAnnouncement(ResultSet rs) throws SQLException {
        Announcement announcement = new Announcement();
        announcement.setId(rs.getInt("id"));
        announcement.setTitle(rs.getString("title"));
        announcement.setMessage(rs.getString("message"));
        announcement.setCreatedBy(rs.getInt("created_by"));
        return announcement;
    }
}
