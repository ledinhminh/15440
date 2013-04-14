package Slave;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Util.*;

public class SlaveCommThread extends Thread {
	private SlaveCoordinator coord;
	private Socket s;

	public SlaveCommThread(SlaveCoordinator _coord, Socket _s) {
		this.coord = _coord;
		this.s = _s;
	}

	public void run() {
		ObjectInputStream oIs;
		ObjectOutputStream oOs;

		try {
			oOs = new ObjectOutputStream(s.getOutputStream());
			oIs = new ObjectInputStream(s.getInputStream());
		} catch (Exception e) {
			System.err.println("error getting I/O streams from client.");
			try {
				s.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return;
		}

		NetworkMessage msg;

		try {
			msg = (NetworkMessage) oIs.readObject();
		} catch (Exception e) {
			System.err.println("Error receiving msg");
			try {
				s.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return;
		}

		//TODO if the message doesn't require an immediete response, reply with
		// 		an ack before starting the task.
		switch (msg.getType()) {
		case NetworkMessage.STOP_PROGRAM:
			SlaveController.stopProgram();
			break;
		default:
			break;
		}

		//Respond with an ACK if nothing else is going to be sent
		msg.setType(NetworkMessage.ACK);
		try {
			oOs.writeObject(msg);
			s.close();
		} catch (IOException e) {
			System.err.println("Error ACKing a new Job message");
			e.printStackTrace();
		}
		
		//Case statements for non immediate responses.
		switch (msg.getType()) {
		case NetworkMessage.RUN_MAP:
			System.out.println("New map task received");
			coord.newMap((MapTask)msg.getTask());
			break;
		case NetworkMessage.RUN_REDUCE:
			System.out.println("New reduce task received");
			coord.newReduce((ReduceTask)msg.getTask());
			break;
		}
		
		return;
	}

}
