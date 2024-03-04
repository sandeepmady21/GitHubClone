package server;

import java.io.PrintWriter;

import common.Commit;
import common.GlobalContext;
import common.LocalContext;
import common.Project;

public class CommandHandler {

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

    public void handleClone(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid CLONE command. Usage: CLONE {name}");
        } else {
            String projectName = parts[1];
            if (GlobalContext.projects.containsKey(projectName)) {
                localContext.clonedProject = GlobalContext.projects.get(projectName);
                serverOutput.println("Project cloned successfully!");
                // after a project is successfully cloned, log it
                localContext.clonedProject.activityLog.add(localContext.username + " cloned project");
            } else {
                serverOutput.println("Project does not exist!");
            }
        }
    }

    public void handleCommit(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
        if (parts.length < 2) {
            serverOutput.println("Invalid COMMIT command. Usage: COMMIT {message}");
        } else if (localContext.clonedProject == null) {
            serverOutput.println("No project cloned!");
        } else {
            String commitMessage = parts[1];
            localContext.clonedProject.commits.add(new Commit(localContext.username, commitMessage));
            serverOutput.println("Commit successful!");
            // after a commit is successfully made, log it
      	  	localContext.clonedProject.activityLog.add(localContext.username + " made a commit: " + parts[1]);
        }
    }

    public void handleLog(String[] parts, PrintWriter serverOutput, LocalContext localContext) {
  	  	if (localContext.clonedProject == null) {
  	  		serverOutput.println("No project cloned!");
  	  	} else {
  	  		Project project = GlobalContext.projects.get(localContext.clonedProject.name);
  	  		if (project != null) {
  	  			for(String log : project.activityLog) {
  	  				serverOutput.println(log);
  	  			}
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
                serverOutput.println("Push successful!");
                // after a push is successfully made, log it
          	  	GlobalContext.projects.get(projectName).activityLog.add(localContext.username + " pushed changes");
            } else {
                serverOutput.println("Project does not exist on server!");
            }
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

}