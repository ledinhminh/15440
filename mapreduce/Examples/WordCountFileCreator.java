package Examples;

import java.io.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import Util.FileRecordWriter;
import Util.FileRecordReader;
/**
 * 
 * @author nickzukoski
 * Sets up the input record file for both WordCount map reduce
 * and LongestWord mapreduce jobs.
 */
public class WordCountFileCreator {
	public static void main (String args[]) throws IOException {
		if (args[0].equals("encode")) {
			WordCount wcJob = new WordCount();
			FileRecordWriter fw = new FileRecordWriter(args[1], wcJob.getRecordSize());
			
			List<String[]> lines = new LinkedList<String[]>();
			
			BufferedReader br = new BufferedReader(new FileReader(args[2]));
			String line;
			while ((line = br.readLine()) != null) {
			   lines.add(new String[] {" ", line});
			}
			br.close();
			System.out.println(wcJob.getClass().getName());
			fw.writeOut(lines);
		} else if (args[0].equals("decode")) {
			WordCount wcJob = new WordCount();
			FileRecordReader fr = new FileRecordReader(args[1], wcJob.getRecordSize());
			int size = fr.numberOfRecords();
			String[][] kvs = fr.getKeyValuePairs(0, size);
			PrintWriter p = new PrintWriter(new FileWriter(args[2]));
			for (int i = 0; i < kvs.length; i++) {
				p.println(kvs[i][0] + ":" + kvs[i][1]);
			}
			p.close();
		}
	}

}
