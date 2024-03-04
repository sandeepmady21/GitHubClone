package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final String serverName = "localhost";
    private static final int port = 8000;

    public static void main(String[] args) {
        try (Socket client = new Socket(serverName, port);
             Scanner scanner = new Scanner(System.in)) {

            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);

            sendLoginCommand(scanner, out);
            printAvailableCommands();

            // Create a thread to read server's responses
            new Thread(() -> {
                String serverResponse;
                try {
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("Server: " + serverResponse);
                    }
                } catch (IOException e) {
                    System.out.println("Server connection closed");
                }
            }).start();

            // Main client loop, send user input to server
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                handleUserInput(input, out);
            }
        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }

    private static void sendLoginCommand(Scanner scanner, PrintWriter out) {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        out.println("LOGIN " + username + " " + password);  // send login command
    }

    private static void printAvailableCommands() {
        System.out.println("Logged in successfully. Available commands: \n");
        System.out.println("- LOGIN {username} {password} : Log in to the server");
        System.out.println("- CREATE {projectname} : Creates a new project on the server");
        System.out.println("- CLONE {projectname} : Clones a project from the server");
        System.out.println("- COMMIT {message} : Commits to your currently cloned project. The commit message will be added to the list of commits in your project");
        System.out.println("- PUSH {projectname} : Pushes your local commit history of the cloned project back to the server");
        System.out.println("- LOG : Shows the commit history of the cloned project");
        System.out.println("- COMMITLOG : Show all the commits of the cloned project");
        System.out.println("- LIST : Lists all available projects");
        System.out.println("- SHUTDOWN {secret_key} : Shuts down the server if the correct key is provided");
        System.out.println("- DISCONNECT : Disconnects you");
        System.out.println("- HELP : Lists all the commands and what they do");
    }

    private static void handleUserInput(String input, PrintWriter out) {
        // If DISCONNECT command is sent, break from the loop
        if (input.equalsIgnoreCase("DISCONNECT")) {
            out.println(input);
            System.exit(0);
        }
        out.println(input);
    }
}