import java.io.*;
import java.lang.*;


public class MasterSlaveMessage implements Serializable {

  private int slaveId;
  private int messageId;
  private static final char STATUS_REQ = '?';
  private static final char STATUS_RSP = '!';
  private static final char COMMAND    = 'c';
  private static final char MAP        = 'm';
  private static final char REDUCE     = 'r';

  private char jobType;

  private char status;


  //partitions for input data
  private FilePartition currentPartition;
  private LinkedList<FilePartition> partitions = new LinkedList();


  //this is for recovery; if the master detects that I've failed, she'll
  //take the most recent MasterSlaveMessage and send a new slave the remaining
  //partitions as well as the output partition, so the new guy can continue
  //almost where I left off
  private FilePartition outputPartition;

  //command from master to slave
  public MasterSlaveMessage (LinkedList<FilePartition> _partitions,
                             int _slaveId, int _messageId, char _jobType) {

    partitions = _partitions;
    messageId  = _messageId;
    slaveId    = _slaveId;
    jobType    = _jobType;
    status     = COMMAND;
  }

  //status report
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

  public char getCurrentPartition() {
    return currentPartition;
  }

  public char getJobType() {
    return jobType;
  }

  public char getSlaveId() {
    return slaveId;
  }

  public char getMessageId() {
    return messageId;
  }

  public char newMessageId() {
    return ++messageId;
  }

  public FilePartition newPartition() {
    return (currentPartition = partitions.remove());
  }

}
