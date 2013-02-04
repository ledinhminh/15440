//package com.cs440.lab1.processes;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

//import com.cs440.lab1.classes.TransactionalFileInputStream;
//import com.cs440.lab1.classes.TransactionalFileOutputStream;
//import com.cs440.lab1.interfaces.MigratableProcess;

public class MeterCheckProcess implements MigratableProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = -759423953872679791L;
	private String[] args;
	private String inFile,outFile;
	private String dictFile;
	
	private volatile Map<String, String> dictMap;
	
	private TransactionalFileInputStream dictFileStream;
	private TransactionalFileInputStream inFileStream;
	private TransactionalFileOutputStream outFileStream;
	
	private volatile boolean suspending;
	private boolean doneProcessing;
	
	public MeterCheckProcess(String[] _args) throws Exception {
		this.args = _args;
		
		if (_args.length != 3) {
			System.out.println("Usage: MeterCheckProcess <infile> <outfile> <dictfile>");
			throw new Exception("Invalid Arguments");
		}
		
		inFile = args[0];
		outFile = args[1];
		dictFile = args[2];
		
		
		dictFileStream = new TransactionalFileInputStream(dictFile);
		inFileStream = new TransactionalFileInputStream(inFile);
		outFileStream =  new TransactionalFileOutputStream(outFile);
		
	}
	
	public String toString() {
		String retStr = "";
		for (int i = 0; i < args.length; i++) {
			retStr += args[i] + " ";
		}
		return retStr;
	}
	
	/**
	 * createDictMap()
	 * Reads in the raw dictionary file and creates a map from
	 * each word to that words meter.(takes a while)
	 */
	private void createDictMap() {
		DataInputStream in = new DataInputStream(dictFileStream);
		dictMap = new HashMap<String,String>();
		String line;
		int i = 0;
		try {
			while ((line = in.readLine()) != null) {
				i++;
				//format for line is
				//<WORD> <phenome> <phenome2> ... <phenomen>
				String[] parts = line.split(" ");
				String[] phenomes = new String[parts.length - 1];
				
				System.arraycopy(parts, 1, phenomes, 0, parts.length -1);
				
				String meterString = "";
				for (String p : phenomes) {
					//each syllable has one phenome with an emphasis of either 0,1, or 2.
					if (p.indexOf('0') != -1)
						meterString += "0";
					else if (p.indexOf('1') != -1)
						meterString += "1";
					else if (p.indexOf('2') != -1)
						meterString += "2";
				}
				
				dictMap.put(parts[0], meterString);
				if (i%1000 == 0)
					System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileOutputStream fs;
		ObjectOutputStream os;
		
		try {
			fs = new FileOutputStream("/Users/nickzukoski/test/dictMap.ser");
			os = new ObjectOutputStream(fs);
			
			os.writeObject(dictMap);
			
			fs.close();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void loadDictMap() throws Exception {
		//load the dictMap from file
		FileInputStream fs;
		ObjectInputStream os;
		try {
			os = new ObjectInputStream(dictFileStream);
			Object dictObject = os.readObject();
			if (!(dictObject instanceof HashMap<?,?>)) {
				System.out.println("Bad dictionary file!");
				throw new Exception("Unable to parse dictionary file to a map");
			}
			dictMap = (HashMap<String, String>) dictObject;
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find dictionary file");
			throw new Exception ("Unable to find dictionary file");
		} catch (IOException e) {
			System.out.println("Unable to read dictionary file");
			throw new Exception ("Unable to read dictionary file");
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to cast dictionary object");
			throw new Exception("Unable to cast dictionary object");
		}
	}
	
	@Override
	public void run() {
		DataInputStream in = new DataInputStream(inFileStream);
		PrintStream out = new PrintStream(outFileStream);
		try {
			while (!suspending) {
				if (dictMap == null) {
					loadDictMap();
					System.out.println("dict loaded");
					continue;
				}
				
				String inputLine = in.readLine();
				if (inputLine == null) {
					doneProcessing = true;
					break;
				}
				String[] words = inputLine.split(" ");
				String meterString = "";

				for (String word : words) {
					String wordMeter = dictMap.get(word.toUpperCase());

					if (wordMeter == null) {
						// word wasn't in the dictionary, mark it and move on
						meterString += "_";
						continue;
					}
					else
						meterString += wordMeter;
				}

				out.println(meterString);
				System.out.println(inputLine + ":::" + meterString);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					//ignore
				}
				
			}

		} catch (IOException e) {
			doneProcessing = true;
		} catch (Exception e) {
			doneProcessing = true;
		}
		
		suspending = false;
	}
		// TODO Auto-generated method stub

	@Override
	public void suspend() {
		suspending = true;
		while (suspending && !doneProcessing);
	}
	
	public static void main(String[] argv) {
		String[] args = {"/Users/nickzukoski/test/in.txt", "/Users/nickzukoski/test/out.txt", "/Users/nickzukoski/test/dictMap.ser"};
		MigratableProcess p;
		try {
			p = new MeterCheckProcess(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		Thread t = new Thread(p);
		t.start();
		
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("suspending");
		p.suspend();
		System.out.println("suspended");
		
		FileOutputStream fs;
		ObjectOutputStream os;
		try {
			fs = new FileOutputStream("/Users/nickzukoski/test/process.test");
			os = new ObjectOutputStream(fs);
			
			os.writeObject(p);
			
			fs.close();
			os.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		FileInputStream fs;
//		ObjectInputStream os;
//		MigratableProcess p;
//			
//		try {
//			fs = new FileInputStream("/Users/nickzukoski/test/process.test");
//			os = new ObjectInputStream(fs);
//			p = (MigratableProcess)os.readObject();
//		} catch (Exception e) {
//			return;
//		}
//		System.out.println("loaded from disk");
//
//		Thread t = new Thread(p);
//		t.start();
//		System.out.println("started");
			
		
	}

}
