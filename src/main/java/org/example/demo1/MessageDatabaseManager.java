package org.example.demo1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalTime;

public class MessageDatabaseManager {
    public static void storeMessage(String sender, String receiver, String message) {
        String sql = "INSERT INTO chat_messages (sender, receiver, message, time, date) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            // Get current time and date
            LocalTime currentTime = LocalTime.now();
            LocalDate currentDate = LocalDate.now();

            // Set values in the prepared statement
            preparedStatement.setString(1, sender);
            preparedStatement.setString(2, receiver);
            preparedStatement.setString(3, message);
            preparedStatement.setTime(4, java.sql.Time.valueOf(currentTime));
            preparedStatement.setDate(5, java.sql.Date.valueOf(currentDate));

            // Execute the insert query
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Message stored successfully!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to store the message.");
        }
    }
}
