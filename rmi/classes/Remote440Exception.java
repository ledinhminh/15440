
public class Remote440Exception extends Exception {

    private String msg;

    public Remote440Exception (String _msg) {
        msg = _msg;
    }

    public void printErrorMessage() {
        System.err.println("Remote440Exception: " + msg);
    }

}
