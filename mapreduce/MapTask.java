import java.io.Serializable;

/**
 * 
 * @author nickzukoski
 *
 * MapTask holds all the info needed to execute a map task
 */
public class MapTask implements Serializable, Task{
	private int taskId;
	private int jobId;
	private FilePartition partition;
	private MapReduceJob job;
	private String outputFile;
	private char status;
	
	public static final char RUNNING = 'r';
	public static final char DONE = 'd';
	public static final char NOT_STARTED = 'n';
	
	public MapTask(int _taskId, int _jobId, FilePartition _p, MapReduceJob j,
			String _outputFile) {
		this.taskId = _taskId;
		this.jobId = _jobId;
		this.partition = _p;
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
	public FilePartition getPartition() {
		return partition;
	}
	public void setPartition(FilePartition partition) {
		this.partition = partition;
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
	
}
