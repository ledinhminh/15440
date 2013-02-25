import java.io.Serializable;
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

	public RMIMessage(String _methodName, Object[] _args) {
		this.args = _args;
		this.methodName = _methodName;
		argTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
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
	
	public static void main(String[] args) {
		String s = "abcd";
		Object[] _args = {"efd"};
		RMIMessage message = new RMIMessage("concat", _args);
		
		Method m;
		try {
			if (_args.length == 0) {
				m = String.class.getMethod(message.getMethodName());

			}
			else {
				m = String.class.getMethod(message.getMethodName(), message.getArgTypes());
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
