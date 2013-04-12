package Slave;

import Util.*;

public class SlaveCoordinator {
	
	public void newMap(MapTask m) {
		//TODO make a record of the task so that if its asked about we have
		// something to say
		MapReduceJob job = m.getJob();
		FilePartition partition = m.getPartition();
		FileRecordReader reader = new FileRecordReader(partition.getFileName(), job.getRecordSize());
		String[][] inputs = reader.getKeyValuePairs(partition.getPartitionIndex(), partition.getPartitionSize());
		String[][] outputs = new String[inputs.length][2];
		
		//perform the mapping
		for (int i = 0; i < inputs[0].length; i++) {
			outputs[i] = job.map(inputs[i][0], inputs[i][1]);
		}
		
		FileRecordWriter writer = new FileRecordWriter(m.getOutputFile(), job.getRecordSize());
		writer.writeOut(outputs);
		
		//TODO mark the task as done.
		//TODO notify the master that the work is done.
	}
	
	
	public void newReduce(ReduceTask r) {
		
	}

}
