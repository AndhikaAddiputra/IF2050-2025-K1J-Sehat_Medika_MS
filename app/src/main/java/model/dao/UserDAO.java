package model.dao;

import model.entity.User; 
import model.entity.UserRole; 
import model.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getString("userId"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password")); 
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phoneNumber")); 
        user.setRole(UserRole.valueOf(rs.getString("role"))); 
        user.setFullname(rs.getString("fullName"));
        Timestamp lastLoginTimestamp = rs.getTimestamp("lastLogin");
        user.setLastlogin(lastLoginTimestamp != null ? lastLoginTimestamp.toLocalDateTime() : null);
        return user;
    }

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO User (userId, username, password, email, phoneNumber, fullName, role, lastLogin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUserId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getPhoneNumber());
            pstmt.setString(6, user.getFullname()); 
            pstmt.setString(7, user.getRole().name());
            pstmt.setTimestamp(8, user.getLastlogin() != null ? Timestamp.valueOf(user.getLastlogin()) : null);
            pstmt.executeUpdate();
        }
    }

    public User getUserById(String userId) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM User WHERE userId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    public User getUserByUsername(String username) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM User WHERE username = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    public User getUserByFullname(String fullname) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM User WHERE fullName = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fullname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
        }
        return user;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    public List<User> getUsersByRole(UserRole role) throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE role = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE User SET username = ?, password = ?, email = ?, phoneNumber = ?, fullName = ?, role = ?, lastLogin = ? WHERE userId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhoneNumber());
            pstmt.setString(5, user.getFullname()); 
            pstmt.setString(6, user.getRole().name());
            pstmt.setTimestamp(7, user.getLastlogin() != null ? Timestamp.valueOf(user.getLastlogin()) : null);
            pstmt.setString(8, user.getUserId());
            pstmt.executeUpdate();
        }
    }

    public void deleteUser(String userId) throws SQLException {
        String sql = "DELETE FROM User WHERE userId = ?";
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT COUNT(*) FROM User WHERE username = ? AND password = ?"; 
        try (Connection conn = new DatabaseConnection().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}