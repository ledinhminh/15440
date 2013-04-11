import java.io.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class SlaveHost {

  private int slaveId;
  private InetAddress mAddr;
  private ServerSocket slaveSock;

  public SlaveHost (String masterName) throws UnknownHostException, IOException {
    
    mAddr     = InetAddress.getByName(masterName);

    slaveSock = new ServerSocket();
  
  }


  /**runSlave()
   * opens the slave server for incoming commands/status requests
   * from the master
   */
  public void runSlave() {
    
    Socket sock;

    
    while (true) {
      try {
        sock = slaveSock.accept();
      } catch (IOException e) {
        System.err.println("runSlave: IOException creating socket");
        continue;
      }

      if (sock != null) {
        Thread t = new Thread(new SlaveConnection(sock));
        t.start();
      }
      
      //TODO poll the master now and then so we don't burn up CPU time
      //if we're orphans

    }


  }
  
}

  
