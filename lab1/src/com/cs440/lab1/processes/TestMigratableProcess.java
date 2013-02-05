//package com.cs440.lab1.processes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

import com.cs440.lab1.classes.TransactionalFileInputStream;
import com.cs440.lab1.classes.TransactionalFileOutputStream;
import com.cs440.lab1.interfaces.MigratableProcess;

public class TestMigratableProcess implements MigratableProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3200687207966080647L;
	
	private String[] args;
	private String inFile;
	private String outFile;
	
	private TransactionalFileInputStream inStream;
	private TransactionalFileOutputStream outStream;
	
	private volatile boolean suspending;
    private boolean isRunning;
    private boolean isFinished;
	public TestMigratableProcess(String[] _args) throws Exception {
		this.args = _args;
		
		if (args.length != 3) {
			System.out.println("usage: TestMigratableProcess <infile> <outfile>");
			throw new Exception("Invalid arguments");
		}
		inFile = args[1];
		outFile = args[2];	
		
		inStream = new TransactionalFileInputStream(inFile);
		outStream = new TransactionalFileOutputStream(outFile);
		suspending = false;
        isRunning = false;
        isFinished = false;
	}
	
	public String toString() {
		return "TestMigratableProcess " + args[1] + " " + args[2];
	}
	
	@Override
	public void run() {
        isRunning = true;
		suspending = false;
		DataInputStream in = new DataInputStream(inStream);
		PrintStream out = new PrintStream(outStream);
		System.out.println(suspending);
		try {
			String line;
			while (!suspending) {
				line = in.readLine();
				if (line == null) {
					System.out.println("line is null");
					isFinished = true;
                    break;
				}
				out.println(line);
				System.out.println("LINE::" + line);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					//ignore
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		suspending = false;
	}

	@Override
	public void suspend() {
		suspending = true;
		while(suspending && isRunning && !isFinished);
	}
}
