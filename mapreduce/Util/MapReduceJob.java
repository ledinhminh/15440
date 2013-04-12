package Util;
import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author nickzukoski
 *
 *
 * MapReduceJob is the interface that mapreduce jobs must conform to
 * to be able to be run by our MapReduce Framework
 */
public interface MapReduceJob extends Serializable {
	public void setInputFiles(List<String> inputFiles);
	public void setOutputFile(String outputFile);
	public List<String> getInputFiles();
	public String getOutputFile();
	
	public int getRecordSize();
	
	public String getReduceIdentity();
	
	/**
	 * The map function that is applied to every record
	 * 
	 * @param key The input key
	 * @param val The input value
	 * @return An array of [Key,Value]
	 */
	public String[] map(String key, String val);
	
	/**
	 * 
	 * @param key The key of all of the vals
	 * @param vals The values to reduce
	 * @param initVal An initial seed value (could be the identity or
	 * 			the result of a previous reduce)
	 * @return Returns a single reduced value from all of the given vals.
	 */
	public String reduce(String key, String[] vals, String initVal);
	

}
