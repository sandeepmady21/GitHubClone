package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import common.GlobalContext;
import common.Project;

public class Server {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        int port = 8000;
        
        // Load projects from file
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("projects.ser"))) {
            GlobalContext.projects = (ConcurrentHashMap<String, Project>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Server] Failed to load projects from file, continuing with empty project list.");
            GlobalContext.projects = new ConcurrentHashMap<>();
        }

        // Create an executor service with a fixed thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Started");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch(IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutdown hook triggered, saving data");
                saveProjects();
            }
        });
    }
    public static void shutdownServer() {
        saveProjects();
        System.out.println("Shutdown command issued, server exiting.");
        System.exit(0);
    }
    
    public static void clearServer() {
        GlobalContext.projects.clear();
        System.out.println("All projects cleared.");
        // Delete the serialized data file
        File file = new File("projects.ser");
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Serialized data file deleted.");
            } else {
                System.out.println("Failed to delete serialized data file.");
            }
        }
    }


    public static void saveProjects() {
        // Save projects to file
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("projects.ser"))) {
            oos.writeObject(GlobalContext.projects);
            System.out.println("Data save successful");
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }
}