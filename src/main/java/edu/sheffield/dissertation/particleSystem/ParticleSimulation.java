/*
 * Main driver class for the Particle Simulation.
 * Its job is to simply setup the Spark environment and call methods.
 */
package edu.sheffield.dissertation.particleSystem;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;

public class ParticleSimulation {
	
	public static void main(String[] args) throws IOException {
		//simConf holds execution information.
		SimulationConfiguration simConf = new SimulationConfiguration(new File(args[0]));
		//Main Spark Object.
		SparkSession spark = SparkSession.builder().appName(simConf.getAppName()).getOrCreate();
		//Main business logic object.
		ParticleDataset pd;
		//Accumulates all new particles per step in the slave nodes so that they can be collected in the master node and joined to the particle dataset.
		ParticleAccumulator newParticles = new ParticleAccumulator();
		
		//Print the configuration.
		simConf.print();
		
		//Set the checkpoint directory.
		spark.sparkContext().setCheckpointDir(simConf.getCheckpointDir());
		
	
		//Register the accumulator to spark.
		spark.sparkContext().register(newParticles, "NewParticles");
		
		//Instantiate the business logic.
		pd = new ParticleDataset(spark.read().textFile(simConf.getInputDir()), simConf);
			
		//Main event loop. On each step, calculate the new position and velocity of the particles, checkpoint them if needed, and then output the result .
		for(int i = 0; i < simConf.getStepNumber(); i++){
			
			//Step the simulation.
			pd.step(newParticles);
			
			//After the step is done, collect all the new particles into a dataset so they can be joined.
			pd.addNewParticles(spark.createDataset(newParticles.value(), Encoders.bean(Particle.class)));

			//Check the checkpoint interval and checkpoint if needed.
			if((i % simConf.getCheckpointInterval()) == 0)
				pd.checkpoint();

			//Output the particles into a file.
			pd.output(i, simConf.getOutputDir());
			
			//Reset the accumulator for the next step.
			newParticles.reset();
			
			System.out.println("\n\nSTEP " + i + " HAS BEEN COMPLETED \n\n");
		}

		spark.stop();
	}
}