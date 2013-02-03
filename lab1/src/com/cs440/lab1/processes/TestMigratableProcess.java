package com.cs440.lab1.processes;

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
	
	public TestMigratableProcess(String[] _args) {
		this.args = _args;
		
		if (args.length < 3)
			System.out.println("bad arguments dawg");
		inFile = args[0];
		outFile = args[1];	
		
		inStream = new TransactionalFileInputStream(inFile);
		outStream = new TransactionalFileOutputStream(outFile);
		suspending = false;
	}
	
	public String toString() {
		return "TestMigratableProcess " + args;
	}
	
	@Override
	public void run() {
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
					break;
				}
				out.println(line);
				System.out.println("LINE::" + line);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//ignore
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void suspend() {
		suspending = true;
	}
	
	public static void main(String[] argv) {
//		String[] args = {"/Users/nickzukoski/test/in.txt", "/Users/nickzukoski/test/out.txt", "ish"};
//		MigratableProcess p = new TestMigratableProcess(args);
//		Thread t = new Thread(p);
//		t.start();
//		System.out.println("thread running");
//		try {
//			Thread.sleep(5000);
//		} catch (InterruptedException e) {
//		}
//		
//		System.out.println("thread suspending");
//		p.suspend();
//		System.out.println("thread suspended");
//		FileOutputStream fs;
//		ObjectOutputStream os;
//		try {
//			fs = new FileOutputStream("/Users/nickzukoski/test/process.test");
//			os = new ObjectOutputStream(fs);
//			
//			os.writeObject(p);
//			
//			fs.close();
//			os.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
		
		FileInputStream fs;
		ObjectInputStream os;
		MigratableProcess p;
		try {
			fs = new FileInputStream("/Users/nickzukoski/test/process.test");
			os = new ObjectInputStream(fs);
			p = (MigratableProcess) os.readObject();
			Thread t = new Thread(p);
			t.start();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
	}

}
