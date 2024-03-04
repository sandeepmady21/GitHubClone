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
                serverOutput.println("Try using help for listing commands");
                System.out.println("Client \'" + localContext.username + 
                        "\' logged in. Total active connections: " + GlobalContext.connectedUsers.get() + ".");
            } else {
                serverOutput.println("Invalid username or password! Please try again :)");
                serverOutput.println("Use the command - login {username} {password}");
                
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
        if (GlobalContext.projects.isEmpty()) {
            serverOutput.println("No projects have been created yet!");
        } else {
            serverOutput.println("┌───────────────────────────┐");
            serverOutput.println("│     Available Projects    │");
            serverOutput.println("├───────────────────────────┤");
            for (String projectName : GlobalContext.projects.keySet()) {
                serverOutput.println("│ " + padRight(projectName, 26) + "│"); // Adjust spacing for alignment
            }
            serverOutput.println("└───────────────────────────┘");
        }
    }

    public void handleClone(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid CLONE command. Usage: CLONE {name}");
        } else {
            String projectName = parts[1];
            if (GlobalContext.projects.containsKey(projectName)) {
                Project projectToClone = GlobalContext.projects.get(projectName);
                localContext.clonedProject = new Project(projectToClone); // Deep copy construction
                serverOutput.println("Project cloned successfully!");
                localContext.clonedProject.activityLog.add(localContext.username + " cloned project");
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
                // Deep copy the files and commits from the local context to the project on the server
                Project projectToPush = new Project(localContext.clonedProject);
                GlobalContext.projects.put(projectName, projectToPush);
                serverOutput.println("Push successful!");
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
                if(localContext.clonedProject == null 
                   || !localContext.clonedProject.name.equals(projectName)){
                    serverOutput.println("The local project is not the same as the project you are trying to pull!");
                    return;
                }
                Project localProject = localContext.clonedProject;
                localProject.files.clear(); // clear the local files first
                // Deep copy each file from the server project to the local project
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
        serverOutput.println("┌───────────────────────────────────────────────────────────────┐");
        serverOutput.println("│                        COMMANDS MENU                          │");
        serverOutput.println("├───────────────────────────────────────────────────────────────┤");
        serverOutput.println("│ 1. LOGIN {user} {pass}            : Log into server           │");
        serverOutput.println("│ 2. DISCONNECT                     : Log out of server         │");
        serverOutput.println("│ 3. CREATE {project}               : Make a new project        │");
        serverOutput.println("│ 4. LIST                           : List all projects         │");
        serverOutput.println("│ 5. CLONE {project}                : Copy a server project     │");
        serverOutput.println("│ 6. COMMIT {message}               : Save your changes         │");
        serverOutput.println("│ 7. LOG                            : Show cloned project log   │");
        serverOutput.println("│ 8. COMMITLOG                      : Show server project log   │");
        serverOutput.println("│ 9. PUSH {project}                 : Upload cloned project     │");
        serverOutput.println("│ 10. PULL {project}                : Download server project   │");
        serverOutput.println("│ 11. SHUTDOWN {key}                : Turn off the server       │");
        serverOutput.println("│ 12. CREATEFILE {filename}         : Make a new file           │");
        serverOutput.println("│ 13. VIEWFILE {filename}           : Read a file               │");
        serverOutput.println("│ 14. WRITEFILE {filename} {content}: Append to a file          │");
        serverOutput.println("│ 15. LISTFILES {project}           : List all files in project │");
        serverOutput.println("│ 16. HELP                          : Show this menu            │");
        serverOutput.println("└───────────────────────────────────────────────────────────────┘");
    }



    
    public void handleCreateFile(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid CREATEFILE command. Usage: CREATEFILE {filename}");
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
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
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
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
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
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
    
    public void handleRemove(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 3) {
            serverOutput.println("Invalid REMOVE command. Usage: REMOVE {projectName} {fileName}");
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
        } else {
            String projectName = parts[1];
            String fileName = parts[2];
            if (GlobalContext.projects.containsKey(projectName)) {
                Project project = GlobalContext.projects.get(projectName);
                if (project.files.containsKey(fileName)) {
                    project.files.remove(fileName);
                    serverOutput.println("File removed successfully!");
                } else {
                    serverOutput.println("File does not exist in this project!");
                }
            } else {
                serverOutput.println("Project does not exist!");
            }
        }
    }
    
    public void handleListFiles(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid LISTFILES command. Usage: LISTFILES {projectName}");
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
        } else {
            String projectName = parts[1];
            if (GlobalContext.projects.containsKey(projectName)) {
                Project project = GlobalContext.projects.get(projectName);
                if (!project.files.isEmpty()) {               
                    serverOutput.println("│   Files in Project " + padRight(projectName, 20) + "  │");
                    
                    for (String fileName : project.files.keySet()) {
                        serverOutput.println("│ " + padRight(fileName, 25) + "│"); // Adjust spacing for alignment
                    }
                    serverOutput.println("└───────────────────────────┘");
                } else {
                    serverOutput.println("No files in project '" + projectName + "'!");
                }
            } else {
                serverOutput.println("Project '" + projectName + "' does not exist!");
            }
        }
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
    
   

}