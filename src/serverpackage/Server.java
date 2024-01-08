package serverpackage;

//Import java lib for networking and I/O operations
import java.net.ServerSocket;  // server socket waits for requests and connects with client sockets
import java.net.Socket;  // aka client sockets is an endpoint for communication b/w two machines
import java.io.BufferedReader; // reads text from an input stream (like a file or a socket)
import java.io.InputStreamReader; // acts as bridge from byte streams to character streams.
import java.io.IOException; // general class of exceptions for I/O operations
import java.io.PrintWriter;

public class Server {
	public static void main(String[] args) throws IOException {
		// create new server socket and bind it to port 1234
		ServerSocket serverSocket = new ServerSocket(1234);
		
		/** 
		 * listen and accept a connection from client. 
		 * waits (blocks) until a client connects to the server.
		 * Once client connects, it returns a 'Socket' object representing client socket
		 */ 
		Socket clientSocket = serverSocket.accept();
		
		/**
		 * Read Client Data: set up BufferedReader on the input stream of client socket
		 * This is used to read the data sent by the client
		 */
		BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		String userName = "";
		// declare a variable to hold each line of text received from the client
		String inputLine;
		
		// 'in.readLine()' reads a line of text sent by the client.
		// loop continuously reads lines from the client till 'null' (end of stream)

		while ((inputLine = in.readLine()) != null) {
			// If the input is a "Connect" RPC, set the username
			if (inputLine.startsWith("Connect")) {
				String[] params = inputLine.split("\\s+");
				userName = params[1];
				int result = connect(userName, params[2]);
				out.println("ConnectResult: " + result);
			} else {
				// If it's a regular message, display the username with the message
				System.out.println(userName + ": " + inputLine);
			}

			if (inputLine.equalsIgnoreCase("exit")) {
				break;
			}
		}
		
		// close resources
		in.close(); // close the BufferedReader
		clientSocket.close();
		serverSocket.close();
		out.close();
		
	}
	// Connect RPC method
	private static int connect(String userName, String password) {
		// Hardcoded credentials for demonstration purposes
		String validUserName = "user123";
		String validPassword = "pass456";

		// Check if the provided username and password match the valid credentials
		if (userName.equals(validUserName) && password.equals(validPassword)) {
			System.out.println("Authentication successful for user: " + userName);
			return 1; // Success code
		} else {
			System.out.println("Authentication failed for user: " + userName);
			return 0; // Failure code
		}
	}


}
