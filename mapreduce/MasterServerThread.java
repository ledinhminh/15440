import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * @author nickzukoski
 *
 * MasterServerThread is the thread that listens for network communication in
 * and deals with it appropriately.
 */
public class MasterServerThread extends Thread {
	private MasterCoordinator coord;
	//true if the thread should keep running, false for it to stop
	private volatile boolean running;
	ServerSocket sSock;
	
	public MasterServerThread(MasterCoordinator _coord) {
		this.running = true;
		this.coord = _coord;
		try {
			this.sSock = new ServerSocket(Configuration.COM_PORT);
			this.sSock.setSoTimeout(Configuration.SOCKET_TIMEOUT);
		} catch (IOException e) {
			System.err.println("Unable to open Server Socket on master server thread");;
			e.printStackTrace();
		}
	}
	
	/**
	 * call stopThread() to stop the execution of the thread,
	 * closes the socket and ends the infinite read loop.
	 */
	public void stopThread() {
		running = false;
		try {
			sSock.close();
		} catch (IOException e) {
			System.err.println("Error closing socket in stopThread");
			e.printStackTrace();
		}
	}
	
	public void run() {
		while (running) {
			Socket s;
			try {
				s = sSock.accept();
			} catch (Exception e) {
				System.err.println("Exception waiting for client connection " + e.getMessage());
				continue;
			}
			
			
			
		}
	}

}

	
