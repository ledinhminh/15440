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
		String[][] outputs = new String[2][inputs[0].length];
		
		//perform the mapping
		for (int i = 0; i < inputs[0].length; i++) {
			
		}
		
	
	}
	
	
	public void newReduce(ReduceTask r) {
		
	}

}
