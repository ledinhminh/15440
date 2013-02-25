import java.io.*;
import java.lang.*;

public class RMIRegistry{
    String host;
    int port;

    RMIRegistry(String _host, int _port) {
        host = _host;
        port = _port;
    }

    public RemoteObjectRef getRemoteObjectRef(String objectName) {
        
        try {
            Socket sock = new Socket(Host, Port);
        } catch (Exception e) {
            System.err.println("getRemoteObjectRef: error creating socket");
        }

        try {
            RMIRegistryMessage msg = new RMIRegistryMessage('?', true);
            ObjectOutputStream oOs = new ObjectOutputStream(sock.getOutputStream);
            ObjectInputStream  oIs = new ObjectInputStream(sock.getInputStream());
            oOs.writeObject(msg);
        } catch (Exception e) {
            System.err.println("getRemoteObjectRef: error writing first message");
        }

        if ((RMIRegistryMessage)oIs.readObject().getResponseChar() == 'y')
            System.out.println("Found a registry!");

        RMIRegistryMessage lookup = new RMIRegistryMessage('l', objectName);
        
        try {
            oOs.writeObject(lookup);
        } catch (Exception e) {
            System.err.println("getRemoteObjectRef: Error sending lookup msg");
        }

        RemoteObjectRef ror;
        try {
            RMIRegistryMessage rsp = (RMIRegistryMessage)oIs.readObject();
        } catch (Exception e) {
            System.err.println("getRemoteObjectRef: Error reading response");
            return null;
        }

        if (rsp.getResponseChar() == 'f') {
            ror = rsp.getRMIObjectRef();
        } else {
            return null;
        }

        
}
