import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class RMIMessage implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4086249581158035244L;
	
	Object[] args;
	Class<?>[] argTypes;

	String methodName;
	
	Object returnValue;
	
	boolean exceptionWasThrown;
	Exception exception;

	public RMIMessage(String _methodName, Object[] _args) {
		this.args = _args;
		this.methodName = _methodName;
		argTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i].getClass().equals(Integer.class))
				argTypes[i] = Integer.TYPE;
			else
				argTypes[i] = args[i].getClass();
		}
	}
	
	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
		
		argTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			if (args[i].getClass().equals(Integer.class))
				argTypes[i] = Integer.TYPE;
			else
				argTypes[i] = args[i].getClass();
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

	public Exception getException() {
		return exception;
	}
	
	public boolean invokeOnObject(Object callee) {
		//get a handle on the method to invoke
		Method m;
		try {
			if (args.length == 0) {
				m = String.class.getMethod(methodName);
			}
			else {
				m = String.class.getMethod(methodName, argTypes);
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public static void main(String[] args) {
		String s = "abcda";
		Object[] _args = {2};
		RMIMessage message = new RMIMessage("charAt", _args);
		if(message.invokeOnObject(s)) {
			//it twerked
			if (message.exceptionWasThrown) {
				//but there was an exception!
				message.getException().printStackTrace();
			}
			else {
				System.out.println(message.getReturnValue());
			}
		}
		else {
			//shit didn't work yo
		}
		
	}
	
}
