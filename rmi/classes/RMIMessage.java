import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;


public class RMIMessage implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4086249581158035244L;
	
	private Object[] args;
	private Class<?>[] argTypes;

	private String methodName;
	
	private Object returnValue;
	
	private boolean exceptionWasThrown;
	private Exception exception;
	
	private RemoteObjectRef ror;

	RMIMessage(String _methodName, Object[] _args) {
		setArgs(_args);
		this.methodName = _methodName;
	}
	
	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
		
		argTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			Class argClass = args[i].getClass();
			
			Class[] interfaces = argClass.getInterfaces();
			if (Arrays.asList(interfaces).contains(Remote440.class)) {
				//it is a remote object stub
				//maybe should be doing something?
                
			}
	
			argTypes[i] = argClass;
		}
	}

	public Class<?>[] getArgTypes() {
		return argTypes;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public boolean exceptionWasThrown() {
		return exceptionWasThrown;
	}


	
	public Exception getException() {
		return exception;
	}

    public void setException(Exception e, boolean _exceptionWasThrown) {
        exception = e;
        exceptionWasThrown = _exceptionWasThrown;
    }

    public RemoteObjectRef getRor() {
		return ror;
	}

	public void setRor(RemoteObjectRef ror) {
		this.ror = ror;
	}
	
	/**
	 * Invokes the specified method on the given object, storing the
	 * result and any exceptions thrown during execution.
	 * 
	 * @param callee The object to call the method on
	 * @return true if successfully invoked, false otherwise
	 */
	public boolean invokeOnObject(Object callee) {
		//get a handle on the method to invoke
		Method m;
		try {
			if (args.length == 0) {
				m = callee.getClass().getMethod(methodName);
			}
			else {
				m = callee.getClass().getMethod(methodName, argTypes);
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			System.err.println(methodName + ": No such method");
            return false;
		}
		try {
			returnValue = m.invoke(callee, args);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			//there was an underlying exception
			exceptionWasThrown = true;
			exception = (Exception)e.getCause();
			returnValue = null;
			return true;
		} 
		
		return true;
	}

}
