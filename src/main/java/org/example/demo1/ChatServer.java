package org.example.demo1;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Chat server started...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First message is the username
                username = in.readLine();
                System.out.println("Sender"+username);
                clients.put(username, out);

                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split(":", 2);
                    if (parts.length == 2) {
                        String recipient = parts[0];
                        String msg = parts[1];
                        System.out.println("Receiver "+recipient);
                        System.out.println("Message "+msg);
                        sendMessage(recipient, username + ": " + msg);
                        //clients.put(recipient,out);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (username != null) {
                    clients.remove(username);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void sendMessage(String recipient, String message) {
            PrintWriter recipientOut = clients.get(recipient);
//            System.out.println(recipientOut.toString());
            if (recipientOut != null) {
                System.out.println(recipient);
                recipientOut.println(message);
            }
        }
    }

    public static Map<String, PrintWriter> getClients(){
        return clients;
    }
}

