/**
 * Kevin Bravo and Nick Zukoski
 * 15440 Project 2
 * 
 */
import java.net.InetAddress;
import java.io.*;


/**
 * 
 * RemoteObjectRef: A reference to a remote object. To get a usable
 * stub for this object call localise().
 *
 */
public class RemoteObjectRef implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5481496144259982534L;
	private InetAddress iAddr;
	private int port;
    private Integer objKey;
    private String remoteInterfaceName;



    public RemoteObjectRef(InetAddress _iAddr, int _port, int _objKey, String iname) {
        iAddr  = _iAddr;
        port   = _port;
        objKey = new Integer(_objKey);
        remoteInterfaceName = iname;
    }

    Object localise() {
    	String stubClassName = remoteInterfaceName + "_stub";
    	Object stub;
    	
    	try {
    		Class c = Class.forName(stubClassName);
			stub = c.newInstance();
			((Remote440)stub).setRemoteRef(this);
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    	return stub;
    	
    }

    public InetAddress getInetAddress() {
        return iAddr;
    }
    
    public void setPort(int port) {
		this.port = port;
	}
    
    public int getPort() {
    	return port;
    }
    public Integer getObjKey() {
		return objKey;
	}

	public void setObjKey(int objKey) {
		this.objKey = objKey;
	}

	public String getRemoteInterfaceName() {
		return remoteInterfaceName;
	}

	public void setRemoteInterfaceName(String _remoteInterfaceName) {
		this.remoteInterfaceName = _remoteInterfaceName;
	}


    boolean remoteEquals(RemoteObjectRef ror) {
        return (iAddr == ror.getInetAddress());
    }

}
