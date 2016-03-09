import java.net.*;
import java.io.*;

public class KawungMsgServer extends Thread
{
   private ServerSocket serverSocket;
   
   public KawungMsgServer(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(60000);
   }

   public void run()
   {
   		
      while(true)
      {
         try
         {
            System.out.println("Waiting for client on port " +
            serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            System.out.println("Just connected to "
                  + server.getRemoteSocketAddress());
            DataInputStream in =
                  new DataInputStream(server.getInputStream());
            System.out.println(in.readUTF());
            DataOutputStream out =
                 new DataOutputStream(server.getOutputStream());
            out.writeUTF("Thank you for connecting to "
              + server.getLocalSocketAddress() + "\nGoodbye!");
            server.close();
         }catch(SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
            break;
         }catch(IOException e)
         {
            e.printStackTrace();
            break;
         }
      }
   }

   public void timeFormater() {

   }

   public static void main(String [] args)
   {
      //int port = Integer.parseInt(args[0]);
   		int port = 6066; //define port here
      try
      {
         Thread t = new KawungMsgServer(port);
         t.start();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}