package Master;

import java.util.*;

import Configuration.Configuration;

import Util.FilePartition;
import Util.FileRecordReader;
import Util.MapReduceJob;
import Util.MapTask;
import Util.Task;
/**
 * 
 * @author nickzukoski
 *
 *
 * MasterCoordinator is what does the bulk of the work
 * of the master. It coordinates where and when jobs should
 * be sent and handles starting a new job once the command
 * has been given.
 */
public class MasterCoordinator {
	private int curJobId = 0;
	
	//Maps from jobID to a list of the maps for that job
	private Map<Integer, List<MapTask>> mapTasks = new HashMap<Integer, List<MapTask>>();
	
	//Maps from slaveID to a list of tasks that slave is currently running.
	private Map<Integer, List<Task>> slaveTasks = new HashMap<Integer, List<Task>>();
	
	private List<Task> queuedTasks = new LinkedList<Task>();
	
	/**
	 * Called when a user has requested a new job to be started
	 * @param j The new job to start
	 */
	public void newJob(MapReduceJob j) {
		int jId = curJobId++;
		int taskId = 0;
		List<MapTask> mTasks = new ArrayList<MapTask>();
		
		//split up the input files into partitions to be mapped
		for (String fName : j.getInputFiles()) {
			FileRecordReader reader = new FileRecordReader(fName, j.getRecordSize());
			
			int totalRecords = reader.numberOfRecords();
			
			for (int i = 0; i < totalRecords; i += Configuration.RECORDS_PER_MAP) {
				int numRecords = i + Configuration.RECORDS_PER_MAP > totalRecords ? 
									totalRecords - i : Configuration.RECORDS_PER_MAP;
				
				FilePartition fp = new FilePartition(fName, i, numRecords);
				MapTask m = new MapTask(taskId++, jId, fp, j, "temp/job_"+jId+"_maptask_"+taskId);
				mTasks.add(m);
			}
		}
		
		//add all of the tasks to the queue
		mapTasks.put(jId, mTasks);
		queuedTasks.addAll(mTasks);
		
		//attempt to distribute tasks to the slaves
		distributeTasks();
	}
	
	public void distributeTasks() {}

}
