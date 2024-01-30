package clientPackage;

 import java.net.*;
 import java.util.Random;
 import java.util.Scanner;
 import java.io.*;

 public class client {

     private static BufferedReader in;
     private static PrintWriter out;
     private static String serverResponse;

     public static boolean loginRPC(String username, String password) throws IOException{
    	 out.println("Connect " + username + " " + password);
    	 serverResponse = in.readLine();
    	 System.out.println("[Client] Server message: " + serverResponse);
    	 return serverResponse.equalsIgnoreCase("User Authentication successful");
     }

     public static boolean disconnectRPC() throws IOException{
    	 out.println("Disconnect");
    	 serverResponse = in.readLine();
    	 System.out.println("[Client] Server message: " + serverResponse);
    	 return serverResponse.equalsIgnoreCase("Disconnected");
     }

     public static void main(String[] args) {
    	 String serverName = "localhost"; // add server IP or hostname
    	 int port = 6000;
    	 boolean isValidUser = false;
    	 try (Socket client = new Socket(serverName, port);
    		  Scanner scanner = new Scanner(System.in)) {

    		 in = new BufferedReader(new InputStreamReader(client.getInputStream()));
    		 out = new PrintWriter(client.getOutputStream(), true);   	
    		  
    		 while ((serverResponse = in.readLine()) != null) {
    			 System.out.println("[Client] Server message: " + serverResponse);
    			 while (!isValidUser) {
    				 System.out.print("[Client] Enter username (guest): ");
    				 String username = scanner.nextLine();
    				 System.out.print("[Client] Enter password (123): ");
    				 String password = scanner.nextLine();

    				 isValidUser = loginRPC(username, password);
    			 }
    			  
    			 // wait between 1 to 10 seconds
    			  
    			 int randNum = new Random().nextInt(10) + 1;
        		 System.out.println("[Client] Going to sleep now for " + randNum + "sec");
        		 for (int i = randNum; i > 0; i--) {
        			 System.out.println("[Client] Disconnecting in " + i + " sec");
        			 Thread.sleep(1000); // Sleep for 1 sec     	      
      	         }       		  
        		 disconnectRPC();
    		 }   		  	  
    		 
    		 System.out.println("[Client] Server disconnected successfully");
    		 System.out.println("[Client] Restart client if you want to connect again. No need to restart the Server!");

    	} catch (IOException | InterruptedException e) {
    		 	e.printStackTrace();
    	}
     }
 }