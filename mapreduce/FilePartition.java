import java.io.*;
import java.lang.*;

/** FilePartition
 * for logical partitioning of files.
 * this partitioning is useful for recovery
 */
public class FilePartition implements Serializable {

  RandomAccessFile file;
  
  /**partitionIndex is an absolute offset into file.  partitionSize
   * is the number of records in this partition
   */
  int partitionIndex;
  int partitionSize;

  public FilePartition (String fname, int _partitionIndex, int _partitionSize,
                       boolean reader) {
    try {
        if (reader) file = new RandomAccessFile(fname, "r");
        else        file = new RandomAccessFile(fname, "rw");
    } catch (FileNotFoundException e) {
      System.err.println("FilePartion: file " + fname + " not found");
    }
    partitionIndex = _partitionIndex;
    partitionSize  = _partitionSize;

  }

  public int getPartitionIndex() {
    return partitionIndex;
  }

  public int getPartitionSize() {
    return partitionSize;
  }

}
