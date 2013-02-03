//package com.cs440.lab1.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;

public class ServeThread extends Thread {
	ObjectInputStream input;
	ObjectOutputStream output;
	InetAddress address;
	//reference to the pm of this node so it can be notified
	ProcessManager pm;
	
	public ServeThread(InputStream in, OutputStream out, InetAddress _addr, ProcessManager _pm) {
		this.pm = _pm;
		this.address = _addr;
		
		try {
			this.input = new ObjectInputStream(in);
			this.output = new ObjectOutputStream(out);
		} catch (IOException e) {
			// SHIT, THE SHIT IS BROKE AS SHIT. or somethin
			e.printStackTrace();
		}
	}
	
	public void run() {
		SlaveMessage m;
		try {
			Object o = input.readObject();
			if (o.getClass() != SlaveMessage.class) {
				//well fuck, they didn't send the right object. Fuck um we'll ignore it
				return;
			}
			m = (SlaveMessage) o;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		if (pm.isMaster())
			pm.master_do(address, m);
		else
			pm.slave_do(m);
		
	}

}
