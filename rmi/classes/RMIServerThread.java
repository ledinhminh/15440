import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

public class RMIServerThread extends Thread {
    private static final char FINDREG  = 'r';
    private static final char ISREG    = 'i';
    private static final char LOOKUP   = 'l';
    private static final char FOUND    = 'f';
    private static final char NOTFOUND = 'n';
    private static final char LIST     = 'm';
    private static final char TERM     = 't';

    private ObjectInputStream oIs;
    private ObjectOutputStream oOs;
    private Socket clientSock;
    private boolean invoker = false;

    private Map<String, RemoteObjectRef> nameToROR;
    private Map<RemoteObjectRef, Object> RORToObject;


    private RMIMessage doMessage;

    public RMIServerThread (Socket _clientSock, ObjectInputStream _oIs,
                            ObjectOutputStream _oOs, boolean _invoker,
                            RMIMessage _doMessage) {
        clientSock = _clientSock;
        nameToROR  = Collections.synchronizedMap(new HashMap());
        oOs        = _oOs;
        oIs        = _oIs;
        invoker    = _invoker;
        doMessage  = _doMessage;
    }


    public RMIServerThread (Socket _clientSock, ObjectInputStream _oIs,
                            ObjectOutputStream _oOs) {
        clientSock = _clientSock;
        nameToROR  = Collections.synchronizedMap(new HashMap());
        oOs        = _oOs;
        oIs        = _oIs;
    }



    public void registerROR(String _name, RemoteObjectRef _ror) {
        nameToROR.put(_name, _ror);
    }

    public void removeROR(String _name) {
        nameToROR.remove(_name);
    }



    /**giveLargeResponse()
     * talks to a client who's tryna get us to invoke methods
     */
    public void giveLargeResponse() {
        RemoteObjectRef ror = doMessage.getRor();
        
        if (ror == null) {
            System.err.println("No RMIMessage found...giving up");
            return;
        }

        
        //we've got a good object...do stuff
        if (!doMessage.invokeOnObject(RORToObject.get(ror))) {
            doMessage.setReturnValue(null);
            Exception e = new Remote440Exception(
                        "Bad RemoteObjectReference.  Couldn't" +
                        " find object on host");
            doMessage.setException(e, true);
        }
        
        
        try {
            //write the object, get the ack
            oOs.writeObject(doMessage);
            oIs.readObject();
        } catch (Exception e) {
            try {
                clientSock.close();
            } catch (Exception e1) {
                System.err.println("Error closing client socket");
            }
        }

        return;
    }



    /**giveSimpleResponse()
     * talks to a client who's just looking for new object refs
     */
    public void giveSmallResponse() {
        System.out.println("is it connected? " + clientSock.isConnected());
        while (clientSock.isConnected()) {

            //listen to the client's message, do server things
            RMIRegistryMessage msg;
            RMIRegistryMessage rsp;
            RemoteObjectRef ref;
 
            try {
                oOs.writeObject(new RMIRegistryMessage(ISREG));
                oOs.flush();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            System.out.println("reading cmd");
            try {
                msg = (RMIRegistryMessage)oIs.readObject();
            } catch (Exception e) {
                System.err.println("ServerThread: Error reading message!");
                continue;
            }

            System.out.println("doing cmd");
            switch(msg.getMessageType()) {

                case LOOKUP:
                    //lookup the RemoteObjectRef
                    System.out.println("LOOKUP " + msg.getRMIObjectName());
                    if ((ref = nameToROR.get(msg.getRMIObjectName())) != null) {
                        //get the ref and pass it to client
                        rsp = new RMIRegistryMessage(FOUND, ref);
                    } else {
                        rsp = new RMIRegistryMessage(NOTFOUND);
                    }

                    try {
                        oOs.writeObject(rsp);
                        //get the ACK
                        oIs.readObject();
                    } catch (Exception e) {
                        System.err.println("ServerThread: Error in I/O with client " +
                                        "Or receiving ACK");
                    }
                    break;
                    
                case LIST:
                    //create a list of names (keys in nameToROR)
                    try {
                        String[] names = (String[])nameToROR.keySet().toArray();
                        oOs.writeObject(names);
                        //ACK
                        oIs.readObject();
                    } catch (Exception e) {
                        System.err.println("ServerThread: Error writing list to"
                                        + " client or receiving ACK");
                    }
                    break;

                case TERM:
                    //TODO do we need dis?
                    break;
                
            }


        }
    }


    /**
     * run()
     * takes the connection to the client and does server things
     * passes back RemoteObjectReferences
     */

    public void run () {

        if (invoker) {
            giveLargeResponse();
        } else {
            giveSmallResponse();
        }
               
    }

}
