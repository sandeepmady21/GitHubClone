package serverpackage;

//Import java lib for networking and I/O operations
import java.net.ServerSocket;  // server socket waits for requests and connects with client sockets
import java.net.Socket;  // aka client sockets is an endpoint for communication b/w two machines
import java.io.BufferedReader; // reads text from an input stream (like a file or a socket)
import java.io.InputStreamReader; // acts as bridge from byte streams to character streams.
import java.io.IOException; // general class of exceptions for I/O operations

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
		// declare a variable to hold each line of text received from the client
		String inputLine;
		
		// 'in.readLine()' reads a line of text sent by the client.
		// loop continuously reads lines from the client till 'null' (end of stream)
		while ((inputLine = in.readLine()) != null) {
			
			//display message from client
			System.out.println("Client: " + inputLine);
			
			// if message is "exit", break out of the loop ending reception
			if (inputLine.equalsIgnoreCase("exit")) {
				break;
			}
		}
		
		// close resources
		in.close(); // close the BufferedReader
		clientSocket.close();
		serverSocket.close();
		
	}

}
