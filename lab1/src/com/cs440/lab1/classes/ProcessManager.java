import java.util.*


class SlaveHost {
	/*contains a slave to run on, and a process*/
	private String hostname;
	/*probably a better way to do this...
	  hash currently used process id's so
	  we can efficiently create new processes
	*/
	private List<int> process_id;
	public String getHostName() {
		return hostname;
	}
}

public static class ProcessManager {
	List<SlaveHost> slave_list;
	boolean master;

	ProcessManager() {
		

	}

        void process_input(byte[]
	public static void main() {
		int b;
		byte[] input;
		int length  = 0;
		long offset = 0;
		while(1) {
			b = System.in.read();
			if (b != -1) {
				length = offset;
				process_command(input, length);
				offset = 0;
			}
			/*load balance here*/
		}
	}
}
