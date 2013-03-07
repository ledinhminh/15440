/**
 * Kevin Bravo and Nick Zukoski
 * 15440 Project 2
 * 
 */
import java.io.*;
import java.net.Socket;

/**
 * 
 * The abstract class that all stubs for remote objects inherit from.
 * Contains the logic to send a message to the server and receive 
 * a response. Also throws exceptions if need be.
 *
 */
public abstract class RemoteStub implements Remote440 {
    private RemoteObjectRef ror;

    public void setRemoteRef(RemoteObjectRef _ror) {
        ror = _ror;
    }

    public RemoteObjectRef getRemoteRef() {
        return ror;
    }
    
    protected Object executeMessage(RMIMessage msg) throws Remote440Exception, Exception {
    	Socket sock = null;
    	//make sure that the reference is attached to the message
    	if (msg.getRor() == null) 
    		msg.setRor(ror);
    	
    	try {
			sock = new Socket(ror.getInetAddress(), ror.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	ObjectOutputStream oOs = null;
        ObjectInputStream oIs  = null;
        RMIMessage response = null;
            
        try {
			oOs = new ObjectOutputStream(sock.getOutputStream());
			oIs = new ObjectInputStream(sock.getInputStream());
	        oOs.writeObject(msg);
	        response = (RMIMessage)oIs.readObject();
		} catch (IOException e) {
			throw new Remote440Exception("IOException communicating with host");
		} catch (ClassNotFoundException e) {
			throw new Remote440Exception("Response class not found");
		}
        
    	
    	if (response.exceptionWasThrown())
    		throw response.getException();
    	
    	return response.getReturnValue();
    }
}
