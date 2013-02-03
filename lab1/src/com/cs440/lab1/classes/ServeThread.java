//package com.cs440.lab1.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.*;
public class ServeThread extends Thread {
	ObjectInputStream input;
	ObjectOutputStream output;
	InetAddress address;
	//reference to the pm of this node so it can be notified
	ProcessManager pm;
    Socket sock;
	
	public ServeThread(Socket socket, InetAddress _addr, ProcessManager _pm) {
		this.pm = _pm;
		this.address = _addr;
		
		try {
			this.input = new ObjectInputStream(socket.getInputStream());
			this.output = new ObjectOutputStream(socket.getOutputStream());
            sock = socket;
		} catch (IOException e) {
			// SHIT, THE SHIT IS BROKE AS SHIT. or somethin
			e.printStackTrace();
			System.err.println("SHIT, THE SHIT IS BROKE AS SHIT");
		}
	}
	
	public void run() {
		SlaveMessage m;
		try {
            		sock.close();
            		System.out.println("here");
			Object o = input.readObject();
            		System.out.println("readobj");
			if (o.getClass() != SlaveMessage.class) {
				//well fuck, they didn't send the right object. Fuck um we'll ignore it
				return;
			}
			m = (SlaveMessage) o;
		} catch (IOException e) {
			// TODO Auto-generated catch block
            		System.out.println("kevin bravo");
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

        if (m.firstTime() == true) {
            pm.addNewSlave(address);
            return;
        }

		if (pm.isMaster())
			pm.master_do(address, m);
		else
			pm.slave_do(m);
		
	}

}
