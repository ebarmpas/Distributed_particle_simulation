import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.Dataset;

public class ParticleSimulation {
	public static void main(String[] args) throws IOException {
		//String fileSource = "/usr/local/spark/input.txt"; // Should be some file on your system
		Configuration conf = new Configuration(new File("configuration.txt"));
		
		SparkSession spark = SparkSession.builder().appName((String) conf.getValue("AppName")).getOrCreate();

		String[] InputSource = new String[1];
		InputSource[0] = (String) conf.getValue("InputSource"); 
		Dataset<String> fileData = spark.read().textFile(InputSource);
		
		ParticleDataset pd = new ParticleDataset(fileData);
		
		for(int i = 0; i < (int) conf.getValue("StepNumber"); i++){
			pd.step();
			pd.show();
			pd.output(i, (String)conf.getValue("OutputPath"));
		}
		
			
		
		spark.stop();
	}
}