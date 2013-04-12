package Master;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import Configuration.Configuration;

import Util.*;
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

	private Map<Integer, MapReduceJob> idToJob = new HashMap<Integer, MapReduceJob>();
	
	//Maps from jobID to a list of the maps for that job
	private Map<Integer, List<Task>> jobTasks = new HashMap<Integer, List<Task>>();

	//Maps from jobID to the jobs status (Mapping, Reducing, Finished)
	private Map<Integer, String> jobStatuses = new HashMap<Integer, String>();
	
	private Map<Integer, List<String>> filesToProcess = new HashMap<Integer,List<String>>();

	//Maps from slaveID to slave address
	private Map<Integer, InetAddress> slaveIdToAddr = new HashMap<Integer, InetAddress>();

	//Maps from slaveID to a list of tasks that slave is currently running.
	private Map<Integer, List<Task>> slaveTasks = new HashMap<Integer, List<Task>>();

	private List<Task> queuedTasks = new LinkedList<Task>();
	
	//List of unresponsive slaves
	private List<Integer> unresponsiveSlaves = new LinkedList<Integer>();

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
		synchronized (jobTasks) {
			jobTasks.put(jId, mTasks);
		}
		synchronized (queuedTasks) {
			queuedTasks.addAll(mTasks);
		}
		synchronized (jobStatuses) {
			jobStatuses.put(jId, "Mapping");
		}
		synchronized (filesToProcess) {
			filesToProcess.put(jId, new LinkedList<String>());
		}
		synchronized(idToJob) {
			idToJob.put(jId, j);
		}

		//attempt to distribute tasks to the slaves
		distributeTasks();
	}
	
	/**
	 * This is called when a job has a round of tasks finished and the next
	 * round needs to be queued up.
	 * @param jId The id of the job
	 */
	private void tasksCompleted(int jId) {
		List<String> newInputFiles;
		synchronized(filesToProcess) {
			newInputFiles = filesToProcess.get(jId);
		}
		
		MapReduceJob j = null;
		synchronized(idToJob) {
			idToJob.get(jId);
		}
		
		//Need to create a series of reduce tasks to send out.
		
		List<ReduceTask> newTasks = new LinkedList<ReduceTask>();
		List<String> curTaskFiles = new LinkedList<String>();
		int currentSize = 0;
		for (String s : newInputFiles) {
			FileRecordReader r = new FileRecordReader(s, j.getRecordSize());
			curTaskFiles.add(s);
			
			currentSize += r.numberOfRecords();
			if (currentSize >= Configuration.RECORDS_PER_REDUCE) {
				//TODO create a new ReduceTask and add it to the list
				//ReduceTask t = new ReduceTask()
			}
		}
		
	}

	public void distributeTasks() {
		synchronized (queuedTasks) {
			Iterator<Task> itr = queuedTasks.iterator();
			while (itr.hasNext()) {
				Task t = itr.next();
				int bestHost = -1;
				//Iterate through to find a host that can accommodate another task
				synchronized (slaveTasks) {
					for (int i : slaveTasks.keySet()) {
						if (unresponsiveSlaves.contains(i))
							continue; //If the slave is unresponsive then just keep going
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
				}

				if (bestHost == -1) {
					//No host can accept any new tasks right now :(
					return;
				}
				//pop the task off the queue and send it to a slave
				itr.remove();
				t.setSlaveId(bestHost);
				dispatchTaskToSlave(t, bestHost);
			}
		}
	}

	public void taskFinished(Task t) {
		synchronized(unresponsiveSlaves) {
			if (unresponsiveSlaves.contains(t.getSlaveId())) {
				//a presumed unresponsive slave has come back up,
				//ignore the work but mark the slave as useful.
				unresponsiveSlaves.remove((Integer)t.getSlaveId());
				return;
			}
		}
		
		int jId = t.getJobId();
		
		//add the output to the files that need to be processed.
		synchronized(filesToProcess) {
			filesToProcess.get(jId).add(t.getOutputFile());
		}
		
		List<Task> siblingTasks;
		boolean allSiblingsDone = true;
		
		synchronized (jobTasks) {
			siblingTasks = jobTasks.get(jId);
		}
		
		
		for (Task jt : siblingTasks) {
			if (jt.getTaskId() == t.getTaskId())
				jt.setStatus(MapTask.DONE);
			else if (jt.getStatus() != MapTask.DONE) {
				allSiblingsDone = false;
			}
		}
		
		if (allSiblingsDone) {
			if (jobStatuses.get(jId).equals("Mapping")) {
				jobStatuses.put(jId, "Reducing");
			}
			
			tasksCompleted(jId);
		}
		//TODO Figure out if this is the last task to complete in the job so that the next
		//		round of tasks can go.
		//		Perhaps put in Maps from job ID to files that need to be reduced and update
		//		and check that each time a task is finished. then when no more files
		//		to be reduced you are done.
		
		synchronized (slaveTasks) {
			List<Task> tasks = slaveTasks.get(t.getSlaveId());
			Iterator<Task> itr = tasks.iterator();
			while (itr.hasNext()) {
				Task check_t = itr.next();

				if (check_t.getJobId() == jId
						&& check_t.getTaskId() == t.getTaskId()) {
					//This is the task that just finished
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
		boolean isMap = t instanceof MapTask ? true : false;
		MasterDispatchThread dispatchThread = new MasterDispatchThread(t, slaveId,slaveIdToAddr.get(slaveId), 
													this, isMap);
		dispatchThread.start();
	}

	/**
	 * Called when it appears that a slave has failed,
	 * Mark all of the tasks that were on that slave as failed and move
	 * them back into the queue to be placed elsewhere.
	 * 
	 * @param slaveId The slave Id that is now unresponsive
	 */
	public void markSlaveUnresponsive(int slaveId) {
		synchronized(unresponsiveSlaves) {
			unresponsiveSlaves.add(slaveId);
		}
		
		List<Task> deadTasks;
		synchronized(slaveTasks) {
			deadTasks = slaveTasks.get(slaveId);
		}
		for (Task t : deadTasks) {
			t.setStatus(MapTask.FAILED);
			synchronized (queuedTasks) {
				queuedTasks.add(t);
			}
		}
		synchronized(slaveTasks) {
			slaveTasks.put(slaveId, new LinkedList<Task>());
		}
	}
	
	public String allJobsInfo() {
		String retStr = "*******All jobs info*******\n";
		Set<Integer> jobs;
		synchronized(jobTasks) {
			jobs = jobTasks.keySet();
		}
		
		for (Integer jId : jobs) {
			retStr = retStr + jobInfo(jId);
		}
		
		return retStr;
	}
	
	public String jobInfo(int jId) {
		MapReduceJob j;
		List<Task> tasks;
		
		synchronized(jobTasks) {
			tasks = jobTasks.get(jId);
		}
		synchronized(idToJob) {
			j = idToJob.get(jId);
		}
		
		String retStr = "Job " + jId + "(" + j.getClass().getCanonicalName()+ ")\n";
		retStr += "Number of tasks: " +tasks.size() + "\n";
		for (int i = 0; i < tasks.size(); i++) {
			Task t = tasks.get(i);
			if (t instanceof MapTask) {
				retStr += "     MAP tId:" + t.getTaskId()+ " status"+t.getStatus();
				FilePartition fp = ((MapTask)t).getPartition();
				retStr += " partition:" + fp.getFileName() + "[" + fp.getPartitionIndex() + "," + fp.getPartitionSize() + "]";
				retStr += " outputFile:" + ((MapTask) t).getOutputFile() + "\n";
			}
			else {
				retStr += "     REDUCE tId:" + t.getTaskId()+ " status"+t.getStatus();
				List<String> inputs= ((ReduceTask) t).getInputFiles();
				retStr += " inputs:";
				for (String s : inputs) {
					retStr += s + " ";
				}
				retStr += " outputFile:" + ((ReduceTask) t).getOutputFile() + "\n";
			}
		}
		retStr += "";
		
		return retStr;
	}

}
