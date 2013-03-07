/**
 * Kevin Bravo and Nick Zukoski
 * 15440 Project 2
 * 
 */
import java.io.*;
import java.net.*;
import java.lang.*;
import java.util.*;

/**
 * 
 * RMIServerThread: does the guts of the RMI call handling.
 *
 */
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
    private int port;


    private Map<String, Integer> nameToKey;
    private Map<Integer, Object> keyToObject;



    private RMIMessage doMessage;

    public RMIServerThread (Socket _clientSock, int _localPort, ObjectInputStream _oIs,
                            ObjectOutputStream _oOs, RMIMessage _doMessage,
                            Map<String, Integer> _nameToKey,
                            Map<Integer, Object> _keyToObject) {
        //_keyToObject should be a synchronizedMap
        clientSock  = _clientSock;
        keyToObject = _keyToObject;
        nameToKey   = _nameToKey;
        oOs         = _oOs;
        oIs         = _oIs;
        invoker     = true;
        doMessage   = _doMessage;
        port        = _localPort;
    }


    public RMIServerThread (Socket _clientSock, int _localPort, ObjectInputStream _oIs,
                            ObjectOutputStream _oOs,
                            Map<String, Integer> _nameToKey, Map<Integer, Object> _keyToObject) {
        clientSock  = _clientSock;
        nameToKey   = _nameToKey;
        keyToObject = _keyToObject;
        oOs         = _oOs;
        oIs         = _oIs;
        port        = _localPort;
    }


    /**giveLargeResponse()
     * talks to a client who's tryna get us to invoke methods
     */
    public void giveLargeResponse() {
        RemoteObjectRef ror = doMessage.getRor();
        
        Object[] args = doMessage.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof RemoteObjectRef) {
                args[i] = keyToObject.get(((RemoteObjectRef)args[i]).getObjKey());
            }
        }
        
        doMessage.setArgs(args);

        if (ror == null) {
            System.err.println("No RMIMessage found...giving up");
            return;
        }

        
        //we've got a good object...do stuff
        if (!doMessage.invokeOnObject(keyToObject.get(ror.getObjKey()))) {
            doMessage.setReturnValue(null);
            Exception e = new Remote440Exception(
                        "Bad RemoteObjectReference.  Couldn't" +
                        " find object on host");
            doMessage.setException(e, true);
        }
        
        
        try {
            doMessage.setArgs(new Object[0]);
            //write the object, get the ack
            oOs.writeObject(doMessage);
            oOs.flush();
            oIs.readObject();
        } catch (Exception e) {
            try {
                clientSock.close();
            } catch (Exception e1) {
                System.err.println("Error closing client socket");
            }
            return;
        }

        return;
    }



    /**giveSimpleResponse()
     * talks to a client who's just looking for new object refs
     */
    public void giveSmallResponse() {

        if (clientSock.isConnected()) {

            //listen to the client's message, do server things
            RMIRegistryMessage msg;
            RMIRegistryMessage rsp;
            RemoteObjectRef ref;
 
            try {
                oOs.writeObject(new RMIRegistryMessage(ISREG));
                oOs.flush();
            } catch (Exception e) {
                System.err.println("giveSmallRsp()...failed writing an object");
                return;
            }

            try {
                msg = (RMIRegistryMessage)oIs.readObject();
            } catch (Exception e) {
                System.err.println("ServerThread: Error reading message!");
                return;
            }

            switch(msg.getMessageType()) {

                case LOOKUP:
                    //lookup the RemoteObjectRef
                    Integer key;
                    if ((key = nameToKey.get(msg.getRMIObjectName())) != null) {
                        //get the ref and pass it to client
                        try {
                            InetAddress iaddr = InetAddress.getLocalHost();
                            if (keyToObject == null) System.err.println("fx");
                            Object obj = keyToObject.get(key);
                            RemoteObjectRef rspRef = new RemoteObjectRef(
                                        iaddr, port, key, obj.getClass().getName());
                            rsp = new RMIRegistryMessage(FOUND, rspRef);
                        } catch (Exception e) {
                            //send a 440Exception?
                            e.printStackTrace();
                            rsp = new RMIRegistryMessage(NOTFOUND);
                        }

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
                    //create a list of names (keys in nameToKey)
                    try {
                        String[] names = (String[])nameToKey.keySet().toArray();
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
