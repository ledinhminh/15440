package Util;
import java.io.*;

/**
 * 
 * @author nickzukoski
 * FilePartition
 * 
 * Describes a partition of a file to be used for a map or reduce.
 */
public class FilePartition implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5919955333397219880L;
	
	
	/**partitionIndex is an absolute offset into file.  partitionSize
	 * is the number of records in this partition
	 */
	String fileName;
	int partitionIndex;
	int partitionSize;

	public FilePartition (String fname, int _partitionIndex, int _partitionSize) {
		fileName = fname;
		partitionIndex = _partitionIndex;
		partitionSize  = _partitionSize;

	}

	public int getPartitionIndex() {
		return partitionIndex;
	}

	public int getPartitionSize() {
		return partitionSize;
	}

	public String getFileName() {
		return fileName;
	}

}
