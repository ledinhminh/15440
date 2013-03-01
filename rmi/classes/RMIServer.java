import java.lang.*;
import java.io.*;
import java.net.*;


public class RMIServer {
    private static InetAddress localHost;
    private static int listenPort = 15440;
    private static ServerSocket sSock;

    private static final char FINDREG = 'r';
    private static final char ISREG   = 'i';
    private static final char LOOKUP  = 'l';
    private static final char FOUND   = 'f';
    private static final char LIST    = 'm';
    private static final char TERM    = 't';


    public static void doClientCommands(Socket sock) {
        ObjectInputStream oIs;
        ObjectOutputStream oOs;
        try {
            oOs = new ObjectOutputStream(sock.getOutputStream());
            oIs = new ObjectInputStream(sock.getInputStream());
        } catch (Exception e) {
            System.err.println("error getting I/O streams from client.");
            try {
                sock.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return;
        }


        Object msg;

        try {
            msg = oIs.readObject();
        } catch (Exception e) {
            System.err.println("Error receiving msg");
            try {
                sock.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return;
        }

        if (msg.getClass().getName().equals("RMIRegistryMessage")) {

            if ((((RMIRegistryMessage)msg).getMessageType()) == FINDREG) {
                RMIServerThread serveThread;

                //create a thread for the guy
                serveThread = new RMIServerThread(sock, oIs, oOs);
                serveThread.start();
            }
        }

        else if (msg.getClass().getName().equals("RMIMessage")) {
            //probably a method invokation, lets dooit
            
        
        }

    }



    public static void main (String args[]) {

        try {
            localHost  = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (args.length != 1) {
            System.err.println("Wrong number of args!" + args.length);
            return;
        }

        listenPort = (new Integer(args[0])).intValue();
 
        try {
            sSock = new ServerSocket(listenPort);
        } catch (Exception e) {
            System.err.println("Error creating the server socket! :) :) :)");
            return;
        }

        while (true) {
            //listen for new RMIRegistryMessage
            Socket sock;

            try {
                sock = sSock.accept();
            } catch (IOException e) {
                System.err.println("You suck at listening for connections." +
                                    " Trying again...");
                continue;
            }
            
            doClientCommands(sock);
        }

    }
}



