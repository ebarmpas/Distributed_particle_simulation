import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Configuration {
	private HashMap<String, Object> conf;
	
	public Configuration(File source) throws FileNotFoundException {
		Scanner scan = new Scanner(source);
		conf = new HashMap<String, Object>();
		
		while(scan.hasNext()) {
			String[] token = scan.nextLine().split("=");
			Object value = new Object();
			
			try {
				value = Double.parseDouble(token[1]);
				try {
					value = Integer.parseInt(token[1]);
				} catch (Exception e) {

				}
			} catch (Exception e) {
				value = token[1];
			}
			System.out.println(token[0] + "\t" + value + "\t" + value.getClass());
			conf.put(token[0], value);
		}
		scan.close();
	}
	
	public Object getValue(String key) {
		return conf.get(key);
	}
}
