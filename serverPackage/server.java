package serverPackage;

 import java.net.*;
 import java.io.*;

 public class server {
	  
	 // authentication method to validate username and password
	 private static boolean authenticate(String username, String password) {
		 // Hardcoding username and password for demonstration purposes
		 String validUsername = "user123";
		 String validPassword = "pass456";
		 String guestUser = "guest";
		 String guestPass = "123";
		 boolean validUser = username.equals(validUsername) && 
				 password.equals(validPassword);
		  
		 boolean validGuest = username.equals(guestUser) &&
				 password.equals(guestPass);
		    
		 if (validUser || validGuest) {
			 return true;
		 } else {
			 return false;
		 }
	 }
	  

     public static void main(String[] args) {
    	 int port = 6000;
    	 int connectionCount = 0;

    	 try (ServerSocket serverSocket = new ServerSocket(port)) {
    		 System.out.println("[Server] Server started successfully!");
    		 while (true) {
    			 // try-with-resources block; will auto close the resources at the end
    			 try ( Socket clientSocket = serverSocket.accept();
    					 BufferedReader in = 
    							 new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    					 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
    			  ) {
    				 connectionCount++;
    				 System.out.println("[Server] New client connected at port: " + port);
    				 System.out.println("[Server] Total number of connections/disconnections made: " + connectionCount);
    				 // welcome message to the client
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
    				 System.out.println("[Server] There was a problem: " + e);
    			 }
    		  }
    	 } catch (IOException e) {
    		 System.out.println("[Server] There was a problem: " + e);
    	 }
      }
 }