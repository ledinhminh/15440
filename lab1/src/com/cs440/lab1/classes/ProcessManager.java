package com.cs440.lab1.classes;

import java.util.*;
import java.io.BufferedReader;
//import java.io.FileInputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.Serializable;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.cs440.lab1.interfaces.MigratableProcess;

class ProcessTable {
	/*holds a slave's process, and that process's time on this processor*/
	private long time = 0;
	String hostname;
}

class SlaveHost {
	/*contains a slave to run on, and a process*/
	private String hostname;
	/*probably a better way to do this...
	  hash currently used process id's so
	  we can efficiently create new processes
	*/
	private List<Integer> process_id;
	public String getHostName() {
		return hostname;
	}
}

public class ProcessManager {
	List<SlaveHost> slave_list;
	boolean master;

	ProcessManager() {	

	}

	public static void main(String[] argv) throws IOException {
		BufferedReader reader;
		
		//process argv
		if (argv.length == 2) {
			if (argv[0] != "-c") {
				System.err.println("ERROR: Bad input");
			}
			//connect to the master
		}
		else if (argv.length != 0) {
			System.err.println("ERROR: Incorrect # of arguments");
		}

		//setup the input stream reader
		reader = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			//monitor stdin
			
			String input = reader.readLine();
			String[] args = input.split(" ");
			
			//This try/catch block find the specified class and instantiate it.
			//If there are any failures, it lets the user know and goes back into
			//the input loop.
			MigratableProcess newProcess;
			try {
				Class<MigratableProcess> processClass = (Class<MigratableProcess>) Class.forName(args[0]);
				Constructor<MigratableProcess> processConstructor = processClass.getConstructor(String[].class);
				newProcess = processConstructor.newInstance((Object[])args);
				
			} catch (ClassNotFoundException e) {
				//Couldn't link find that class. stupid user.
				System.out.println("Could not find class " + args[0]);
				continue;
			} catch (SecurityException e) {
				System.out.println("Security Exception getting constructor for " + args[0]);
				continue;
			} catch (NoSuchMethodException e) {
				System.out.println("Could not find proper constructor for " + args[0]);
				continue;
			} catch (IllegalArgumentException e) {
				System.out.println("Iilegal arguments for " + args[0]);
				continue;
			} catch (InstantiationException e) {
				System.out.println("Instantiation Exception for " + args[0]);
				continue;
			} catch (IllegalAccessException e) {
				System.out.println("IIlegal access exception for " + args[0]);
				continue;
			} catch (InvocationTargetException e) {
				System.out.println("Invocation target exception for " + args[0]);
				continue;
			}
			
			
			//Send the new process out to one of the slaves and add it to the registry
			
			
			/*load balance here*/
		}
	}
}
