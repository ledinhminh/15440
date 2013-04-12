package Master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
				MapTask m = new MapTask(taskId++, jId, fp, j, "temp/job_"+jId+"_task_"+taskId);
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
	private void taskRoundCompleted(int jId) {
		List<String> newInputFiles;
		synchronized(filesToProcess) {
			newInputFiles = filesToProcess.get(jId);
		}
		

		MapReduceJob j = null;
		synchronized(idToJob) {
			j = idToJob.get(jId);
		}

		String status;
		synchronized(jobStatuses) {
			status = jobStatuses.get(jId);
		}
		if (newInputFiles.size() == 1 &&  status.endsWith("Reducing")) {
			//move the final reduced file to the desired output file
			copyFileToOutput(newInputFiles.get(0), j.getOutputFile());
			//clean up the temporary files
			cleanupTempFiles(jId);
			synchronized(jobStatuses) {
				jobStatuses.put(jId, "Done");
			}
		}
		
		//Need to create a series of reduce tasks to send out.
		int curTaskId;
		synchronized(jobTasks) {
			curTaskId = jobTasks.get(jId).size();
		}

		List<ReduceTask> newTasks = new LinkedList<ReduceTask>();
		List<String> curTaskFiles = new LinkedList<String>();
		int currentSize = 0;
		for (String s : newInputFiles) {
			FileRecordReader r = new FileRecordReader(s, j.getRecordSize());
			curTaskFiles.add(s);

			currentSize += r.numberOfRecords();

			if (currentSize >= Configuration.RECORDS_PER_REDUCE) {
				String outF = "temp/job_"+jId+"_task_"+curTaskId++;
				ReduceTask t = new ReduceTask(curTaskId, jId, curTaskFiles, j, outF);
				newTasks.add(t);
				currentSize = 0;
				curTaskFiles = new LinkedList<String>();
			}
		}

		synchronized(jobTasks) {
			jobTasks.get(jId).addAll(newTasks);
		}
		synchronized(queuedTasks) {
			queuedTasks.addAll(newTasks);
		}
		synchronized(filesToProcess) {
			filesToProcess.put(jId, new LinkedList<String>());
		}

	}

	private void cleanupTempFiles(int jId) {
		int numTasks;
		synchronized(jobTasks) {
			numTasks = jobTasks.get(jId).size();	
		}
		
		for (int i = 0; i < numTasks; i++) {
			File f = new File("temp/job_"+jId+"_task_"+i);
			if (!f.delete()) {
				System.err.println("Could not delete temp file: " + "temp/job_"+jId+"_task_"+i);
			}
		}
	}

	private void copyFileToOutput(String inputFile, String outputFile) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(inputFile);
			out = new FileOutputStream(outputFile);
			
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
			   out.write(buf, 0, len);
			}
		} catch (FileNotFoundException e) {
			System.err.println("Error finding reduce output file");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error copying reduce output to named output");
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				System.err.println("Error closing files when copying from reduce output to final output");
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Called to attempt to distribute tasks in the queuedTasks queue to
	 * slave nodes to be processed.
	 */
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

	/**
	 * Called when a notification from a slave that a task
	 * has finished running comes in
	 * @param t The task that has finished
	 */
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
			taskRoundCompleted(jId);
			
			if (jobStatuses.get(jId).equals("Mapping")) {
				jobStatuses.put(jId, "Reducing");
			}

			
		}

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

	/**
	 * Sends a task to a slave to be executed.
	 * 
	 * @param t The task to send
	 * @param slaveId The slave to send it to
	 */
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

	/**
	 * 
	 * @return Returns a string describing all of the jobs
	 */
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

	/**
	 * 
	 * @param jId The job for a summary
	 * @return Returns a summary of all of the info about a job.
	 */
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
