import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class Configuration {
	private HashMap<String, Object> conf;
	private String filepath;
	public Configuration(File source) throws FileNotFoundException {
		Scanner scan = new Scanner(source);
		conf = new HashMap<String, Object>();
		filepath = source.getAbsolutePath();
		while(scan.hasNext()) {
			String line = scan.nextLine();
			String[] token;
			Object value = new Object();
			
			if(line.length() == 0 || line.startsWith("//"))
				continue;
			
			token = line.split("=");
			
			try {
				value = Double.parseDouble(token[1]);
				try {
					value = Integer.parseInt(token[1]);
				} catch (Exception e) {

				}
			} catch (Exception e) {
				value = token[1];
			}
			
			conf.put(token[0], value);
		}
		scan.close();
	}
	
	public Object getValue(String key) {
		return conf.get(key);
	}
	public String getAppName() {
		return (String) conf.get("AppName");
	}
	public Integer getSteps() {
		return (Integer) conf.get("Steps");
	}
	public void print() {
		final int margin = 4;
		int maxKeyLength = 3, maxTypeLength = 4;
		
		String key = "KEY", type = "TYPE";
		
		for(Entry<String, Object> entry : conf.entrySet()) {
			
			if(entry.getKey().length() > maxKeyLength)
				maxKeyLength = entry.getKey().length();
			
			if(entry.getValue().getClass().toString().length() > maxTypeLength) 
				maxTypeLength = entry.getValue().getClass().toString().substring(16).length();
		}
		
		key = pad(key, maxKeyLength + margin);
		type = pad(type, maxTypeLength + margin);
		System.out.println("\nCONFIGURATION : " + filepath + "\n");
		System.out.println(key + type + "VALUE");
		
		for(Entry<String, Object> entry : conf.entrySet()) {
			String k = entry.getKey(), t = entry.getValue().getClass().toString().substring(16);
			
			k = pad(k, maxKeyLength + margin);
			t = pad(t, maxTypeLength + margin);
			
			System.out.println(k + t + entry.getValue());
		}
		System.out.println();
	}
	private String pad(String s, int len) {
		for(int i = s.length(); i < len; i++)
			s+= " ";
		
		return s;
	}
}
