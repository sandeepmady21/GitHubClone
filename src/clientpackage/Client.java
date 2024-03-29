package clientpackage;

import java.net.Socket;  // for client side socket
/**
 * For printing formatted representations of objects to a text-output stream
 * Used to send text data to the server
 */
import java.io.PrintWriter; 
import java.io.BufferedReader; // to read data from the console
import java.io.InputStreamReader; // to convert byte streams
import java.io.IOException; 

public class Client {
	public static void main(String[] args) throws IOException {
		// create a client socket and connect to a port
		Socket socket = new Socket("localhost", 1234);
		
		/**
		 * Set up output stream.
		 * create 'PrintWriter' on the output stream of the socket.
		 * true means 'PrintWriter' will automatically flush the output buffer.
		 */
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		
		// Setup BufferedReader to read text from the std input (console)
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		
		// declare variable to store user input
		String userInput;
		
		// Input loop: read lines from console as long as the input is not 'null'
		while((userInput = stdIn.readLine()) != null) {
			out.println(userInput);
			if (userInput.equalsIgnoreCase("exit")) {
				break;
			}
		}
		
		// close Resourses
		out.close();
		stdIn.close();
		socket.close();
	}
}
