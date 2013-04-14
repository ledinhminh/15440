package Examples;

import java.util.ArrayList;
import java.util.List;

import Util.MapReduceJob;

/**
 * 
 * @author nickzukoski
 * LargestWord is a MapReduceJob that reduces a large dataset
 * down to its single largest word.
 */
public class LargestWord implements MapReduceJob {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8351463585706692739L;
	
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
		return "0, ";
	}

	@Override
	public List<String[]> map(String key, String val) {
		String[] words = val.split(" ");
		List<String[]> output = new ArrayList<String[]>(words.length);
		
		for (String w : words) {
			output.add(new String[] { " ", w});
		}
			
		return output;
	}

	@Override
	public String reduce(String key, List<String> vals, String initVal) {
		int maxLen = Integer.parseInt(initVal);
		String longestStr = "";
		for (String s : vals) {
			if (s.length() > maxLen) {
				maxLen = s.length();
				longestStr = s;
			}
		}
		
		return longestStr;
	}

}
