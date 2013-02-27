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
    private static final char LISTMETHODS = 'm';


    RMIRegistry(InetAddress _host, int _port) {
        host = _host;
        port = _port;
    }

    RMIRegistry(String _host, int _port) throws UnknownHostException {
        host = InetAddress.getByName(_host);
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

        
        oOs.writeObject(new RMIRegistryMessage(LOOKUP, objectName));

        RemoteObjectRef ror    = null;
        RMIRegistryMessage rsp = null;

        try {
            rsp = (RMIRegistryMessage)oIs.readObject();
        } catch (ClassNotFoundException e) {
            //TODO do we need to send an ack here?
            oOs.writeObject(new String("ACK"));
            e.printStackTrace();
        }

        oOs.writeObject(new String("ACK"));

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

    public String[] listRemoteNames() {
        Socket sock;
        ObjectInputStream oIs;
        ObjectOutputStream oOs;
        String[] names;

        
        try {
            sock = new Socket(host, port);
        } catch (Exception e) {
            System.err.println("listRemoteNames: error creating socket");
            return null;
        }

        try {
            oIs = new ObjectInputStream(sock.getInputStream());
            oOs = new ObjectOutputStream(sock.getOutputStream());
        } catch (Exception e) {
            System.err.println("listRemoteNames: error creating I/O streams");
            return null;
        }
 
        RMIRegistryMessage req = new RMIRegistryMessage(LISTMETHODS);

        try {
            oOs.writeObject(req);
            names = (String[])oIs.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            oOs.writeObject(new String("ACK"));
        } catch (Exception e) {
            System.err.println("listRemoteNames error: failed to send ACK");
        }

        return names;
    }

}
