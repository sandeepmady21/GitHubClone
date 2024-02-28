package clientPackage;

import java.net.*;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

public class Client implements Runnable {
    private Socket clientSocket;
    private BufferedReader serverInput;
    private PrintWriter clientOutput;
    private String serverMessage;
    private String username;
    private String password;

    public Client(Socket socket, String username, String password) {
        this.clientSocket = socket;
        this.username = username;
        this.password = password;
    }

    public boolean loginRPC(String username, String password) throws IOException{
        clientOutput.println("Connect " + username + " " + password);
        serverMessage = serverInput.readLine();
        System.out.println("[Client] Server message: " + serverMessage);
        return serverMessage.equalsIgnoreCase("User Authentication successful");
    }

    public boolean disconnectRPC() throws IOException{
        clientOutput.println("Disconnect");
        serverMessage = serverInput.readLine();
        System.out.println("[Client] Server message: " + serverMessage);
        return serverMessage.equalsIgnoreCase("Disconnected");
    }

    public void run() {
        try {
            serverInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            clientOutput = new PrintWriter(clientSocket.getOutputStream(), true); 

            boolean isValidUser = loginRPC(this.username, this.password);
            
            if (!isValidUser) {
            	System.out.println("[Client] Authentication failed. Disconnecting...");
            	return;
            }

            int randNum = new Random().nextInt(10) + 1;
            System.out.println("[Client] Going to sleep now for " + randNum + "sec");
            
            for (int i = randNum; i > 0; i--) {
                System.out.println("[Client] Disconnecting in " + i + " sec");
                Thread.sleep(1000); // Sleep for 1 sec
            }
            
            disconnectRPC();
            System.out.println("[Client] Server disconnected successfully");           

            clientSocket.close();
        } catch(IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String serverName = "localhost"; // add server IP or hostname
        int port = 6000;
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("[Client] Enter username (guest): ");
        String username = scanner.nextLine();
        System.out.print("[Client] Enter password (123): ");
        String password = scanner.nextLine();
        // Here, we create and start four clients
        for (int i = 0; i < 4; i++) {
            Socket clientSocket = new Socket(serverName, port);
            Client client = new Client(clientSocket, username, password);
            new Thread(client).start();
        }
    }
}