import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class SlaveConnection implements Runnable {

  private Socket masterSock;
  private LinkedList<FilePartition> partitions;
  private static final char STATUS_REQ = '?';
  private static final char STATUS_RSP = '!';
  private static final char COMMAND    = 'c';
  private static final char ACK        = 'a';
  private static final char MAP        = 'm';
  private static final char REDUCE     = 'r';

  public boolean threwException = false;

  public SlaveConnection (Socket _masterSock) {
    masterSock = _masterSock;
  }



  private void mapPartition (MasterSlaveMessage msg) {

  }

  public void run() {

    OutputStream os; InputStream is;
    ObjectOutputStream oOs; ObjectInputStream oIs;

    try {
      os  = masterSock.getOutputStream();
      is  = masterSock.getInputStream();
      oOs = new ObjectOutputStream(os);
      oIs = new ObjectInputStream(is);
    } catch (IOException e) {
      threwException = true;
      return;
    }

    MasterSlaveMessage msg = null;
    try {
      msg = (MasterSlaveMessage)oIs.readObject();
      msg.newMessageId();
      if (msg.getStatus() != COMMAND) throw new Exception();
      msg.setStatus(ACK);
      oOs.writeObject(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }

    FilePartition partition;
    while (msg.newPartition() != null) {
    
      if (msg.getJobType() == MAP) mapPartition(msg);
      else if (msg.getJobType() == REDUCE) mapPartition(msg);

    }

  }

    
  

}


