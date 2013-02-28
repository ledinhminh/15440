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
    private static final char LIST    = 'm';


    public RMIRegistry(InetAddress _host, int _port) {
        host = _host;
        port = _port;
    }

    public RMIRegistry(String _host, int _port) throws UnknownHostException {
        host = InetAddress.getByName(_host);
        port = _port;
    }

    /** getRemoteObjectRef()
     * returns the remote object ref retreived from the server
     * using the name objectname
     * @param objectName - the name of the remote object
     */
    public RemoteObjectRef getRemoteObjectRef (String objectName) 
                                 throws IOException, Remote440Exception
    {
        Socket sock = null;

        //create the socket
        try {
            sock = new Socket(host, port);
        } catch (Exception e) {
            throw new Remote440Exception(
                        "getROR: error creating socket");
        }

        ObjectOutputStream oOs = null;
        ObjectInputStream oIs  = null;

        RMIRegistryMessage msg = new RMIRegistryMessage(FINDREG);
            
        oOs = new ObjectOutputStream(sock.getOutputStream());
        oIs = new ObjectInputStream(sock.getInputStream());
        oOs.writeObject(msg);
        oOs.flush();        

        //verify that it's actually an RMI registry server
        try {
            if (((RMIRegistryMessage)oIs.readObject()).getMessageType() == ISREG) {
                System.out.println("Found a registry!");
            }
        } catch (ClassNotFoundException e) {
            throw new Remote440Exception("getROR: failed to read message(1) " +
                                        "from host; ClassNotFoundException");
        }

        //LOOKUP the object on the server
        oOs.writeObject(new RMIRegistryMessage(LOOKUP, objectName));
        oOs.flush();

        RemoteObjectRef ror    = null;
        RMIRegistryMessage rsp = null;

        //get the response, which should contain an objectref, from the server
        try {
            rsp = (RMIRegistryMessage)oIs.readObject();
        } catch (ClassNotFoundException e) {
            //TODO do we need to send an ACK here?
            oOs.writeObject(new String("ACK"));
            throw new Remote440Exception("getROR: failed to read message(2) " +
                                         "from host; ClassNotFoundException");
        }

        //ackit
        oOs.writeObject(new String("ACK"));


        //if it's not FOUND, it wasn't found.  get over it.  no server wants
        //a needy client
        if (rsp.getMessageType() == FOUND) {
            ror = rsp.getRemoteObjectRef();
        } else {
            throw new Remote440Exception("Object not found!");
        }

        return ror;
    }

    /** getRegistryHostName()
     */
    public String getRegistryHostName() {
        return host.toString();
    }

    /** getRegistryInetAddress()
     * gets the remote InetAddress of this RMI registry
     */
    public InetAddress getRegistryInetAddress() {
        return host;
    }

    /** getRemoteRegistryPort()
     * returns the remote port of this registry
     */
    public int getRemoteRegistryPort() {
        return port;
    }


    /** listRemoteNames()
     * lists the remote object names on the RMI registry
     */
    public String[] listRemoteNames() {
        Socket sock;
        ObjectInputStream oIs;
        ObjectOutputStream oOs;
        String[] names;

        //connect to the server
        try {
            sock = new Socket(host, port);
        } catch (Exception e) {
            System.err.println("listRemoteNames: error creating socket");
            return null;
        }

        //create the I/O streams with the server
        try {
            oIs = new ObjectInputStream(sock.getInputStream());
            oOs = new ObjectOutputStream(sock.getOutputStream());
        } catch (Exception e) {
            System.err.println("listRemoteNames: error creating I/O streams");
            return null;
        }

        
        //send a request for the object name list
        try {
            oOs.writeObject(new RMIRegistryMessage(LIST));
            names = (String[])oIs.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        //ackit
        try {
            oOs.writeObject(new String("ACK"));
        } catch (Exception e) {
            System.err.println("listRemoteNames error: failed to send ACK");
        }

        return names;
    }

}
