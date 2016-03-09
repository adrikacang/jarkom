import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

public class KawungMsgServer 
{
   private static int uniqueID;
   private ArrayList<ClientThread> al;
   private int port;
   private SimpleDateFormat sdf;
   //Boolean to turn off server
   private boolean keepGoing;
   
   public KawungMsgServer(int portNumber) {
      this.port = 6066;
      sdf = new SimpleDateFormat("HH:mm");
      al = new ArrayList<ClientThread>();
   }

   //Start the server
   public void start() {
      keepGoing = true;
      //Create serversocket and wait for client
      try
      {
         // the socket used by the server
         ServerSocket serverSocket = new ServerSocket(port);
         while(keepGoing)
         {
            Socket socket = serverSocket.accept();//accept connection
            if(!keepGoing)
               break;
            ClientThread t = new ClientThread(socket); //create new thread
            al.add(t); //save thread in arraylist
            t.start();
         }
         //keepGoing = false or asked server socket to stop
         try {
            serverSocket.close();
         } catch (Exception e) {
            System.out.println("Exception closing server " + e);
         }
      } 
      //Exception when server socket failed
      catch(IOException e) {
          String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
      }
   }

   /*
   *  Broadcast message to all client
   */
   private synchronized void broadcast(String username, String message) {
      //add time to the message
      String time = sdf.format(new Date());
      String messageLf = username + " (" + time + ") " + message;

      //loop in reverse order because the a client
      //may have disconect and have to be removed
      for(int i = al.size(); --i >= 0;) {
         ClientThread ct = al.get(i);
         if(!ct.writeMsg(messageLf)) {
            al.remove(i);
            System.out.println("Disconected client " + ct.username + " removed from list.");
         }
      }  
   }

   //For a client who logoff using the EXIT message
   synchronized void remove(int id) {
      //Scan the arraylist until we found the id
      for(int i = 0; i < al.size(); i++) {
         ClientThread ct = al.get(i);
         if(ct.id == id) {
            al.remove(i); //remove the thread
            return;
         }
      }
   }

   public static void main(String [] args)
   {
      int portNumber = 6066;
      KawungMsgServer server = new KawungMsgServer(portNumber);
      server.start();
   }

   class ClientThread extends Thread
   {
      //the socket where to listen/talk
      Socket socket; 
      ObjectInputStream sInput;
      ObjectOutputStream sOutput;
      int id;
      String username;
      String date;
      //Message that passed betwen server and client
      ChatMessage cm;

      //Constructor
      public ClientThread(Socket socket) {
         id = ++uniqueID;
         this.socket = socket;
         System.out.println("Thread trying to create object io stream");
         try {
            //Create input output stream
            sOutput = new ObjectOutputStream(socket.getOutputStream());
            sInput = new ObjectInputStream(socket.getInputStream());
            username = (String) sInput.readObject();
         }
         catch (IOException e) {
            System.out.println("Error creating new io streams");
            return;
         }
         catch (ClassNotFoundException e) {

         }
         date = new Date().toString() + "\n";
      }
      //thread run forever until EXIT command
      public void run() {
         //to loop until EXIT
         boolean keepGoing = true;
         while(keepGoing) {
            //read a string
            try {
				cm = (ChatMessage) sInput.readObject();
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            //message part of chat message
            String message = cm.getMessage();
            //switch on the type of message
            switch(cm.getType()) {
               case ChatMessage.MESSAGE:
                  broadcast(username, message);
                  break;
               case ChatMessage.LOGOUT:
                  keepGoing = false;
                  break;
            }
         }
         remove(id);
         close();
      }
      //try to close everything
      private void close() {
         try  {
            if(sOutput != null) sOutput.close();
            if(sInput != null) sInput.close();
            if(socket != null) socket.close();
         } catch(IOException e) {}
           
      }
      //Write a String to the client output stream
      private boolean writeMsg(String msg) {
         if(!socket.isConnected()) {
            close();
            return false;
         } try {
            sOutput.writeObject(msg);
            System.out.println("Pesan : "); 
         } catch(IOException e) {
            System.out.println("Error sending message to " + username);
         }
         return true;
      }
   } 
}