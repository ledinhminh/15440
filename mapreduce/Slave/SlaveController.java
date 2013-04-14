package Slave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Util.MapReduceJob;

public class SlaveController {
	private static SlaveCoordinator coord;
	private static SlaveServerThread serverThread;
	private static BufferedReader reader;
	private static boolean running = true;
	
	public static void stopProgram() {
		running = false;
		try {
			reader.close();
		} catch (Exception e) {
			//meh, were shutting down anyway
		}
		serverThread.stopThread();
		System.exit(0);
	}

	public static void main (String args[]) {
		coord = new SlaveCoordinator();
		serverThread = new SlaveServerThread(coord);
		serverThread.start();

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		while (running) {
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
					
					SlaveDispatchThread t = new SlaveDispatchThread(job);
					t.start();
					System.out.println(jobName + " started!");
					continue;
				} else if (input.equals("list")) {
					System.out.println(coord.getInfo());
				}
				else if (input.equals("quit")) {
					SlaveController.stopProgram();
				}
			} catch (IOException e) {
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
