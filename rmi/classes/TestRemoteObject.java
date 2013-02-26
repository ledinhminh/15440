
public class TestRemoteObject {
	
	private String s;

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}
	
	public String concatS(String s2) {
		return s.concat(s2);
	}

	public String concat2(String s1, String s2) {
		return s.concat(s1).concat(s2);
	}
	
	public void throwException() {
		throw new IndexOutOfBoundsException();
	}
}
