package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        String serverName = "localhost";
        int port = 8000;
        try(Socket client = new Socket(serverName, port);
				Scanner scanner = new Scanner(System.in)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            out.println("LOGIN " + username + " " + password);  // send login command

            // after login attempt
            System.out.println("Logged in successfully. Available commands: ");
            System.out.println("- CREATE {projectname} : Creates a new project on the server");
            System.out.println("- CLONE {projectname} : Clones a project from the server to your local context");
            System.out.println("- COMMIT {message} : Commits to the project that is currently cloned in your local context. The commit message will be added to the list of commits in your local project");
            System.out.println("- PUSH {projectname} : Pushes your local commit history of the cloned project back to the server");
            System.out.println("- DISCONNECT : Disconnect current user");
            // Create a thread to read server's responses
            new Thread(() -> {
                try {
                    String serverResponse;
                    while((serverResponse = in.readLine()) != null) {
                        System.out.println("Server: " + serverResponse);
                    }
                } catch (IOException e) {
                    System.out.println("Server connection closed");
                }
            }).start();
    
            // Main client loop, send user input to server
            while(scanner.hasNextLine()) {
            	String input = scanner.nextLine();

                // If DISCONNECT command is sent, break from the loop
                if(input.equalsIgnoreCase("DISCONNECT")) {
                    out.println(input);
                    break;
                }
                out.println(input);
            }
        } catch(IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}