/* SimpleApp.java */
import org.apache.spark.sql.SparkSession;
import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.Dataset;

public class ParticleSimulation {
	public static void main(String[] args) throws IOException {
		String fileSource = "/usr/local/spark/input.txt"; // Should be some file on your system
		
		SparkSession spark = SparkSession.builder().appName("Simple Application").getOrCreate();

		Dataset<String> fileData = spark.read().textFile(fileSource);
		
		ParticleDataset pd = new ParticleDataset(fileData);
		
		for(int i = 0; i < 1; i++){
			pd.step();
			pd.show();
			pd.output(i);
		}
		
			
		
		spark.stop();
	}
}