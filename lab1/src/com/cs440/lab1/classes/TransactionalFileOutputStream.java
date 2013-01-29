package com.cs440.lab1.classes;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class TransactionalFileOutputStream extends OutputStream implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 237544828305277844L;
	String fileName;
	long position;
	
	private RandomAccessFile openFile() throws IOException {
		RandomAccessFile file = new RandomAccessFile(fileName, "rws");
		file.seek(position);
		return file;
		
	}
	
	public TransactionalFileOutputStream(String _fileName) {
		this.fileName = _fileName;
		this.position = 0;
	}
	
	@Override
	public void write(int b) throws IOException {
		RandomAccessFile f = openFile();
		f.write(b);
		f.close();
		
		position++;
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		RandomAccessFile f = openFile();
		f.write(b);
		f.close();
		
		position += b.length;
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		RandomAccessFile f = openFile();
		f.write(b);
		f.close();
		
		position += len;
	}
	
}
