import java.io.*;
import java.lang.*;
import java.util.*;

public class MasterSlaveMessage implements Serializable {

  private int slaveId;
  private int messageId;
  private static final char STATUS_REQ = '?';
  private static final char STATUS_RSP = '!';
  private static final char COMMAND    = 'c';
  private static final char ACK        = 'a';
  private static final char MAP        = 'm';
  private static final char REDUCE     = 'r';



  private char jobType;

  private char status;


  //partitions for input data
  private FilePartition currentPartition;
  private LinkedList<FilePartition> partitions = new LinkedList<FilePartition>();


  private RandomAccessFile outFile;
  //for recovery
  private int outFileOffset;

  //command from master to slave
  public MasterSlaveMessage (LinkedList<FilePartition> _partitions,
                             int _slaveId, int _messageId, char _jobType,
                             String outFileName, int _outFileOffset) {

    partitions    = _partitions;
    messageId     = _messageId;
    slaveId       = _slaveId;
    jobType       = _jobType;
    status        = COMMAND;
    try {
      File file     = new File(outFileName);
      //if it doesn't exist, we'll create a new one
      file.createNewFile();
      //TODO check that these permissions are reasonable
      outFile       = new RandomAccessFile(file, "rw");
    } catch (Exception e) {
      e.printStackTrace();
    }
    outFileOffset = _outFileOffset;

  }


  //use this when recovering...it's cleaner
  public MasterSlaveMessage (LinkedList<FilePartition> _partitions,
                             int _slaveId, int _messageId, char _jobType,
                             RandomAccessFile _outFile, int _outFileOffset) {

    partitions    = _partitions;
    messageId     = _messageId;
    slaveId       = _slaveId;
    jobType       = _jobType;
    status        = COMMAND;
    outFile       = _outFile;
    outFileOffset = _outFileOffset;

  }

  //TODO setStatus functions and stuff so we don't have to create a new
  //message every transaction?


  //status report from master to slave or request from master
  public MasterSlaveMessage (char _status, FilePartition _currentPartition,
                            int _slaveId, int _messageId, char _jobType) {

    slaveId          = _slaveId;
    messageId        = _messageId;
    jobType          = _jobType;
    currentPartition = _currentPartition;
    status           = _status;

  }

  public char getStatus() {
    return status;
  }

  public FilePartition getCurrentPartition() {
    return currentPartition;
  }

  public char getJobType() {
    return jobType;
  }

  public int getSlaveId() {
    return slaveId;
  }

  public int getMessageId() {
    return messageId;
  }

  public int newMessageId() {
    return ++messageId;
  }

  public FilePartition newPartition() {
    return (currentPartition = partitions.remove());
  }

  public FilePartition currentPartition() {
    return currentPartition;
  }

  public void setStatus (char _status) {
    status = _status;
  }

}
