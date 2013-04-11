import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author nickzukoski
 *
 * MasterController is the class to run to start the master node.
 * It spawns off a thread to listen for server input and listens 
 * for stdin input as well.
 */
public class MasterController {
	private static MasterCoordinator coord;
    
	public static void main (String args[]) {
		coord = new MasterCoordinator();
		MasterServerThread serverThread = new MasterServerThread(coord);
		serverThread.start();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		while (true) {
			//read stdin input and deal with it.
			try {
				String input = reader.readLine();
				String[] jobArgs = input.split(" ");
				
				if (jobArgs[0].equals("start")) {
					if (jobArgs.length < 4) {
						System.out.println("Format: start (jobclass) (outputfile) (inputfiles ...)");
						continue;
					}
					//Starting a new job
					String jobName = jobArgs[1];
					MapReduceJob job = (MapReduceJob) Class.forName(jobName).newInstance();
					
					job.setOutputFile(jobArgs[2]);
					List<String> inputFiles = new ArrayList<String>();
					for (int i = 3; i < jobArgs.length; i++) {
						inputFiles.add(jobArgs[i]);
					}
					
					job.setInputFiles(inputFiles);
					
					coord.newJob(job);
					System.out.println(jobName + " started!");
					continue;
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				System.out.println("Could not instantiate job class" + e.getMessage());
				continue;
			} catch (IllegalAccessException e) {
				System.out.println("Iilegal access when opening job" + e.getMessage());
				continue;
			} catch (ClassNotFoundException e) {
				System.out.println("Job class not found" + e.getMessage());
				continue;
			}
			
			
		}
    	
    }
}
