package Slave;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Configuration.Configuration;
import Util.MapReduceJob;
import Util.NetworkMessage;

public class SlaveDispatchThread extends Thread{
	
	private MapReduceJob job;
	
	public SlaveDispatchThread(MapReduceJob _j) {
		this.job = _j;
	}
	
	public void run() {
		Socket s;
		try {
			s = new Socket(Configuration.MASTER_ADDRESS, Configuration.COM_PORT);
		} catch (IOException e) {
			System.err.println("Error opening socket to Master");
			e.printStackTrace();
			return;
		}
		
		NetworkMessage msg = new NetworkMessage();
		msg.setNewJob(job);
		msg.setType(NetworkMessage.NEW_JOB);
		
		ObjectOutputStream oOs;
		ObjectInputStream oIs;
		try {
			oOs = new ObjectOutputStream(s.getOutputStream());
			oIs = new ObjectInputStream(s.getInputStream());
			
			//write the object out
			oOs.writeObject(msg);
			oOs.flush();
			
			//Wait for the ack
			oIs.readObject();
		} catch (IOException e) {
			System.err.println("IOException writing message to Master");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFound writing to master");
			e.printStackTrace();
		}
		
		
		try {
			s.close();
		} catch (IOException e) {
			System.err.println("Error closing socket, continuing anyway");
			e.printStackTrace();
		}
		
	}
	

}
