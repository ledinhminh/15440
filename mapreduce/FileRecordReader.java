import java.lang.*;
import java.io.*;


/**
 * FileRecordReader
 * Provides a clean interface for reading a file as a sequence of records.
 * Using a FileRecordReader instance of a file, you can access records
 * as if they're elements in an array.
 */
public class FileRecordReader implements Serializable {

  private RandomAccessFile file;
  private boolean isInput;
  private int recordLength;
  private int whiteSize = 1;
  private int keySize;
  private int valueSize;
  private int partitionIndex;



  /** Use this for input files.
   * They are all constant length, just cause.
   * Trust me.
   */

  public FileRecordReader (String fname, int _partitionIndex) {
    isInput      = false;
    try {
      file         = new RandomAccessFile(fname, "r");
    } catch (Exception e) {
      e.printStackTrace();
    }
    partitionIndex = _partitionIndex;
  }



  public FileRecordReader (String fname, int _keySize, int _valueSize) {
    isInput        = true;
    try {
      file         = new RandomAccessFile(fname, "r");
    } catch (Exception e) {
      e.printStackTrace();
    }
    keySize        = _keySize;
    valueSize      = _valueSize;
    partitionIndex = 0;
  }



  public FileRecordReader (String fname, int _keySize, int _valueSize,
                           int _partitionIndex) {
    isInput        = true;
    try {
      file         = new RandomAccessFile(fname, "r");
    } catch (Exception e) {
      e.printStackTrace();
    }
    keySize        = _keySize;
    valueSize      = _valueSize;
    partitionIndex = _partitionIndex;
  }




  /** getKeyValuePair
   * @param recordNum
   * Runs through file and retrieves the recordNum'th record
   */
  public String[] getKeyValuePair (int recordNum) {
    String[] res = new String[2];
    if (isInput) {
      //read with constant key/value sizes
      byte[] b = new byte[keySize + valueSize + 1];
      
      //+1 is for whitespace after every record
      try {
        file.read(b, partitionIndex + recordNum * (keySize + 1 + valueSize + 1),
                 keySize + valueSize + 1);
      } catch (IOException e) {
        System.err.println("getKeyValuePair: error reading file");
      }
      String s = new String(b);
      
      //TODO make sure these offsets are correct
      res[0]   = s.substring(0, keySize - 1);
      res[1]   = s.substring(keySize + 1, keySize + valueSize);
      
      return res;
    
    } else {
      //TODO read with variable key/value sizes
    }

    return new String[1];
  }
      

}
      





