/*
 * Main driver class for the Particle Simulation
 * Its job is to simply setup the Spark environment and call methods
 */

import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;

public class ParticleSimulation {
	
	public static void main(String[] args) throws IOException {
		//Helper objects; InputSource array to satisfy .textFile, and simConf holds execution information.
		String[] inputSource = new String[1];
		SimulationConfiguration simConf = new SimulationConfiguration(new File(args[0]));
		//Main Spark Object
		SparkSession spark = SparkSession.builder().appName(simConf.getAppName()).getOrCreate();
		//Main business logic object
		ParticleDataset pd;
		
		spark.sparkContext().setCheckpointDir(simConf.getCheckpointDir());
		simConf.print();
		
		inputSource[0] = simConf.getInputDir(); 
		
		pd = new ParticleDataset(spark.read().textFile(inputSource));
			
		//Main event loop. On each step, calculate the new position and velocity of the particles, checkpoint them if needed, and then output the result 
		for(int i = 0; i < simConf.getStepNumber(); i++){
			
			pd.step(simConf.getForceMultiplier(), simConf.getPlaneWidth(), simConf.getPlaneHeight());
			
			if((i % simConf.getCheckpointInterval()) == 0)
				pd.checkpoint();

			pd.output(i, simConf.getOutputDir());
			
			System.out.println("\n\nSTEP " + i + " HAS BEEN COMPLETED \n\n");
		}

		spark.stop();
	}
}