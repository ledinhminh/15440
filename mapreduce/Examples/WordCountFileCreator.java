package Examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import Util.FileRecordWriter;

/**
 * 
 * @author nickzukoski
 * Sets up the input record file for a WordCount mapReduce
 */
public class WordCountFileCreator {
	public static void main (String args[]) throws IOException {
		WordCount wcJob = new WordCount();
		FileRecordWriter fw = new FileRecordWriter(args[1], wcJob.getRecordSize());
		
		List<String[]> lines = new LinkedList<String[]>();
		
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		String line;
		while ((line = br.readLine()) != null) {
		   lines.add(new String[] {" ", line});
		}
		br.close();
		System.out.println(wcJob.getClass().getName());
		fw.writeOut(lines);
	}

}
