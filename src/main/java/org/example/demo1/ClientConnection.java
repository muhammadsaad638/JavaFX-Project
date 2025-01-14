package org.example.demo1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {
    // Static connection details that will be used across the application
    private static PrintWriter out;
    private static BufferedReader in;
    private static String currentUser = User.userName;// Hardcoded username\
    public static String time;


    public void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send username to server
            out.println(currentUser);

            // Start listening for messages
            new Thread(() -> listenForMessages()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                // Process incoming message

                MessageManager.handleIncomingMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PrintWriter getWriter() {
        return out;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

}
