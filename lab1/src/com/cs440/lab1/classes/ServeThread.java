package com.cs440.lab1.classes;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class ServeThread extends Thread {
	ObjectInputStream input;
	ObjectOutputStream output;
	//reference to the pm of this node so it can be notified
	ProcessManager pm;
	
	public ServeThread(InputStream in, OutputStream out, ProcessManager _pm) {
		this.pm = _pm;
		
		try {
			this.input = new ObjectInputStream(in);
			this.output = new ObjectOutputStream(out);
		} catch (IOException e) {
			// SHIT, THE SHIT IS BROKE AS SHIT. or somethin
			e.printStackTrace();
		}
	}
	
	public void run() {
		ServerCommand c;
		try {
			Object o = input.readObject();
			if (o.getClass() != ServerCommand.class) {
				//well fuck, they didn't send the right object. Fuck um we'll ignore it
				return;
			}
			c = (ServerCommand) o;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		if (c.getType().equals("RUN_PROCESS")) {
			//need to deserialize the process from disk and start chugging with it.
		}
		else if (c.getType().equals("STOP_PROCESS")) {
			//need to stop the given process and serialize it to disk
		}
		
		
		
	}

}
