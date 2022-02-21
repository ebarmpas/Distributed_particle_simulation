import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;

import org.apache.spark.sql.Dataset;

public class ParticleSimulation {
	
	public static void main(String[] args) throws IOException {
		
		String[] InputSource = new String[1];
		SimulationConfiguration simConf = new SimulationConfiguration(new File(args[0]));
		SparkSession spark = SparkSession.builder().appName(simConf.getAppName()).getOrCreate();
		Dataset<String> fileData;
		ParticleDataset pd;
		spark.sparkContext().setCheckpointDir(simConf.getCheckpointDir());
		//spark.conf().set("spark.sql.streaming.checkpointLocation", simConf.getCheckpointDir());
		simConf.print();
		
		InputSource[0] = simConf.getInputDir(); 
		
		fileData = spark.read().textFile(InputSource);
		pd = new ParticleDataset(fileData);
		
		for(int i = 0; i < simConf.getStepNumber(); i++){
			pd.step();
			
			if((i % simConf.getCheckpointInterval()) == 0)
				pd.checkpoint();

			pd.output(i, simConf.getOutputDir());
			
			System.out.println("\n\nSTEP " + i + " HAS BEEN COMPLETED \n\n");
		}

		spark.stop();
	}
}