package Master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import Util.*;
import Configuration.Configuration;

public class MasterDispatchThread extends Thread {
	
	private Task t;
	private InetAddress addr;
	private MasterCoordinator coord;
	private int slaveId;
	//True if it is a map task, false if it is a reduce task
	private boolean isMap;
	private boolean isStop;
	
	public MasterDispatchThread(Task _t, int _slaveId, InetAddress _addr, 
			MasterCoordinator _coord, boolean _isMap) {
		this.t = _t;
		this.addr = _addr;
		this.coord = _coord;
		this.slaveId = _slaveId;
		this.isMap = _isMap;
		this.isStop = false;
	}
	
	public MasterDispatchThread(int _slaveId, InetAddress _addr) {
		this.isStop = true;
		this.slaveId = _slaveId;
		this.addr = _addr;
	}
	
	private void unableToConnect() {
		coord.markSlaveUnresponsive(slaveId);
	}
	
	public void run() {
		Socket s;
		try {
			s = new Socket(Configuration.SLAVE_ADDRESS[slaveId], Configuration.COM_PORT);
		} catch (IOException e) {
			System.err.println("Error opening socket to slave with id:" + slaveId);
			e.printStackTrace();
			unableToConnect();
			return;
		}
		
		NetworkMessage msg = new NetworkMessage();
		if (isStop) {
			msg.setType(NetworkMessage.STOP_PROGRAM);
		}
		else {
			msg.setTask(t);

			if (isMap)
				msg.setType(NetworkMessage.RUN_MAP);
			else
				msg.setType(NetworkMessage.RUN_REDUCE);
		}
		
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
			System.err.println("IOException writing message to slave with ID: " + slaveId);
			e.printStackTrace();
			unableToConnect();
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFound writing message to slave with ID: " + slaveId);
			e.printStackTrace();
			unableToConnect();
		}
		
		try {
			s.close();
		} catch (IOException e) {
			System.err.println("Error closing socket, continuing anyway");
			e.printStackTrace();
		}
		
	}

}
