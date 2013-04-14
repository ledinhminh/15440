package Slave;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Configuration.Configuration;


public class SlaveServerThread extends Thread {
	private SlaveCoordinator coord;
	private volatile boolean running;
	private ServerSocket sSock;
	
	public SlaveServerThread(SlaveCoordinator _coord) {
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
		System.out.println("stopping server");
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
				continue;
			}
			
			SlaveCommThread t = new SlaveCommThread(coord, s);
			t.start();	
		}
	}
	
}
