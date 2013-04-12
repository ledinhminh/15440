package Master;

import java.net.InetAddress;
import java.net.UnknownHostException;
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
	private Map<Integer, List<Task>> jobTasks = new HashMap<Integer, List<Task>>();
	
	//Maps from jobID to the jobs status (Mapping, Reducing, Finished)
	private Map<Integer, String> jobStatuses = new HashMap<Integer, String>();
	
	//Maps from slaveID to slave address
	private Map<Integer, InetAddress> slaveIdToAddr = new HashMap<Integer, InetAddress>();
	
	//Maps from slaveID to a list of tasks that slave is currently running.
	private Map<Integer, List<Task>> slaveTasks = new HashMap<Integer, List<Task>>();
	
	private List<Task> queuedTasks = new LinkedList<Task>();
	
	public MasterCoordinator() {
		int slaveId = 0;
		for (String slave : Configuration.SLAVE_ADDRESS) {
			try {
				InetAddress addr = InetAddress.getByName(slave);
				slaveIdToAddr.put(slaveId++, addr);
			} catch (UnknownHostException e) {
				System.out.println("couldn't resolve slave named: " + slave);
				e.printStackTrace();
				continue;
			}	
		}
	}
	
	/**
	 * Called when a user has requested a new job to be started
	 * @param j The new job to start
	 */
	public void newJob(MapReduceJob j) {
		int jId = curJobId++;
		int taskId = 0;
		List<Task> mTasks = new ArrayList<Task>();
		
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
		jobTasks.put(jId, mTasks);
		queuedTasks.addAll(mTasks);
		jobStatuses.put(jId, "Mapping");
		
		//attempt to distribute tasks to the slaves
		distributeTasks();
	}
	
	public void distributeTasks() {
		Iterator<Task> itr = queuedTasks.iterator();
		while (itr.hasNext()) {
			Task t = itr.next();
			int bestHost = -1;
			//Iterate through to find a host that can accommodate another task
			for (int i : slaveTasks.keySet()) {
				int numTasks = slaveTasks.get(i).size();
				if (numTasks >= Configuration.MAX_TASKS_PER_NODE) continue;
				if (numTasks == 0) {
					bestHost = i;
					break;
				}
				if (bestHost == -1 
						|| numTasks < slaveTasks.get(bestHost).size()) 
					bestHost = i;
			}
			
			if (bestHost == -1) {
				//No host can accept any new tasks right now :(
				return;
			}
			//pop the task off the queue and send it to a slave
			itr.remove();
			dispatchTaskToSlave(t, bestHost);
		}
	}
	
	public void taskFinished(Task t) {
		List<Task> siblingTasks = jobTasks.get(t.getJobId());
		boolean allSiblingsDone = true;
		for (Task jt : siblingTasks) {
			if (jt.getTaskId() == t.getTaskId())
				jt.setStatus(MapTask.DONE);
			else if (jt.getStatus() != MapTask.DONE) {
				allSiblingsDone = false;
			}
		}
		
		//TODO Figure out if this is the last task to complete in the job so that the next
		//		round of tasks can go.
		//		Perhaps put in Maps from job ID to files that need to be reduced and update
		//		and check that each time a task is finished. then when no more files
		//		to be reduced you are done.
		
		for (List<Task> tasks : slaveTasks.values()) {
			Iterator<Task> itr = tasks.iterator();
			while (itr.hasNext()) {
				Task check_t = itr.next();
				
				if (check_t.getJobId() == t.getJobId()
						&& check_t.getTaskId() == t.getTaskId()) {
					//This is the task that finished
					itr.remove();
					return;
				}
			}
		}
		
		//distribute another task if there are tasks waiting.
		if (queuedTasks.size() > 0) distributeTasks();
	}
	
	private void dispatchTaskToSlave(Task t, int slaveId) {
		t.setStatus(MapTask.RUNNING);
		//TODO actually dispatch the task, lulz
	}

}
