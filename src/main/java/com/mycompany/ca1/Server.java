/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ca1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 8080;
    private static final int MAX_CONNECTIONS = 10;

    public static void main(String[] args) {
    /**
     * The main method sets up the server and listens for 
     * incoming client connections, which are handled by 
     * ClientHandler objects in separate threads. 
     * 
     * The userCredentials map is used to authenticate clients, 
     * and the clients map is used to keep track of connected clients.
     */
        ExecutorService executor = Executors.newFixedThreadPool(MAX_CONNECTIONS);
        Map<String, ClientHandler> clients = new HashMap<>();
        Map<String, String> userCredentials = new HashMap<>(); // Added this line to store user credentials

        // Sample users
        userCredentials.put("user1", "password1");
        userCredentials.put("user2", "password2");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients, userCredentials); // Pass userCredentials to the constructor
                executor.submit(() -> {
                    clientHandler.run();
                    clients.put(clientHandler.getClientId(), clientHandler); // Moved this line from before to after authentication
                });
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }
}
