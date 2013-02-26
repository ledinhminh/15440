import java.io.*;
import java.lang.*;
import java.net.Socket;

public abstract class RemoteStub implements Remote440 {
    private RemoteObjectRef ror;

    public void setRemoteRef(RemoteObjectRef _ror) {
        ror = _ror;
    }

    public RemoteObjectRef getRemoteRef() {
        return ror;
    }
    
    private Object executeMessage(RMIMessage msg) throws Remote440Exception, Exception {
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
			throw new Remote440Exception();
		} catch (ClassNotFoundException e) {
			throw new Remote440Exception();
		}
        
    	
    	if (response.exceptionWasThrown())
    		throw response.getException();
    	
    	return response.getReturnValue();
    }
}