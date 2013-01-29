package com.cs440.lab1.classes;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class TransactionalFileInputStream extends InputStream implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4452549782932382119L;
	String fileName;
	long position;
	
	TransactionalFileInputStream(String _fileName) {
		this.fileName = _fileName;
		this.position = 0;
	}
	
	
	private FileInputStream openFile() throws IOException {
		FileInputStream fs = new FileInputStream(fileName);
		fs.skip(position);
		return fs;
	}
	
	/*Reads a single byte from the input stream
	*/
	@Override
	public int read() throws IOException {
		
		FileInputStream fs = openFile();
		int retVal = fs.read();
		fs.close();
		
		if (retVal != -1)
			position += 1;
		
		return retVal;
	}
	
	/*Reads up to b.length bytes of data from the input stream
	*/
	@Override
	public int read(byte[] b) throws IOException  {
		FileInputStream fs = openFile();
		int retVal = fs.read(b);
		fs.close();
		
		if (retVal != -1)
			position += retVal;
		
		return retVal;
	}
	
	/*Reads len bytes into b starting at b[off]
	*/
	@Override
	public int read(byte[] b, int off, int len) throws IOException  {
		FileInputStream fs = openFile();
		int retVal = fs.read(b, off, len);
		fs.close();
		
		if (retVal != -1)
			position += retVal;
		
		return retVal;
	}
	
	/**
	 * skip (long n)
	 * Skips ahead in the stream by N bytes.
	 * 
	 * NOTE:Does not actually read from the file so may actually 
	 * seek past the end of the file.
	 */
	@Override
	public long skip(long n) {
		if ( n <= 0 ) return 0;
		position += n;
		return n;
	}

}
