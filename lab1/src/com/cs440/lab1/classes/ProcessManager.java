package my440package;

import java.util.*;
import java.nio.channels.*;
import java.io.BufferedReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
//import java.io.FileInputStream;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.Serializable;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.*;


/** SlaveHost contains methods for load balancing, most importantly
 *  a list of processes that its host currently holds
 *  the process list is FIFO, so processes that have been on this machine
 *  the longest will be packaged and mailed somewhere
 *  @param _sock  The SocketAddress which the master will use to communicate
 *	          with the slave
*/
class SlaveHost {
	private SocketAddress slave_sock;
	private int process_count;
	private List<Integer> process_list;

	SlaveHost(SocketAddress _sock) {
		slave_sock    = _sock;
		process_count = 0;
	}

	public SocketAddress getSocket() {
		return slave_sock;
	}
	public Integer popProcess() throws Exception {
		if (processId.size() == 0) {
			System.err.println("popProcess: No Processes Remain.  Can't pop");
			throw new Exception();
		}
		return processId.remove(0);
	}
	public void pushProcess(int _pid, Socket m_sock) throws Exception {
		process_list.add(_pid);
		m_sock 
}

public class ProcessManager {
	//The port for all the servers to run on
	private static final int MASTER_PORT   = 15440;
	private static final int SLAVE_PORT    = 15440;
	private static final String OBJECT_DIR = "/afs/ blah blah kbravo / 440";
	private int currentProcessId           = 0;

	private ServerSocket master_sock;
	
	//list of slaves that are communicating with the master
	private List<SlaveHost> slave_list;

	//mapping from processID to slave it is on.
	private Map<Integer, SlaveHost> processList;
	private boolean master;

	//private ProcessServer server;

	public ProcessManager(boolean _isMaster, String masterUrl) {	
		this.master = _isMaster;
		//server = new ProcessServer(port, this);
	}
	
	/**
	 * printProcesses()
	 * 
	 * Print to std out the running processes and their arguments
	 */
	public void printProcesses() {
		return;
	}
	
	/**
	 * sendProcessToSlave: opens a connection to the slave with id slaveId
	 * and sends it a message to start running process processId
	 * 
	 * @param slaveId The id of the slave to send to
	 * @param processId The id of the process to send
	 */
	private void sendProcessToSlave(int slaveId, int processId) {
		receiver_slave = slave_list[slaveId];
			
		return;
	}
	
	/**
	 * Reads and deserializes the process with ID _id. 
	 * 
	 * @param _id The process ID to read in from disk
	 * @return The deserialized MigratableProccess with id _id
	 */
	private MigratableProcess readProcess(int _id) {
		String fileName = "processes/process_" + _id;
		MigratableProcess p;

		TransactionalFileInputStream fileStream = new TransactionalFileInputStream(fileName);

		try {
			ObjectInputStream objectStream = new ObjectInputStream(fileStream);
			p = (MigratableProcess) objectStream.readObject();
			objectStream.close();
			fileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				fileStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return null;
		}
		return p;
	}
	
	/**
	 * Suspends, serializes and writes to disk the given process
	 * 
	 * @param p The migratableProcess to store
	 * @param _id The ID of this migratable process
	 */
	private void writeProcess(MigratableProcess p, int _id) {
		//suspend the process so it can be serialized
		p.suspend();
		
		TransactionalFileOutputStream fileStream = new TransactionalFileOutputStream("processes/process_"+_id+".ser");
		ObjectOutputStream objectStream;
		
		try {
			objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(p);
			objectStream.close();
			fileStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts a new slave host by opening up a socket and listening
	 * to stuff...sends a server socketaddress to the master so it
	 * knows where to send stuff
	 */
	public void startSlave() {
	}
	
	/** 
	 * Starts a new master host by opening up a master socket
	 * and monitoring stdin for new commands
	 */
	public void startMaster() throws IOException {
		//setup the input stream reader
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		
		//open the master socket to communicate with slaves
		try {
			master_sock           = new ServerSocket(PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}

		//master_sock.configureBlocking(false);
		
		while(true) {
			//monitor stdin for new commands
			String input;
			String[] args;
			Socket slave_sock;

			//accept() creates a new socket for listening to the slave.  all we really 
			//need is the remote SocketAddress
			if ((slave_sock = master_sock.accept()) != null) {
				//create a new slave host and store the remote serversocket address
				//the remote server's socketaddress is communicated through an object stream
				ObjectInputStream slave_ostream = ObjectInputStream(slave_sock.getInputStream());
				SocketAddress server_sockaddr   = (SocketAddress)slave_ostream.readobject();
				SlaveHost slave_host = new SlaveHost(server_sockaddr);
				System.out.println("Connected to " + slave_sock.toString());
				
				//add it to our current list of slaves
				slave_list.add(slave_host);
				//now we'll act assuming that the slave's serversocket 
				//is waiting on the master's command
			}
			
			try {
				input = reader.readLine();
				args = input.split(" ");
				if (args.length == 0)
					continue;
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			
			if (args[0].equals("ps")) {
				printProcesses();
				continue;
			}
			else if (args[0].equals("quit")) {
				//TODO quit
				break;
			}
			
			//This try/catch block find the specified class and instantiate it.
			//If there are any failures, it lets the user know and goes back into
			//the input loop.
			MigratableProcess newProcess;
			try {
				Class<MigratableProcess> processClass = (Class<MigratableProcess>)(Class.forName(args[0]));
				
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
				System.out.println("Illegal arguments for " + args[0]);
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

			//select a slave to send the process to
			int slaveId = currentProcessId % slave_list.size();
			sendProcessToSlave(slaveId, currentProcessId);
			
			currentProcessId++;
		}
	}

	public static void main(String[] argv) throws IOException {
		ProcessManager pm;
		//process argv
		if (argv.length == 2) {
			if (argv[0] != "-c") {
				System.err.println("ERROR: Bad input");
				return;
			}
			//setup as slave
			pm = new ProcessManager(false, argv[1]);
			pm.startSlave();
		}
		else if (argv.length != 0) {
			System.err.println("ERROR: Incorrect # of arguments");
			return;
		}
		else {
			//setup as master PM
			pm = new ProcessManager(true, null);
			pm.startMaster();
		}
		
	}
}
