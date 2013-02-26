
public class TestRemoteObject_stub {
	TestRemoteObject obj;
	
	public void setRemoteObject(TestRemoteObject o) {
		obj = o;
	}
	
	
	public String getS() throws Remote440Exception, Exception {
		RMIMessage m = new RMIMessage("getS", new Object[0]);
		
		if (m.invokeOnObject(obj)) {
			if (m.exceptionWasThrown())
				throw m.getException();
			else
				return (String)m.getReturnValue();
		}
		else {
			throw new Remote440Exception();
		}
	}

	public void setS(String s) throws Remote440Exception, Exception {
		RMIMessage m = new RMIMessage("setS", new Object[] {s});
		
		if (m.invokeOnObject(obj)) {
			if (m.exceptionWasThrown())
				throw m.getException();
			else
				return;
		}
		else {
			throw new Remote440Exception();
		}
	}
	
	public String concatS(String s2) throws Remote440Exception, Exception{
		RMIMessage m = new RMIMessage("concatS", new Object[] {s2});
		
		if (m.invokeOnObject(obj)) {
			if (m.exceptionWasThrown())
				throw m.getException();
			else
				return (String)m.getReturnValue();
		}
		else {
			throw new Remote440Exception();
		}
	}

	public String concat2(String s1, String s2) throws Remote440Exception, Exception{
		RMIMessage m = new RMIMessage("concat2", new Object[] {s1,s2});
		
		if (m.invokeOnObject(obj)) {
			if (m.exceptionWasThrown())
				throw m.getException();
			else
				return (String)m.getReturnValue();
		}
		else {
			throw new Remote440Exception();
		}
	}
	
	public void throwException() throws Remote440Exception, Exception {
		RMIMessage m = new RMIMessage("throwException", new Object[0]);
		
		if (m.invokeOnObject(obj)) {
			if (m.exceptionWasThrown())
				throw m.getException();
			else
				return;
		}
		else {
			throw new Remote440Exception();
		}
	}
	
	public static void main(String[] args) {
		TestRemoteObject_stub stub = new TestRemoteObject_stub();
		stub.setRemoteObject(new TestRemoteObject());
		
		try {
			stub.setS("a");
			System.out.println(stub.getS());
			System.out.println(stub.concatS("b"));
			System.out.println(stub.concat2("b", "c"));
			try {
				stub.throwException();
			} catch (IndexOutOfBoundsException e){
				System.out.println("properly thrown");
			}
			
		} catch (Remote440Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	

}
