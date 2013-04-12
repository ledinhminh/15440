package Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileRecordWriter {
	private String fileName;
	private int recordLength;
	
	public FileRecordWriter (String _fname, int _recordLength) {
		this.fileName = _fname;
		this.recordLength = _recordLength;
	}
	
	public void writeOut(List<String[]> keyVals) {
		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(new File(fileName), true);
		} catch (IOException e) {
			System.err.println("Error opening file to write out: " + fileName);
			e.printStackTrace();
		}
		
		for (int i = 0; i < keyVals.size(); i++) {
			String tempstr = keyVals.get(i)[0] + "\\:" + keyVals.get(i)[1];
			byte[] fullRecord = new byte[recordLength];
			byte bytes[] = tempstr.getBytes();
			
			if (bytes.length > recordLength) {
				System.err.println("Record too long to record: " + bytes.length + 
						" max recordLength: " + recordLength);
				continue;
			}
			
			for (int j = 0; j < recordLength; j++) {
				if (j > bytes.length - 1)  fullRecord[j] = bytes[j];
				else fullRecord[j] = 0;
			}
			
			try {
				fOut.write(fullRecord);
			} catch (IOException e) {
				System.err.println("Error writing record to file");
				e.printStackTrace();
				continue;
			}
			
		}
		
		try {
			fOut.close();
		} catch (IOException e) {
			System.err.println("Error closing the file");
			e.printStackTrace();
		}
	}
}
