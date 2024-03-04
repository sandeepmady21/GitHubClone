package server;

import java.io.PrintWriter;
import java.util.Map;

import common.Commit;
import common.File;
import common.GlobalContext;
import common.LocalContext;
import common.Project;

public class CommandHandler {
	
	private static final String SHUTDOWN_KEY = "prettyPlease";

    public void handleLogin(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
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
    }

    public void handleDisconnect(PrintWriter serverOutput, LocalContext localContext) {
        if (localContext.username != null) {
            serverOutput.println("Disconnecting " + localContext.username + "...");
            GlobalContext.connectedUsers.decrementAndGet();
            System.out.println("Client \'" + localContext.username + 
                    "\' disconnected. Total active connections: " + GlobalContext.connectedUsers.get() + ".");
        } else {
            serverOutput.println("You're not logged in!");
        }
    }

    public void handleCreate(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid CREATE command. Usage: CREATE {name}");
        } else {
            String projectName = parts[1];
            if (GlobalContext.projects.containsKey(projectName)) {
                serverOutput.println("Project already exists!");
            } else {
                GlobalContext.projects.put(projectName, new Project(projectName));
                serverOutput.println("Project created successfully!");
                // after a project is successfully created, log it
                GlobalContext.projects.get(projectName).activityLog.add(localContext.username + " created project");
            }
        }
    }
    
    public void handleList(PrintWriter serverOutput) {
        if(GlobalContext.projects.isEmpty()){
                serverOutput.println("No projects have been created yet!");
        } else {
            serverOutput.println("Available projects: ");
            for (String projectName : GlobalContext.projects.keySet()) {
                serverOutput.println(projectName);
            }
        }
    }

    public void handleClone(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
    	System.out.println("[Debug] Entered handleClone method. Processing...");
        if (parts.length < 2) {
            serverOutput.println("Invalid CLONE command. Usage: CLONE {name}");
        } else {
            String projectName = parts[1];
            System.out.println("[Debug] Requested to clone project: " + projectName);
            if (GlobalContext.projects.containsKey(projectName)) {
                System.out.println("[Debug] Project exists. Cloning...");
                Project projectToClone = GlobalContext.projects.get(projectName);
                try {
                    localContext.clonedProject = new Project(projectToClone);
                    serverOutput.println("Project cloned successfully!");
                    localContext.clonedProject.activityLog.add(localContext.username + " cloned project");
                    System.out.println("[Debug] Cloning successful"); // Add a debug log after cloning
                } catch (Exception e) {
                    System.out.println("[Debug] Error during cloning: " + e.getMessage()); // Catch any exception during cloning
                }
            } else {
                serverOutput.println("Project does not exist!");
            }
        }
    }

    public void handleCommit(PrintWriter serverOutput, LocalContext localContext, String inputLine) {
        int firstSpace = inputLine.indexOf(' ');
        if (firstSpace == -1 || firstSpace == inputLine.length() - 1) {
            serverOutput.println("Invalid COMMIT command. Usage: COMMIT {message}");
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
        } else {
            String commitMessage = inputLine.substring(firstSpace + 1);
            localContext.clonedProject.commits.add(new Commit(localContext.username, commitMessage));
            serverOutput.println("Commit successful!");
            // after a commit is successfully made, log it
            localContext.clonedProject.activityLog.add(localContext.username + " made a commit: " + commitMessage);
        }
    }

    public void handleLog(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (localContext.clonedProject == null) {
  	  serverOutput.println("No project cloned!");
        } else {
  	  for(String log : localContext.clonedProject.activityLog) {
  	      serverOutput.println(log);
  	  }
        }
    }
    
    public void handleCommitLog(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
        } else {
            Project project = GlobalContext.projects.get(localContext.clonedProject.name);
            if (project != null) {
                for (Commit commit : project.commits) {
                    serverOutput.println(commit.toString());
                }
            }
        }
    }

    public void handlePush(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid PUSH command. Usage: PUSH {name}");
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
        } else {
            String projectName = parts[1];
            if (GlobalContext.projects.containsKey(projectName)) {
                GlobalContext.projects.get(projectName).commits = localContext.clonedProject.commits;
                GlobalContext.projects.get(projectName).files = localContext.clonedProject.files; // handle files during push
                serverOutput.println("Push successful!");
                // after a push is successfully made, log it
                GlobalContext.projects.get(projectName).activityLog.add(localContext.username + " pushed changes");
            } else {
                serverOutput.println("Project does not exist on server!");
            }
        }
    }
    
    public void handlePull(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid PULL command. Usage: PULL {name}");
        } else {
            String projectName = parts[1];
            if (GlobalContext.projects.containsKey(projectName)) {
                Project serverProject = GlobalContext.projects.get(projectName);
                Project localProject = localContext.clonedProject; 

                // Iterate over each file in the server project, copying it to the local project
                for (Map.Entry<String, File> entry : serverProject.files.entrySet()) {
                    localProject.files.put(entry.getKey(), new File(entry.getValue()));
                }
                
                serverOutput.println("Project pulled successfully!");
                localContext.clonedProject.activityLog.add(localContext.username + " pulled project");
            } else {
                serverOutput.println("Project does not exist!");
            }
        }
    }
    
    public void handleShutdown(String key, PrintWriter serverOutput) {
        if (SHUTDOWN_KEY.equals(key)) {
            System.out.println("Shutdown command received with valid key. Shutting down server...");
            Server.shutdownServer();
        } else {
            serverOutput.println("Invalid key provided for SHUTDOWN command");
        }
    }
    
    public void handleHelp(PrintWriter serverOutput, LocalContext localContext) {
        serverOutput.println("Available commands:");
        serverOutput.println("- LOGIN {username} {password} : Log in to the server");
        serverOutput.println("- CREATE {projectname} : Creates a new project on the server");
        serverOutput.println("- CLONE {projectname} : Clones a project from the server to your local context");
        serverOutput.println("- COMMIT {message} : Commits to your currently cloned project. The commit message will be added to the list of commits in your local project");
        serverOutput.println("- PUSH {projectname} : Pushes your local commit history of the cloned project back to the server");
        serverOutput.println("- LOG : Shows the commit history of the cloned project");
        serverOutput.println("- DISCONNECT : Disconnects you");
        serverOutput.println("- HELP : Lists all the commands and what they do");
    }
    
    public void handleCreateFile(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid CREATEFILE command. Usage: CREATEFILE {filename}");
        } else {
            File file = new File(parts[1]);
            localContext.clonedProject.files.put(file.getName(), file); // Adding the file to the project
            serverOutput.println("File created successfully!");
            localContext.clonedProject.activityLog.add(localContext.username + " created file: " + parts[1]);
        }
    }

    public void handleViewFile(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid VIEWFILE command. Usage: VIEWFILE {filename}");
        } else {
            File file = localContext.clonedProject.files.get(parts[1]);
            if (file != null) {
                serverOutput.println("File content: " + file.getContent());
            } else {
                serverOutput.println("File does not exist!");
            }
        }
    }

    public void handleWriteFile(String[] parts, String inputLine, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 3) {
            serverOutput.println("Invalid WRITEFILE command. Usage: WRITEFILE {filename} {content}");
        } else {
            File file = localContext.clonedProject.files.get(parts[1]);
            if (file != null) {
                String content = inputLine.substring(inputLine.indexOf(parts[1]) + parts[1].length() + 1);
                file.appendContent(content);
                serverOutput.println("Content written to file!");
            } else {
                serverOutput.println("File does not exist!");
            }
        }
    }

}