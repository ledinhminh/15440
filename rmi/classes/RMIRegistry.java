import java.io.*;
import java.lang.*;
import java.net.*;


public class RMIRegistry {
    InetAddress host;
    int port;

    private static final char FINDREG = 'r';
    private static final char ISREG   = 'i';
    private static final char LOOKUP  = 'l';
    private static final char FOUND   = 'f';

    RMIRegistry(InetAddress _host, int _port) {
        host = _host;
        port = _port;
    }

    public RemoteObjectRef getRemoteObjectRef (String objectName) 
                                                throws IOException
    {
        Socket sock = null;

        try {
            sock = new Socket(host, port);
        } catch (Exception e) {
            System.err.println("getRemoteObjectRef: error creating socket");
        }

        ObjectOutputStream oOs = null;
        ObjectInputStream oIs  = null;

        RMIRegistryMessage msg = new RMIRegistryMessage(FINDREG);
            
        oOs = new ObjectOutputStream(sock.getOutputStream());
        oIs = new ObjectInputStream(sock.getInputStream());
        oOs.writeObject(msg);
            
        try {
            if (((RMIRegistryMessage)oIs.readObject()).getMessageType() == ISREG) {
                System.out.println("Found a registry!");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        RMIRegistryMessage lookup = new RMIRegistryMessage(LOOKUP, objectName);
        
        oOs.writeObject(lookup);

        RemoteObjectRef ror    = null;
        RMIRegistryMessage rsp = null;

        try {
            rsp = (RMIRegistryMessage)oIs.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //TODO send an ACK

        if (rsp.getMessageType() == FOUND) {
            ror = rsp.getRemoteObjectRef();
        } else {
            return null;
        }

        return ror;
    }

    public String getRegistryHostName() {
        return host.toString();
    }

    public InetAddress getRegistryInetAddress() {
        return host;
    }

    public int getRemoteRegistryPort() {
        return port;
    }

        
}
