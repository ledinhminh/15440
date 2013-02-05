//package com.cs440.lab1.processes;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

//import com.cs440.lab1.classes.TransactionalFileInputStream;
//import com.cs440.lab1.classes.TransactionalFileOutputStream;
//import com.cs440.lab1.interfaces.MigratableProcess;

public class RhymeProcess implements MigratableProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8975395894819217960L;

	private class SortingLine implements Comparable, Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5214954876345485725L;
		String rhyme, fullLine;
		public SortingLine(String _line, String _rhyme) {
			this.rhyme = _rhyme;
			this.fullLine = _line;
		}
		
		public String getRhyme() {
			return rhyme;
		}
		
		public String getLine() {
			return fullLine;
		}
		
		public String toString() {
			return rhyme + ": " + fullLine;
		}
		
		@Override
		public int compareTo(Object arg0) {
			SortingLine otherL = (SortingLine) arg0;
			int rhymeCompare = rhyme.compareTo(otherL.rhyme);
			if (rhymeCompare != 0)
				return rhymeCompare;
			else 
				return fullLine.compareTo(otherL.fullLine);
		}
		
	}
	
	private String[] args;
	private String inFile,outFile;
	private String dictFile;
	
	//transient because it would take too long to
	//serialize/deserialze it. So instead we do that
	//during the main run loop so that it doesnt block
	//the master process
	private transient Map<String, String> dictMap;
	
	private SortedSet<SortingLine> rhymeSorter;
	private SortingLine[] sortedArray;
	private int nextLineToWrite;
	
	private TransactionalFileInputStream dictFileStream;
	private TransactionalFileInputStream inFileStream;
	private TransactionalFileOutputStream outFileStream;
	
	private volatile boolean suspending;
	private transient boolean loadingDict;
	private boolean doneSorting;
	private boolean doneProcessing;
	private boolean startedProcessing;
	
	public RhymeProcess(String[] _args) throws Exception {
		this.args = _args;
		
		if (_args.length != 3) {
			System.out.println("Usage: RhymeProcess <infile> <outfile> <dictfile>");
			throw new Exception("Invalid Arguments");
		}
		
		inFile = args[0];
		outFile = args[1];
		dictFile = args[2];
		
		
		dictFileStream = new TransactionalFileInputStream(dictFile);
		inFileStream = new TransactionalFileInputStream(inFile);
		outFileStream =  new TransactionalFileOutputStream(outFile);
		
		rhymeSorter = new TreeSet<SortingLine>();
		
		suspending = false;
		doneSorting = false;
		doneProcessing = false;
		startedProcessing = false;
		loadingDict = false;
		
	}
	
	private void buildDictMap() {
		loadingDict = true;
		DataInputStream in = new DataInputStream(dictFileStream);
		dictMap = new HashMap<String,String>();
		String line;
		int j = 0;
		try {
			while ((line = in.readLine()) != null && !suspending) {
				//format for line is
				//<WORD> <phoneme> <phoneme2> ... <phoneme N>
				String[] parts = line.split(" ");
				String[] phonemes = new String[parts.length - 1];
				
				System.arraycopy(parts, 1, phonemes, 0, parts.length -1);
				String rhymeStr = "";
				
				//We are defining the rhyme as all of the phonemes including
				//and after the last phoneme to be marked with an
				//emphasis. (not the best way to do it, but it'll do)
				for (int i = phonemes.length - 1; i >= 0; i--) {
					if (phonemes[i].indexOf("0") != -1
						|| phonemes[i].indexOf("1") != -1
						|| phonemes[i].indexOf("2") != -1) {
						//phoneme is the last one we want but get rid of the number
						rhymeStr = phonemes[i].substring(0, phonemes[i].length()-2) + rhymeStr;
						break;
					} else {
						rhymeStr = phonemes[i] + rhymeStr;
					}
						
				}
				if (j++%1000 == 0) {
					System.out.println(parts[0] + ":" + rhymeStr);
				}
				dictMap.put(parts[0], rhymeStr);
			}
		} catch (IOException e) {
			System.out.println("Interrupted while building dictionary");
			loadingDict = false;
			return;
		}
		FileOutputStream fs;
		ObjectOutputStream os;
		
		try {
			fs = new FileOutputStream("/Users/nickzukoski/test/rhymeMap.ser");
			os = new ObjectOutputStream(fs);
			
			os.writeObject(dictMap);
			
			fs.close();
			os.close();
		} catch (FileNotFoundException e) {
			System.out.println("Couldn't find dictionary file");
		} catch (IOException e) {
			System.out.println("Interrupted while building dictionary");
		}
		loadingDict = false;
	}
	
	private void loadDictMap() throws Exception {
		loadingDict = true;
		//load the dictMap from file
		ObjectInputStream os;
		try {
			os = new ObjectInputStream(dictFileStream);
			Object dictObject = os.readObject();
			if (!(dictObject instanceof HashMap<?,?>)) {
				System.out.println("Bad dictionary file!");
				loadingDict = false;
				throw new Exception("Unable to parse dictionary file to a map");
			}
			dictMap = (HashMap<String, String>) dictObject;
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find dictionary file");
			loadingDict = false;
			return;
		} catch (IOException e) {
			System.out.println("Unable to read dictionary file");
			loadingDict = false;
			return;
		} catch (ClassNotFoundException e) {
			System.out.println("Unable to cast dictionary object");
			loadingDict = false;
			return;
		}
		loadingDict = false;
	}
	
	public String toString() {
		String retStr = "";
		for (int i = 0; i < args.length; i++) {
			retStr += args[i] + " ";
		}
		return retStr;
	}

	@Override
	public void run() {
		startedProcessing = true;
		DataInputStream in = new DataInputStream(inFileStream);
		PrintStream out = new PrintStream(outFileStream);
		try {
			while (!suspending) {
				if (dictMap == null) {
					loadDictMap();
					//buildDictMap();
					
					System.out.println("dict loaded");
					continue;
				}
				
				if ( !doneSorting) {
					String inputLine = in.readLine();
					if (inputLine == null) {
						doneSorting = true;
						nextLineToWrite = 0;
						sortedArray = rhymeSorter.toArray(new SortingLine[rhymeSorter.size()]);
						rhymeSorter.clear();
						rhymeSorter = null;
						continue;
					}

					System.out.println(inputLine);
					String[] words = inputLine.split(" ");
					String rhymeWord = words[words.length-1];
					System.out.println("RhymeWord: " + rhymeWord);
					String rhyme = dictMap.get(rhymeWord.toUpperCase());
					System.out.println("Rhyme: " + rhyme);
					
					if (rhyme == null) {
						//couldn't find the word in the dictionary
						rhyme = "_";
					}
					
					SortingLine l = new SortingLine(inputLine, rhyme);
					rhymeSorter.add(l);
				}
				else {
					//time to print!
					if (nextLineToWrite >= sortedArray.length) {
						doneProcessing = true;
						//everythings written
						break;
					}
					System.out.println("writing");
					SortingLine l = sortedArray[nextLineToWrite];
					out.println(l);
					nextLineToWrite++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("errror dawg");
			doneProcessing = true;
		}
		System.out.println("done");
		suspending = false;

	}

	@Override
	public void suspend() {
		suspending = true;
		while (suspending && startedProcessing && !doneProcessing)
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				//ignore
			}
	}
	
	public static void main(String[] argv) {
		String[] args = {"/Users/nickzukoski/test/in.txt", "/Users/nickzukoski/test/out.txt", "/Users/nickzukoski/test/rhymeMap.ser"};
		MigratableProcess p;
		try {
			p = new RhymeProcess(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		Thread t = new Thread(p);
		t.start();
		
		try {
			Thread.sleep(1000);
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
