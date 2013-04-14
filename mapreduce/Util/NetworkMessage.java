package Util;
import java.io.*;
/**
 * 
 * @author nickzukoski
 *
 * NetworkMessage packages up a message to be sent over the network
 * between the master and a slave
 */
public class NetworkMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7141810298825134457L;
	
	//Sent from master:
	public static final char RUN_MAP = 'm';
	public static final char RUN_REDUCE = 'r';
	public static final char GET_STATUS = '?';
	public static final char STOP_PROGRAM = 's';
	
	//Sent from slaves:
	public static final char TASK_FINISHED = 'f';
	public static final char NEW_JOB = 'n';
	
	//Bi-directional:
	public static final char ACK = 'a';
	
	private char type;
	private Task task;
	private MapReduceJob newJob;
	
	public char getType() {
		return type;
	}
	public void setType(char type) {
		this.type = type;
	}
	public Task getTask() {
		return task;
	}
	public void setTask(Task task) {
		this.task = task;
	}
	public MapReduceJob getNewJob() {
		return newJob;
	}
	public void setNewJob(MapReduceJob newJob) {
		this.newJob = newJob;
	}

	
}
