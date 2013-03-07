/**
 * Kevin Bravo and Nick Zukoski
 * 15440 Project 2
 * 
 */

/**
 * 
 * Remote440Exception: thrown when there is a problem calling
 * a method on a remote object
 *
 */
public class Remote440Exception extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3317395360603754250L;
	private String msg;

    public Remote440Exception (String _msg) {
        msg = _msg;
    }

    public void printErrorMessage() {
        System.err.println("Remote440Exception: " + msg);
    }

}
