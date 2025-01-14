package org.example.demo1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    public static int id;
    public static String userName;
    private String email;
    private String password;
    private static boolean profileCompleted;

    public User( String userName, String email,String password) {
//        User.userName = userName;
        profileCompleted=false;
        this.email = email;
        this.password=password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static int getId() {
        return id;
    }

    public static void setId(int id) {
        User.id = id;
    }

    public static boolean isProfileCompleted() {
        return profileCompleted;
    }

    public static void setProfileCompleted(boolean profileCompleted) {
        User.profileCompleted = profileCompleted;
    }

    public static boolean checkProfileCompletionStatus(String username) {
        String query = "SELECT profileCompleted FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("profileCompleted");
                }
                return false; // User not found
            }
        } catch (SQLException e) {
            // Log the exception
            System.out.println(e.getMessage());
            return false; // Return false in case of database errors
        }
    }
}




