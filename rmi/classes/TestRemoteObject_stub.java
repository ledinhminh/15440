/**
 * Kevin Bravo and Nick Zukoski
 * 15440 Project 2
 * 
 */

/**
 * 
 * TestRemoteObject_stub: A handmade stub for TestRemoteObject
 *
 */
public class TestRemoteObject_stub extends RemoteStub {
	TestRemoteObject obj;
	
	public void setRemoteObject(TestRemoteObject o) {
		obj = o;
	}
	

    public void incrementErp(TestRemoteObject_stub kb) 
                    throws Remote440Exception, Exception{
    	//If a remote stub is passed in, send the ROR to the server so that it can dereference it there
        RMIMessage m = new RMIMessage("incrementErp", new Object[] {kb.getRemoteRef()});

        executeMessage(m);
    }

        
    public Integer getErp(TestRemoteObject_stub kb) 
                        throws Remote440Exception, Exception {
    	//If a remote stub is passed in, send the ROR to the server so that it can dereference it there
    	RMIMessage m = new RMIMessage("getErp", new Object[] {kb.getRemoteRef()});

        return (Integer)executeMessage(m);
    }




	public String getS() throws Remote440Exception, Exception {
		RMIMessage m = new RMIMessage("getS", new Object[0]);
		
		return (String)executeMessage(m);
	}

	public void setS(String s) throws Remote440Exception, Exception {
		RMIMessage m = new RMIMessage("setS", new Object[] {s});
		
		executeMessage(m);
	}
	
	public String concatS(String s2) throws Remote440Exception, Exception{
		RMIMessage m = new RMIMessage("concatS", new Object[] {s2});
		
		return (String)executeMessage(m);
	}

	public String concat2(String s1, String s2) throws Remote440Exception, Exception{
		RMIMessage m = new RMIMessage("concat2", new Object[] {s1,s2});
		
		return (String)executeMessage(m);
	}
	
	public void throwException() throws Remote440Exception, Exception {
		RMIMessage m = new RMIMessage("throwException", new Object[0]);
		
		executeMessage(m);
	}

}
