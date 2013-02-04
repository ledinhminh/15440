//package com.cs440.lab1.classes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ProcessServer extends Thread {
	ServerSocket sSock;
	boolean serverOn;
	ProcessManager pm;
	
	public ProcessServer(int port, ProcessManager _pm) {
		serverOn = true;
		this.pm = _pm;
		try {
			sSock = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
		    System.err.println("ProcessServer: failed to create Socket");
        }
	}
	
	public void stopServer() {
		serverOn = false;
	}
	
	public void run() {
		while (serverOn) {
			try {
				Socket sock = sSock.accept();
				ServeThread t = new ServeThread(sock, sock.getInetAddress(), pm);
				t.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
			    System.err.println("ProcessServer: failed to create new thread");
            }
			
		}
	}

	
	
}
