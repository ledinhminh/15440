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

  private static final int LENGTHSIZE = 10;


  /** Use this for input files.
   * They are all constant length, just cause.
   * Trust me.
   */

  public FileRecordReader (String fname, int _partitionIndex, int _recordLength) {
    isInput      = false;
    try {
      file         = new RandomAccessFile(fname, "r");
    } catch (Exception e) {
      e.printStackTrace();
    }
    partitionIndex = _partitionIndex;
    recordLength   = _recordLength;
  }



  public FileRecordReader (String fname, int _partitionIndex) {
    isInput        = true;
    try {
      file         = new RandomAccessFile(fname, "r");
    } catch (Exception e) {
      e.printStackTrace();
    }
    partitionIndex = _partitionIndex;
  }




  /** getKeyValuePair
   * @param recordNum
   * Runs through file and retrieves the recordNum'th record
   */
  public String[][] getKeyValuePairs (int partitionIndex, int partitionSize) {
    String[] res = new String[2];
    String[][] pairs = new String[2][partitionSize];
    if (isInput) {
      //read with constant key/value sizes
      byte[] b = new byte[recordLength + 1];
      

      for (int recordNum = 0; recordNum < partitionSize; recordNum++) {
        //+1 is for whitespace after every record
        try {
          file.read(b, partitionIndex + recordNum * (recordLength + 1),
                  recordLength);
        } catch (IOException e) {
          System.err.println("getKeyValuePair: error reading file (1)");
          return null;
        }
        String s = new String(b);
      
        //TODO make sure these offsets are correct
        int splitIndex = s.indexOf(' ');
        res[0]         = s.substring(0, splitIndex - 1);
        res[1]         = s.substring(splitIndex + 1, recordLength - 1);

        pairs[recordNum] = res;
      
      }

      return pairs;
    
    } else {
      //TODO read with variable key/value sizes
      //get the key length
      int offset = partitionIndex;
      int idx;
      byte[] b   = new byte[LENGTHSIZE];

      for (int recordNum = 0; recordNum < partitionSize; recordNum++) {
        
        try {
          file.read(b, offset, LENGTHSIZE);
          offset += LENGTHSIZE + 1;
        } catch (IOException e) {
          System.err.println("getKeyValuePair: error reading file (2)");
          return null;
        }
        
        String s        = new String(b);
        String[] kvLen  = s.split(" ");
        int keyLen      = (new Integer(kvLen[0])).intValue();
        int valueLen    = (new Integer(kvLen[1])).intValue();

        b               = new byte[keyLen + valueLen + 1];
        try {
          file.read(b, offset, keyLen + valueLen + 1);
          //set offset to start of next record
          offset += keyLen + valueLen + 1 + 1;
        } catch (IOException e) {
          System.err.println("getKeyValuePair: error reading file (3)");
          return null;
        }
        String KV    = new String(b);
        String key   = KV.substring(0, keyLen);
        String value = KV.substring(keyLen + 1, keyLen + valueLen + 1);
        
        pairs[recordNum][0] = key;
        pairs[recordNum][1] = value;

      }

      return pairs;

    }


  }


}
      





