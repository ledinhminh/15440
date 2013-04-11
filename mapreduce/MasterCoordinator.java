import java.util.*;
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
	//Maps from jobID to a list of the maps for that job
	private Map<Integer, List<MapTask>> mapTasks;
	
	//Maps from slaveID to a list of tasks that slave is currently running.
	private Map<Integer, List<Task>> slaveTasks;
	
	
	
	/**
	 * Called when a user has requested a new job to be started
	 * @param j The new job to start
	 */
	public void newJob(MapReduceJob j) {
		
	}

}
