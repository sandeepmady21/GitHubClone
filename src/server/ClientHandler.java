package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.GlobalContext;
import common.LocalContext;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private LocalContext localContext;
    private CommandHandler commandHandler;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.localContext = new LocalContext();
        this.commandHandler = new CommandHandler();
    }

    @Override
    public void run() {
        try {
            BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter serverOutput = new PrintWriter(clientSocket.getOutputStream(), true);

            String inputLine;
            while((inputLine = clientInput.readLine()) != null) {
                String[] parts = inputLine.split(" ", 3);
                String rpc = parts[0].toUpperCase();

                switch (rpc) {
                    case "LOGIN":
                        commandHandler.handleLogin(parts, serverOutput, localContext);
                        break;

                    case "DISCONNECT":
                        commandHandler.handleDisconnect(serverOutput, localContext);
                        clientSocket.close();
                        return; 

                    case "CREATE":
                        commandHandler.handleCreate(parts, serverOutput, localContext);
                        break;

                    case "CLONE":
                        commandHandler.handleClone(parts, serverOutput, localContext);
                        break;
                    
                    case "COMMIT":
                        commandHandler.handleCommit(parts, serverOutput, localContext);
                        break;

                    case "LOG":
                        commandHandler.handleLog(parts, serverOutput, localContext);
                        break;
                    
                    case "COMMITLOG":
                    	commandHandler.handleCommitLog(parts, serverOutput, localContext);
                    	break;

                    case "PUSH":
                        commandHandler.handlePush(parts, serverOutput, localContext);
                        break;

                    case "HELP":
                        commandHandler.handleHelp(serverOutput, localContext);
                        break;
                    default:
                        serverOutput.println("Invalid command. Please use HELP to list all available commands.");
                        break;
                }
            }
        } catch(IOException e) {
            GlobalContext.connectedUsers.decrementAndGet();
            System.out.println("[Server] There was a problem: " + e);
        }
    }
}