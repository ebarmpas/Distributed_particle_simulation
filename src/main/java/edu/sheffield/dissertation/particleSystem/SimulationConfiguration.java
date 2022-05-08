/*
 * Holds information about the simulation. Currently holds:
 *  The name of the app
 *  The number of steps to be executed
 *  The directory in which the Particle dataset gets checkpointed
 *  The input directory
 *  The output directory
 *  The general force multiplier
 *  The width of the plane
 *  The height of the plane
 */
package edu.sheffield.dissertation.particleSystem;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

public class SimulationConfiguration implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private static int KEY = 0, VALUE = 1;
	//The different options are saved as key-value pairs. The keys are always string and the values can either be double, int, or String.
	private Map<String, Object> simulationSettings;
	private String filepath;
	
	public SimulationConfiguration(File source) throws FileNotFoundException {
		Scanner scan = new Scanner(source);
		
		simulationSettings = new TreeMap<String, Object>();
		filepath = source.getAbsolutePath();
		
		while(scan.hasNext()) {
			
			String line = scan.nextLine();
			String[] token;
			Object value = new Object();
			
			//Check if the next line is either empty or a comment, if yes, skip.
			if(line.length() == 0 || line.startsWith("//"))
				continue;
			
			//Split the line into two tokens: The value and the key.
			token = line.split("=");
			
			//Remove trailing and leading whitespace
			token[KEY] = token[KEY].trim();
			token[VALUE] = token[VALUE].trim();
			
			//Find out what kind of value key is. Tries for double, if it fails, tries for int, if that also fails, leaves it as a String.
			try {
				value = Double.parseDouble(token[VALUE]);
				try {
					value = Integer.parseInt(token[VALUE]);
				} catch (Exception e) {}
			} catch (Exception e) {
				value = token[VALUE];
			}
			simulationSettings.put(token[KEY], value);
		}
		
		scan.close();

	} 
	//Prints the contents of the configuration file as a table.
	public void print() {
		final int margin = 4;
		int maxKeyLength = 3, maxTypeLength = 4;
		
		//Iterate through every entry from the configuration file to find the max length of both the keys and the types.
		for(Entry<String, Object> entry : simulationSettings.entrySet()) {
			
			//Check if the length of the key is greater than the current max length of the key. If not, set it to it.
			if(entry.getKey().length() > maxKeyLength)
				maxKeyLength = entry.getKey().length();
			
			//Check if the length of the java class converted into a string is greater than the current max length of type. If not, set it to it.
			if(entry.getValue().getClass().toString().substring(16).length() > maxTypeLength) 
				maxTypeLength = entry.getValue().getClass().toString().substring(16).length();
		}
				
		//Print the headers the key and type headers to be of appropriate length so that everything is lined up.
		System.out.println("\nCONFIGURATION : " + filepath + "\n");
		System.out.println(pad("KEY", (maxKeyLength + margin)) + pad("TYPE", (maxTypeLength + margin)) + "VALUE");
		
		for(Entry<String, Object> entry : simulationSettings.entrySet()) {
			
			//Print the actual values of each entry on the simulation settings, while also padding them to ensure everything is lined up.
			System.out.println(pad(entry.getKey(), maxKeyLength + margin) +
			pad(entry.getValue().getClass().toString().substring(16), (maxTypeLength + margin)) + 
			entry.getValue());
		}
		
		//Leave an extra empty line at the end.
		System.out.print("\n");
	}
	//Simple getters that cast the object into the desired data type.
	public String getAppName() {
		return (String) simulationSettings.get("AppName");
	}
	public int getStepNumber() {
		return (int) simulationSettings.get("StepNumber");
	}
	public String getCheckpointDir() {
		return (String) simulationSettings.get("CheckpointDir");
	}
	public String getInputDir() {
		return (String) simulationSettings.get("InputDir");
	}	
	public String getOutputDir() {
		return (String) simulationSettings.get("OutputDir");
	}
	public int getCheckpointInterval() {
		return (int) simulationSettings.get("CheckpointInterval");
	}
	public double getForceMultiplier() {
		return getDouble("ForceMultiplier");
	}
	public int getPlaneWidth() {
		return (int) simulationSettings.get("PlaneWidth");
	}
	public int getPlaneHeight() {
		return (int) simulationSettings.get("PlaneHeight");
	}
	public int getSpeciesNumber() {
		return (int) simulationSettings.get("SpeciesNumber");
	}
	public int getSpeciesPopulation(int species) {
		return (int) simulationSettings.get("SpeciesPopulation" + species);
	}
	public double getSpeciesAttractionMultiplier(int species) {
		return  getDouble("SpeciesAttractionMultiplier" + species);
	}
	public double getSpeciesRepulsionMultiplier(int species) {
		return  getDouble("SpeciesRepulsionMultiplier" + species);
	}
	public double getSpeciesForceMultiplier(int species) {
		return  getDouble("SpeciesForceMultiplier" + species);
	}
	public int getSpeciesMaxLibido(int species) {
		return (int) simulationSettings.get("SpeciesMaxLibido" + species);
	}
	public int getSpeciesMaxAge(int species) {
		return (int) simulationSettings.get("SpeciesMaxAge" + species);
	}
	public double getSpeciesStandardDeviation(int species) {
		return getDouble("SpeciesStandardDeviation" + species);
	}
	public int getSeed() {
		return (int) simulationSettings.get("Seed");
	}
	public double getPartitioningInterval() {
		return getDouble("PartitioningInterval");
	}
	public int getPartitionNumber() {
		return (int) simulationSettings.get("PartitionNumber");
	}
	
	//Pad a String s with spaces until the String is of length len.
	private String pad(String s, int len) {
		for(int i = s.length(); i < len; i++)
			s+= " ";
		
		return s;
	}
	private static boolean isType(@SuppressWarnings("rawtypes") Class cls, Object value) {
		if(cls == value.getClass())
			return true;
		
		return false;
	}
	private double getDouble(String key) {
		if(isType(Integer.class, simulationSettings.get(key)))
			return (double)((int) simulationSettings.get(key));
		else
			return (double) simulationSettings.get(key);
	}
}
