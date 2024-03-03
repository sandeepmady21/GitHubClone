package server;

import common.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        int port = 8000;

        // Create an executor service with a fixed thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started successfully!");
            // Main server loop
            while (true) {
                // Accept incoming connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client attempting to connect...");
             
                // Create a worker thread to handle the client
                executorService.submit(new ClientHandler(clientSocket));
                          
            }
        } catch(IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            executorService.shutdown();
        }
    }
}