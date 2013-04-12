package Configuration;

public class Configuration {
	//The size of an individual record (in bytes)
	public static final int RECORD_SIZE = 20;
	
	//The port that network communication takes place on
	public static final int COM_PORT = 15443;
	
	//How long to wait on sockets before timing out (in ms)
	public static final int SOCKET_TIMEOUT = 200;
	
	//The number of records to allocate to a single map
	public static final int RECORDS_PER_MAP = 50;
	
	//The number of records to allocate to a single reduce
	//(Not a hard limit as reduces reduce full files at a time)
	public static final int RECORDS_PER_REDUCE = 100;
	
	//The max number of tasks to be running on one slave at a time
	public static final int MAX_TASKS_PER_NODE = 3;
	
	public static final String[] SLAVE_ADDRESS = 	{"unix12.andrew.cmu.edu", 
													 "unix13.andrew.cmu.edu",
													 "unix14.andrew.cmu.edu",
													 "unix1.andrew.cmu.edu"};
	
	public static final String MASTER_ADDRESS = "unix16.andrew.cmu.edu";
	
}
