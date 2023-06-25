/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ca1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final Map<String, ClientHandler> clients;
    private final OrderBook orderBook; // store and manage orders from clients
    private String clientId;
    private final Map<String, String> userCredentials; // authenticate clients

    public ClientHandler(Socket clientSocket, Map<String, ClientHandler> clients, Map<String, String> userCredentials) {
        this.clientSocket = clientSocket;
        this.clients = clients;
        this.orderBook = new OrderBook();
        this.userCredentials = userCredentials;
        // Sample users
        userCredentials.put("user1", "password1");
        userCredentials.put("user2", "password2");
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public void run() {
        // simple protocol for a client-server system
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Add authentication here
            /**
             * With these changes, the code will now prompt users to enter their
             * username and password upon connecting.
             */
            boolean authenticated = false;
            while (!authenticated) {
                output.println("Please enter your username and password separated by a colon:");
                String[] credentials = input.readLine().split(":", 2);
                String username = credentials[0].trim();
                String password = credentials[1].trim();

                if (userCredentials.containsKey(username) && userCredentials.get(username).equals(password)) {
                    authenticated = true;
                    clientId = username;
                    output.println("CONNECTED");
                } else {
                    output.println("Invalid username or password. Please try again.");
                }
            }

            String inputLine;
            while ((inputLine = input.readLine()) != null) { // processes each line as a message from a client
                String[] messageParts = inputLine.split(":", 2);
                String messageType = messageParts[0].toUpperCase();
                String messageContent = messageParts.length > 1 ? messageParts[1] : "";

                switch (messageType) { // select the appropriate action based on the message type.
                    case "USER" -> {
                        clientId = messageContent.trim();
                        output.println("CONNECTED");
                    }
                    case "ORDER" ->
                        handleOrder(output, messageContent);
                    case "CANCEL" ->
                        handleCancel(output, messageContent);
                    case "VIEW" ->
                        output.println(orderBook.toString());
                    case "END" -> {
                        clients.remove(clientId);
                        clientSocket.close();
                    }
                    default ->
                        output.println("UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            clients.remove(clientId);
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void handleOrder(PrintWriter output, String messageContent) {
        /***
         * The handleOrder method processes buy/sell orders received
         * from clients, matches them with existing orders if possible,
         * and adds new orders to the orderBook if no match is found.
         */
        String[] orderParts = messageContent.split(",", 3);
        if (orderParts.length < 3) {
            output.println("INVALID_ORDER");
            return;
        }

        boolean isBuy = "B".equalsIgnoreCase(orderParts[0].trim());
        String title = orderParts[1].trim();
        double price;
        try {
            price = Double.parseDouble(orderParts[2].trim());
        } catch (NumberFormatException e) {
            output.println("INVALID_PRICE");
            return;
        }

        Order order = new Order(clientId, isBuy, title, price);
        Order matchedOrder = orderBook.findMatchingOrder(order);

        if (matchedOrder != null) {
            output.println(
                    "MATCH:" + (isBuy ? "B" : "S") + "," + title + "," + price + "," + matchedOrder.getUsername());
            clients.get(matchedOrder.getUsername())
                    .sendMatchMessage(isBuy ? "S" : "B", title, price, clientId);
        } else {
            orderBook.addOrder(order);
            output.println(orderBook.toString());
        }
    }

    private void handleCancel(PrintWriter output, String messageContent) {
        /***
         * The handleCancel method processes cancel requests
         * received from clients, cancels the corresponding
         * order in the orderBook object, and sends a response
         * back to the client indicating whether the
         * cancellation was successful or not.
         */
        String[] orderParts = messageContent.split(",", 3);
        if (orderParts.length < 3) {
            output.println("INVALID_CANCEL");
            return;
        }

        boolean isBuy = "B".equalsIgnoreCase(orderParts[0].trim());
        String title = orderParts[1].trim();
        double price;
        try {
            price = Double.parseDouble(orderParts[2].trim());
        } catch (NumberFormatException e) {
            output.println("INVALID_PRICE");
            return;
        }

        Order order = new Order(clientId, isBuy, title, price);
        boolean success = orderBook.cancelOrder(order);

        if (success) {
            output.println("CANCEL_SUCCESS");
        } else {
            output.println("CANCEL_FAILED");
        }
    }

    /*
     * The sendMatchMessage method is likely called by
     * the handleOrder method to notify a client when
     * their order is matched with another client's
     * order.
     * The method sends a "MATCH" message
     * to the appropriate client over the network
     * using the clientSocket object associated
     * with the client's identifier.
     */
    private void sendMatchMessage(String orderType, String title, double price, String clientId) {
        try (PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {
            output.println("MATCH:" + orderType + "," + title + "," + price + "," + clientId);
        } catch (IOException e) {
            System.err.println("Error sending match message: " + e.getMessage());
        }
    }
}
