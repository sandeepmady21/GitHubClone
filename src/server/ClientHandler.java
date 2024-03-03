package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import common.GlobalContext;
import common.LocalContext;
import common.Project;

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private LocalContext localContext;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.localContext = new LocalContext();
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
                        if (parts.length < 3) {
                            serverOutput.println("Invalid LOGIN command. Usage: LOGIN {username} {password}");
                        } else {
                            String username = parts[1];
                            String password = parts[2];
                            if(AuthenticationService.authenticate(username, password)) {
                                localContext.username = username;
                                GlobalContext.connectedUsers.incrementAndGet();
                                serverOutput.println("Login successful!");
                                System.out.println("Client \'" + localContext.username + 
                                		"\' logged in. Total active connections: " + GlobalContext.connectedUsers.get() + ".");
                            } else {
                                serverOutput.println("Invalid username or password!");
                            }
                        }
                        break;

                    case "DISCONNECT":
                        if (localContext.username != null) {
                            serverOutput.println("Disconnecting " + localContext.username + "...");
                            GlobalContext.connectedUsers.decrementAndGet();
                            System.out.println("Client \'" + localContext.username + 
                            		"\' disconnected. Total active connections: " + GlobalContext.connectedUsers.get() + ".");
                            clientSocket.close();
                            return;
                        } else {
                            serverOutput.println("You're not logged in!");
                        }
                        break; 
                        
                    case "CREATE":
                        if (parts.length < 2) {
                            serverOutput.println("Invalid CREATE command. Usage: CREATE {name}");
                        } else {
                            String projectName = parts[1];
                            if (GlobalContext.projects.containsKey(projectName)) {
                                serverOutput.println("Project already exists!");
                                break;
                            }

                            GlobalContext.projects.put(projectName, new Project(projectName));
                            serverOutput.println("Project created successfully!");
                        }
                        break;

                    case "CLONE":
                        if (parts.length < 2) {
                            serverOutput.println("Invalid CLONE command. Usage: CLONE {name}");
                        } else {
                            String projectName = parts[1];
                            if (GlobalContext.projects.containsKey(projectName)) {
                                localContext.clonedProject = GlobalContext.projects.get(projectName);
                                serverOutput.println("Project cloned successfully!");
                            } else {
                                serverOutput.println("Project does not exist!");
                            }
                        }
                        break;
                  
                    case "COMMIT":
                        if (parts.length < 2) {
                            serverOutput.println("Invalid COMMIT command. Usage: COMMIT {message}");
                        } else if (localContext.clonedProject == null) {
                            serverOutput.println("No project cloned!");
                        } else {
                            String commitMessage = parts[1];
                            localContext.clonedProject.commits.add(commitMessage);
                            serverOutput.println("Commit successful!");
                        }
                        break;

                    case "PUSH":
                        if (parts.length < 2) {
                            serverOutput.println("Invalid PUSH command. Usage: PUSH {name}");
                        } else if (localContext.clonedProject == null) {
                            serverOutput.println("No project cloned!");
                        } else {
                            String projectName = parts[1];
                            if (GlobalContext.projects.containsKey(projectName)) {
                                GlobalContext.projects.get(projectName).commits = localContext.clonedProject.commits;
                                serverOutput.println("Push successful!");
                            } else {
                                serverOutput.println("Project does not exist on server!");
                            }
                        }
                        break;
               
                    default:
                        if (localContext.username != null) {
                            serverOutput.println("Invalid command. Available commands: ");
                            serverOutput.println("- CREATE {projectname} : Creates a new project on the server");
                            serverOutput.println("- CLONE {projectname} : Clones a project from the server to your local context");
                            serverOutput.println("- COMMIT {message} : Commits to the project that is currently cloned in your local context. "
                            		+ "The commit message will be added to the list of commits in your local project");
                            serverOutput.println("- PUSH {projectname} : Pushes your local commit history of the cloned project back to the server");
                            serverOutput.println("- DISCONNECT : Disconnects you");
                        } else {
                            serverOutput.println("Please LOGIN {username} {password} before sending commands");
                        }
                }
            }
        } catch(IOException e) {
            GlobalContext.connectedUsers.decrementAndGet();
            System.out.println("[Server] There was a problem: " + e);
        }
    }
}