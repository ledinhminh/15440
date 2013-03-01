import java.net.InetAddress;

public class RemoteObjectRef {
    InetAddress iAddr;
	int port;
    int objKey;
    String remoteInterfaceName;



    public RemoteObjectRef(InetAddress _iAddr, int _port, int _objKey, String iname) {
        iAddr  = _iAddr;
        port   = _port;
        objKey = _objKey;
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
    public int getObjKey() {
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
