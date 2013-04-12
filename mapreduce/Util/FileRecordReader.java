package Util;

import java.io.*;

/**
 * FileRecordReader
 * Provides a clean interface for reading a file as a sequence of records.
 * Using a FileRecordReader instance of a file, you can access records
 * as if they're elements in an array.
 */
public class FileRecordReader {

	private String fileName;
	private int recordLength;

	/** Use this for input files.
	 * They are all constant length, just cause.
	 * Trust me.
	 */
	public FileRecordReader (String _fname, int _recordLength) {
		this.fileName = _fname;
		this.recordLength = _recordLength;
	}
	
	public int numberOfRecords() {
		RandomAccessFile f;
		long temp = 0;
		try {
			f = new RandomAccessFile(fileName, "r");
			temp = f.length() / recordLength;
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find file " + fileName);
		} catch (IOException e) {
			System.out.println("IOException when getting length of file" + fileName);
		}
		
		return (int)temp;
	}

	/** getKeyValuePair
	 * @param recordNum
	 * Runs through a partition and returns an array of all K-V pairs in that
	 * partition
	 */
	public String[][] getKeyValuePairs (int partitionIndex, int partitionSize) {
		RandomAccessFile file;
		try {
			file = new RandomAccessFile(fileName, "r");
		} catch (FileNotFoundException e1) {
			System.err.println("Unable to open file "+ fileName);
			e1.printStackTrace();
			return null;
		}
		
		String[] res = new String[2];
		String[][] pairs = new String[2][partitionSize];
		//read with constant key/value sizes
		byte[] b = new byte[recordLength];


		for (int recordNum = 0; recordNum < partitionSize; recordNum++) {
			try {
				int bytesRead = file.read(b, partitionIndex + recordNum * recordLength,
						recordLength);
				
				if (bytesRead < recordLength) {
					file.close();
					return pairs;
				}
			} catch (IOException e) {
				System.err.println("getKeyValuePair: error reading file (1)");
				try {
					file.close();
				} catch (IOException e1) {
					System.err.println("error closing file");
				}
				return null;
			}
			String s = new String(b);

			//TODO make sure these offsets are correct
			int splitIndex = s.indexOf("\\:");
			res[0]         = s.substring(0, splitIndex - 1);
			res[1]         = s.substring(splitIndex + 2, recordLength - 1);

			pairs[recordNum] = res.clone();

		}
		try {
			file.close();
		} catch (IOException e) {
			System.err.println("error closing file");
		}
		return pairs;
	}


}








