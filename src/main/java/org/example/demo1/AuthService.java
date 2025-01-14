package org.example.demo1;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {

    // Method to register a new user
    public boolean registerUser(String username, String email, String password) {
        String insertQuery = "INSERT INTO users (username, email, password,profileCompleted) VALUES (?, ?, ?,?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            // Setting parameters for the SQL query
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashPassword(password));  // Hashing the password
            stmt.setBoolean(4,false);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;  // Return true if registration is successful

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to authenticate a user with either email or username and password
    public boolean authenticateUser(String identifier, String password) {
        String selectQuery = "SELECT id,password FROM users WHERE (username = ? OR email = ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            // Setting parameters for SQL query
            stmt.setString(1, identifier);
            stmt.setString(2, identifier);

            ResultSet rs = stmt.executeQuery();

            // If user found, validate the password
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                User.id=rs.getInt("id");
                return checkPassword(password, storedPassword);  // Password verification
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;  // Return false if authentication fails
    }

    // Helper method to hash the password
    private String hashPassword(String password) {
        // Generate a salt and hash the password with it
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Helper method to check if the entered password matches the stored hashed password
    private boolean checkPassword(String enteredPassword, String storedPasswordHash) {
        // BCrypt will automatically compare the password with the stored hash
        return BCrypt.checkpw(enteredPassword, storedPasswordHash);
    }


}
