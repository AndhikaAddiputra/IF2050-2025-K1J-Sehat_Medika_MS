package model.entity;

import java.time.LocalDateTime;


public class User {
    private String userId;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime lastlogin;


    public User() {}

    public User(String userId, String username, String password, String email, String phoneNumber, UserRole role, LocalDateTime lastlogin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.lastlogin = lastlogin;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }
    public LocalDateTime getLastlogin() {
        return lastlogin;
    }
    public void setLastlogin(LocalDateTime lastlogin) {
        this.lastlogin = lastlogin;
    }
}
