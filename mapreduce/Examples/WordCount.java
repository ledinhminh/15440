package Examples;

import java.util.ArrayList;
import java.util.List;

import Util.MapReduceJob;

/**
 * 
 * @author nickzukoski
 * WordCount is a MapReduce Job that takes in a set of strings
 * and outputs the data for a histogram from word to word count
 * in that set.
 */
public class WordCount implements MapReduceJob {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6217380880151520602L;
	private List<String> inputFiles;
	private String outputFile;
	
	@Override
	public void setInputFiles(List<String> inputFiles) {
		this.inputFiles = inputFiles;
	}

	@Override
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public List<String> getInputFiles() {
		return inputFiles;
	}

	@Override
	public String getOutputFile() {
		return outputFile;
	}

	@Override
	public int getRecordSize() {
		return 500;
	}

	@Override
	public String getReduceIdentity() {
		return "0";
	}

	@Override
	public List<String[]> map(String key, String val) {
		String[] words = val.split(" ");
		List<String[]> out = new ArrayList<String[]>(words.length);
		for (String s : words) {
			out.add(new String[] {s, "1"});
		}
		
		return out;
	}

	@Override
	public String reduce(String key, List<String> vals, String initVal) {
		int count = Integer.parseInt(initVal);
		for (String s : vals) {
			count += Integer.parseInt(s);
		}
		return ""+count;
	}

}
