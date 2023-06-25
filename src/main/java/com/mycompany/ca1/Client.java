package com.mycompany.ca1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String SERVER_IP = "127.0.0.1"; // localhost
    private static final int SERVER_PORT = 8080; // port number

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

            Thread listenerThread = new Thread(new Listener(serverInput));
            listenerThread.start();

            String clientInput;
            while ((clientInput = consoleInput.readLine()) != null) {
                output.println(clientInput);
                if (clientInput.equalsIgnoreCase("END")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
}

class Listener implements Runnable {
    private final BufferedReader serverInput;

    public Listener(BufferedReader serverInput) {
        this.serverInput = serverInput;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = serverInput.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Error listening to server: " + e.getMessage());
        }
    }
}
