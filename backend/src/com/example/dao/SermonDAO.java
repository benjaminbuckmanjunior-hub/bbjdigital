package com.example.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.example.model.Sermon;
import com.example.db.DBConnection;

public class SermonDAO {

    public boolean addSermon(Sermon sermon) {
        String query = "INSERT INTO sermons (title, description, file_path, file_type, uploaded_by) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sermon.getTitle());
            stmt.setString(2, sermon.getDescription());
            stmt.setString(3, sermon.getFilePath());
            stmt.setString(4, sermon.getFileType());
            stmt.setInt(5, sermon.getUploadedBy());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Sermon getSermonById(int id) {
        String query = "SELECT * FROM sermons WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSermon(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Sermon> getAllSermons() {
        List<Sermon> sermons = new ArrayList<>();
        String query = "SELECT * FROM sermons ORDER BY uploaded_date DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                sermons.add(mapResultSetToSermon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sermons;
    }

    public List<Sermon> getSermonsByAdmin(int adminId) {
        List<Sermon> sermons = new ArrayList<>();
        String query = "SELECT * FROM sermons WHERE uploaded_by = ? ORDER BY uploaded_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, adminId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                sermons.add(mapResultSetToSermon(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sermons;
    }

    public boolean updateSermon(Sermon sermon) {
        String query = "UPDATE sermons SET title = ?, description = ?, file_path = ?, file_type = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, sermon.getTitle());
            stmt.setString(2, sermon.getDescription());
            stmt.setString(3, sermon.getFilePath());
            stmt.setString(4, sermon.getFileType());
            stmt.setInt(5, sermon.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteSermon(int id) {
        String query = "DELETE FROM sermons WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Sermon mapResultSetToSermon(ResultSet rs) throws SQLException {
        Sermon sermon = new Sermon();
        sermon.setId(rs.getInt("id"));
        sermon.setTitle(rs.getString("title"));
        sermon.setDescription(rs.getString("description"));
        sermon.setFilePath(rs.getString("file_path"));
        sermon.setFileType(rs.getString("file_type"));
        sermon.setUploadedBy(rs.getInt("uploaded_by"));
        return sermon;
    }
}
