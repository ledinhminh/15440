import java.lang.*;
import java.io.*;
import java.net.*;


public class RMIServer {
    InetAddress localHost;
    int listenPort = 15440;
    ServerSocket sSock;

    private static final char FINDREG = 'r';
    private static final char ISREG   = 'i';
    private static final char LOOKUP  = 'l';
    private static final char FOUND   = 'f';
    private static final char LIST    = 'm';
    private static final char TERM    = 't';

    public static void main (String args[]) {
        localHost  = InetAddress.getLocalHost();

        if (args.length() != 3) {
            System.err.println("Wrong number of args!");
            return;
        }

        listenPort = args[2];
        
        try {
            sSock = new ServerSocket(listenPort);
        } catch (SocketException e) {
            System.err.println("Error creating the server socket! :) :) :)");
            return;
        }

        while (true) {
            //listen for new RMIRegistryMessage
            Socket sock;
            ObjectOutputStream oOs;
            ObjectInputStream oIs;

            try {
                sock = sSock.accept();
            } catch (IOException e) {
                System.err.println("You suck at listening for connections." +
                                    " Trying again...");
                continue;
            }

            try {
                oOs = new ObjectOutputStream(sock.getOutputStream());
                oIs = new ObjectInputStream(sock.getInputStream());
            } catch (Exception e) {
                System.err.println("error getting I/O streams from client.");
                sock.close();
                continue;
            }


            RMIRegistryMessage msg;

            try {
                msg = (RMIRegistryMessage)oIs.readObject();
            } catch (Exception e) {
                System.err.println("Error receiving msg");
                sock.close();
                continue;
            }

            if (msg.getMessageType() == FINDREG) {
                RMIServerThread serveThread;

                //create a thread for the guy
                serveThread = new RMIServerThread(sock);
                serveThread.start();

            }

        }

    }
}



