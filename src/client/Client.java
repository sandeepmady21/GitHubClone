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
            // send help command
            out.println("help");

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

    private static void handleUserInput(String input, PrintWriter out) {
        // If DISCONNECT command is sent, break from the loop
        if (input.equalsIgnoreCase("DISCONNECT")) {
            out.println(input);
            System.exit(0);
        }
        out.println(input);
    }
}