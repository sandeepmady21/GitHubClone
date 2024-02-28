package serverPackage;

import java.net.*;
import java.io.*;

public class Server {
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;

        ClientHandler(Socket socket) throws IOException {
            this.clientSocket = socket;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        @Override
        public void run() {
            try {
                out.println("Hello! You connected to the Server. Please login using guest and 123");

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] inputSplit = inputLine.split(" ");
                    String command = inputSplit[0];

                    if (command.equalsIgnoreCase("Connect")) {
                        String username = inputSplit[1];
                        String password = inputSplit[2];
                        System.out.println("[Server] Authentication attempt by " + username);
                        if (authenticate(username, password)) {
                            out.println("User Authentication successful");
                            System.out.println(username + " logged in");
                        } else {
                            System.out.println("[Server] User Authentication failed");
                            out.println("Invalid username or password. Please try again!");
                        }

                    } else if (command.equalsIgnoreCase("Disconnect")) {
                        System.out.println("[Server] Client requested to disconnect");
                        out.println("Disconnecting");
                        clientSocket.close();
                        System.out.println("[Server] Client disconnected successfully");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private boolean authenticate(String username, String password) {
            String validUsername = "user123";
            String validPassword = "pass456";
            String guestUser = "guest";
            String guestPass = "123";
            boolean validUser = username.equals(validUsername) && password.equals(validPassword);
            boolean validGuest = username.equals(guestUser) && password.equals(guestPass);

            return validUser || validGuest;
        }
    }

    public static void main(String[] args) {
        int port = 6000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Server] Server started successfully!");
            int connectionCount = 0;
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] New client connected at port: " + port);
                connectionCount++;
                System.out.println("[Server] Total number of connections/disconnections made: " + connectionCount);
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.out.println("[Server] There was a problem: " + e);
        }
    }
}