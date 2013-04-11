
public class Configuration {
	//The size of an individual record (in bytes)
	public static final int RECORD_SIZE = 20;
	
	//The port that network communication takes place on
	public static final int COM_PORT = 15443;
	
	//How long to wait on sockets before timing out (in ms)
	public static final int SOCKET_TIMEOUT = 200;
	
	//The number of records to allocate to a single map
	public static final int RECORDS_PER_MAP = 50;
}
