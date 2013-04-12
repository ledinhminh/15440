package Slave;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import Util.*;
import Configuration.*;

public class SlaveCoordinator {
	
	public void newMap(MapTask m) {
		//TODO make a record of the task so that if its asked about we have
		// something to say
		MapReduceJob job = m.getJob();
		FilePartition partition = m.getPartition();
		FileRecordReader reader = new FileRecordReader(partition.getFileName(), job.getRecordSize());
		String[][] inputs = reader.getKeyValuePairs(partition.getPartitionIndex(), partition.getPartitionSize());
		List<String[]> outputs = new LinkedList<String[]>();
		
		//perform the mapping
		for (int i = 0; i < inputs[0].length; i++) {
			List<String[]> temp = job.map(inputs[i][0], inputs[i][1]);
			outputs.addAll(temp);
		}
		
		FileRecordWriter writer = new FileRecordWriter(m.getOutputFile(), job.getRecordSize());
		writer.writeOut((String[][])outputs.toArray());
		
		//TODO mark the task as done.
		notifyMasterTaskComplete(m);
	}
	
	
	public void newReduce(ReduceTask r) {
		
		MapReduceJob job = r.getJob();
		List<String> inputFiles = r.getInputFiles();
		List<String[][]> inputList = new LinkedList<String[][]>();
		int totalRecords = 0;
		for (String s : inputFiles) {
			FileRecordReader reader = new FileRecordReader(s, job.getRecordSize());
			int numRecords = reader.numberOfRecords();
			String[][] kvs = reader.getKeyValuePairs(0, numRecords);
			
			totalRecords += kvs.length;
			
			inputList.add(kvs);
		}
		
		String[][] inputs = new String[totalRecords][2];
		int curIdx = 0;
		for (String[][] kv : inputList) {
			for (int i = 0; i < kv.length; i++) {
				inputs[i+curIdx][0] = kv[i][0];
				inputs[i+curIdx][1] = kv[i][1];
			}
			curIdx += kv.length;
		}
		
		//Sort the inputs based on key
		Arrays.sort(inputs, new Comparator<String[]>(){
			public int compare(String[] a, String[] b){
				return a[0].compareTo(b[0]);
			}
		});
		
		
		List<String[]> outputs = new LinkedList<String[]>();
		String curKey = inputs[0][0];
		List<String> sameKeyInputs = new LinkedList<String>();
		
		//DO THE REDUCING!
		for (int i = 0; i < inputs.length; i++) {
			if (! curKey.equals(inputs[i][0])) {
				//its a new key so run the reduce with all of the values up till now
				String result = job.reduce(curKey, sameKeyInputs, job.getReduceIdentity());
				outputs.add(new String[] {curKey, result});
				
				sameKeyInputs.clear();
				sameKeyInputs.add(inputs[i][1]);
				curKey = inputs[i][0];
			} else {
				sameKeyInputs.add(inputs[i][1]);
			}
		}
		
		FileRecordWriter writer = new FileRecordWriter(r.getOutputFile(), job.getRecordSize());
		writer.writeOut((String[][])outputs.toArray());
		
		notifyMasterTaskComplete(r);
	}
	
	public void notifyMasterTaskComplete(Task t) {
		t.setStatus(MapTask.DONE);
		NetworkMessage msg = new NetworkMessage();
		msg.setTask(t);
		msg.setType(NetworkMessage.TASK_FINISHED);
		
		//now send the message to the master
		try {
			Socket s = new Socket(Configuration.MASTER_ADDRESS, Configuration.COM_PORT);
			ObjectOutputStream oOs = new ObjectOutputStream(s.getOutputStream());
			ObjectInputStream oIs = new ObjectInputStream(s.getInputStream());
			
			oOs.writeObject(msg);
			oOs.flush();
			//wait for the ack
			oIs.readObject();
			s.close();
		} catch (UnknownHostException e) {
			System.err.println("Unknown host trying to connect to master");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("IOException trying to send to master");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println("class not found exception recieving ack from master");
			e.printStackTrace();
		}
		
	}

}
