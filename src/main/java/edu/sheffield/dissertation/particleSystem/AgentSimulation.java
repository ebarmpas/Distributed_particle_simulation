/*
 * Main driver class for the Particle Simulation.
 * Its job is to simply setup the Spark environment and call methods.
 */
package edu.sheffield.dissertation.particleSystem;

import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Random;

public class AgentSimulation {

	public static void main(String[] args) throws IOException {
		//simConf holds execution information.
		SimulationConfiguration simConf = new SimulationConfiguration(new File(args[0]));
		//Main Spark Object.
		SparkSession spark = SparkSession.builder().appName(simConf.getAppName()).getOrCreate();
		//Main business logic object.
		AgentDataset ad;
		//Accumulates all new particles per step in the slave nodes so that they can be collected in the master node and joined to the particle dataset.
		AgentAccumulator newParticles = new AgentAccumulator();
		//Print the configuration.
		simConf.print();
		
		//Set the checkpoint directory.
		spark.sparkContext().setCheckpointDir(simConf.getCheckpointDir());
		
		//Register the accumulator to spark.
		spark.sparkContext().register(newParticles, "NewParticles");
		
		//Instantiate the business logic.
		{
			ArrayList<Agent> p;
			int numberOfParticles = 0; 
			Random r = new Random(simConf.getSeed());
			
			for(int i = 0; i < simConf.getSpeciesNumber(); i++)
				numberOfParticles += simConf.getSpeciesPopulation(i);
			
			p = new ArrayList<Agent>(numberOfParticles);
			
			for(int i = 0; i < simConf.getSpeciesNumber(); i++) {
				
				double variance = simConf.getSpeciesVariance(i);
				double attractionMultiplier = simConf.getSpeciesAttractionMultiplier(i);
				double repulsionMultiplier = simConf.getSpeciesRepulsionMultiplier(i);
				double forceMultiplier = simConf.getSpeciesForceMultiplier(i);
				double libido = simConf.getSpeciesMaxLibido(i);
				double age = simConf.getSpeciesMaxAge(i);
				double health = simConf.getSpeciesHealth(i);
				double attack = simConf.getSpeciesDamage(i);
				double energy = simConf.getSpeciesMaxEnergy(i);
				double visionRange = simConf.getSpeciesVisionRange(i);
				
				for(int j = 0; j < simConf.getSpeciesPopulation(i); j++) 
					p.add(new Agent(new Vector2D(r.nextDouble() * 1000, r.nextDouble() * 1000), 
						new Vector2D(), new Vector2D(), i,
						attractionMultiplier * (1 - (variance / 2) + r.nextDouble() * variance), 
						repulsionMultiplier * (1 - (variance / 2) + r.nextDouble() * variance), 
						forceMultiplier * (1 - (variance / 2) + r.nextDouble() * variance),
						libido * (1 - (variance / 2) + r.nextDouble() * variance),
						age * (1 - (variance / 2) + r.nextDouble() * variance),
						health * (1 - (variance / 2) + r.nextDouble() * variance),
						attack * (1 - (variance / 2) + r.nextDouble() * variance),
						energy * (1 - (variance / 2) + r.nextDouble() * variance),
						visionRange * (1 - (variance / 2) + r.nextDouble() * variance)));
			}
			ad = new AgentDataset(spark.createDataset(p, Encoders.bean(Agent.class)), simConf);
		}
		
		//Main event loop. On each step, calculate the new position and velocity of the particles, checkpoint them if needed, and then output the result .
		ad.outputDataset(0, simConf.getOutputDir());
		for(int i = 0; i < simConf.getSpeciesNumber(); i++)
			ad.computeStatistics(i, 0, simConf.getOutputDir());
		
		for(int i = 1; i < simConf.getStepNumber(); i++){
			
			//Step the simulation.
			ad.step(newParticles);
			
			//After the step is done, collect all the new particles into a dataset so they can be joined.
			ad.addNewParticles(spark.createDataset(newParticles.value(), Encoders.bean(Agent.class)));

			//Check the checkpoint interval and checkpoint if needed.
			if(i % simConf.getCheckpointInterval() == 0)
				ad.checkpoint();

			if(i % simConf.getPartitioningInterval() == 0)
				ad.coalesce(simConf.getPartitionNumber());
			
			//Output the particles into a file.
			ad.outputDataset(i, simConf.getOutputDir());
			
			//Reset the accumulator for the next step.
			
			for(int j = 0; j < simConf.getSpeciesNumber(); j++)
				ad.computeStatistics(j, i, simConf.getOutputDir());
			
			newParticles.reset();
			System.out.println("\n\n STEP " + i + " HAS BEEN COMPLETED\n\n");
		}

		spark.stop();
		
	}

}