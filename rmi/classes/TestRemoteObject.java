
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

    public String concat3(String s1, String s2, String s3) {
		return s.concat(s1).concat(s2).concat(s3);
	}
	
	public String concat4(String s1, String s2, String s3, String s4) {
		return s.concat(s1).concat(s2).concat(s3).concat(s4);
	}
	

	public void throwException() {
		throw new IndexOutOfBoundsException();
	}
}
