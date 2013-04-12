package Util;

import java.io.Serializable;
import java.util.List;

public class ReduceTask implements Serializable, Task {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8552240454021458949L;
	
	private int slaveId;
	private int taskId;
	private int jobId;
	private List<String> inputFiles;
	private MapReduceJob job;
	private String outputFile;
	private char status;
	
	public static final char RUNNING = 'r';
	public static final char DONE = 'd';
	public static final char FAILED = 'f';
	public static final char NOT_STARTED = 'n';
	
	public ReduceTask(int _taskId, int _jobId, List<String> _inputFiles, MapReduceJob j,
			String _outputFile) {
		this.taskId = _taskId;
		this.jobId = _jobId;
		this.inputFiles = _inputFiles;
		this.job = j;
		this.outputFile = _outputFile;
		this.status = NOT_STARTED;
	}
	
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public List<String> getInputFiles() {
		return inputFiles;
	}
	public void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}
	public MapReduceJob getJob() {
		return job;
	}
	public void setJob(MapReduceJob job) {
		this.job = job;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	
	public int getSlaveId() {
		return slaveId;
	}

	public void setSlaveId(int slaveId) {
		this.slaveId = slaveId;
	}


}
