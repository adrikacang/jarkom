
import java.net.*;
import java.io.*;
import java.util.*;

public class KawungMsgClient
{
	// for IO
	private ObjectInputStream sInput;
	private ObjectOutputStream sOutput;
	private Socket socket;

	//the server, username, port
	private String server, username;
	private int port;
	

	/*
	 * Constructor
	 * server: server adress
	 * port: the port number
	 *  username: the username
	*/
	KawungMsgClient(String server, int port, String username) {
		this.server = server;
		this.port = port;
		this.username = username;
	}

	//Start dialog
	public boolean start() {
		//connect to server
		try {
			socket = new Socket(server, port);
		}
		catch (Exception ec) {
			System.out.println("Error connectiong to server: " + ec );
		}
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		System.out.println(msg);
		try {
			sInput = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
		}
		catch(IOException eIO) {
			System.out.println("Ecxception creating new IO Stream: " + eIO);
			return false;
		}
		new ListenFromServer().start(); //start listening input from server
		try
		{
			sOutput.writeObject(username);
		}
		catch(IOException eIO) {
			System.out.println("Ecxception doing login: " + eIO);
			disconnect();
			return false;
		}
		return true;
	}

	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			System.out.println("Exception writing to server: " + e);
		}
	}

	/*
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
	}



   	public static void main(String [] args)
   	{
   		int portNumber = 6066;
   		String serverAddress = "localhost";
   		String username = "Anonymous";
   		KawungMsgClient client = new KawungMsgClient(serverAddress,portNumber,username);
   		if(!client.start()) return;
   		Scanner scan = new Scanner(System.in);
		System.out.println("=======================");
		System.out.println("PAPAN PESAN KAWUNG CSUI");
		System.out.println("=======================");
		System.out.println("Ketik 'exit' untuk keluar");
		System.out.println();
		System.out.print("Pesan : ");	
		//infinite loop until exit
		while(true) {
			String input = scan.nextLine();
		    if(input.equalsIgnoreCase("exit")) {
		    	client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
		    	client.disconnect();
		    	break;
		    }
		    else {
		    	client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, input));
		    }
		}
		scan.close();
   }

    /* a class that waits for the message from the server and append them to the 
     * console mode
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
					System.out.println(msg);
				} 
				catch(IOException e) {
					System.out.println("Input listener exiting...");
					break;
				}
				catch(ClassNotFoundException e2) {}
			}
				
		}
	}

}