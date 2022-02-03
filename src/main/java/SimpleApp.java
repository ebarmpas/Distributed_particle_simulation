/* SimpleApp.java */
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;

public class SimpleApp {
	public static void main(String[] args) {
		String fileSource = "/usr/local/spark/input.txt"; // Should be some file on your system
		SparkSession spark = SparkSession.builder().appName("Simple Application").getOrCreate();

		
		Dataset<String> fileData = spark.read().textFile(fileSource);
		
		ParticleDataset pd = new ParticleDataset(fileData);
		
		for(int i = 0; i < 5; i++){
			pd.show();
			pd.step();
		}

		spark.stop();
	}
}