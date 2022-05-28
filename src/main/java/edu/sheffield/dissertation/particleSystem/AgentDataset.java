/*
 * Main business logic class
 * It contains all the logic for simulation
 */
package edu.sheffield.dissertation.particleSystem;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
//import java.util.Properties;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
//import org.apache.spark.sql.SaveMode;

public class AgentDataset implements Serializable{
	
	private static final long serialVersionUID = 1L;
	//Holds all the particles for the simulation.
	private Dataset<Agent> agents;
	private SimulationConfiguration simConf;
//	private Properties jdbcProperties;
	//Initialize the dataset from file. Takes the String Dataset that is passed and parses the Strings into numbers.
	public AgentDataset(Dataset<Agent> particles, SimulationConfiguration simConf) {
		this.agents = particles;
		this.simConf = simConf;
//		jdbcProperties = new Properties();
//		jdbcProperties.put("user", "root");
//		jdbcProperties.put("password", "1234");
//		jdbcProperties.put("driver", "org.mariadb.jdbc.Driver");
	}

	//Print the dataset; just used for debugging.
	public void show() {
		agents.show();
	}
	//Calculates the reproduction and movement of the particles by updating their position, velocity and acceleration.
	//Currently there are two forces at play: attraction, which happens between particles of the same species and repulsion, which happens between particles of different species.
	//Reproduction is done by comparing each particle with every other particle.
	public void step(AgentAccumulator newAgents) {
		List<Agent> p = agents.collectAsList();
		//Iterates through all the particles and transforms them.
		agents = agents.map((MapFunction<Agent, Agent>) (agent)->{

			agent.resetAcc();
			p.forEach((elem) ->{
				if(agent.canSee(elem))
					if(agent.sameSpecies(elem)) {
						if(agent.canReproduce(elem))
							newAgents.add(Agent.reproduce(elem, agent, simConf.getSpeciesVariance(agent.getSpecies())));
						agent.calculateAttraction(elem);
					}else {
						if(agent.canAttack(elem)) 
							agent.attack(elem);	
						agent.calculateRepulsion(elem);
					}
			}); 

			return agent;

		}, Encoders.bean(Agent.class));

		//Only return particles who are alive.
		agents = agents.filter((FilterFunction<Agent>) (agent) -> !agent.isDead());

		//Apply all the changes that were calculated previously. This is done separately to make sure everything is done uniformly.
		agents = agents.map((MapFunction<Agent, Agent>) (agent) -> {
			agent.step(simConf.getForceMultiplier());
			return agent;
		}, Encoders.bean(Agent.class));

	}

	public void addNewParticles(Dataset<Agent> np) {
		agents = agents.union(np);
		np.unpersist();
	}
	
	public void computeStatistics(int species, int step, String outputPath) {
		//{"summary":"mean","attractionMultiplier":"0.999749774457852","damage":"80.07986395730077","forceMultiplier":"1.003525433206758","id":"5.569975630427063E33","repulsionMultiplier":"1.004194110166721","species":"0.0","visionRange":"5.008222942758357"}
		agents.filter((FilterFunction<Agent>) (agent) -> agent.getSpecies() == species).select(
		"attractionMultiplier", "damage", "forceMultiplier", "repulsionMultiplier", "visionRange")
		.summary("mean", "count").write().json(outputPath + "/stats/step" + step + "/species/" + species);
		
	}
	
	//Checkpoint the dataset. This has two purposes, out of which we are interested in the latter : local backup, and truncating the logical plan (ie force the lazy evaluations to happen).
	//This is very important to save RAM and improve performance. Without it, the program crashes due to a stack overflow error.

	public void checkpoint() {
		agents = agents.localCheckpoint(true);
	}
	public void coalesce(int partitionNumber) {
		agents = agents.coalesce(partitionNumber);
	}

	//Write the current state of the dataset onto a file.
	public void outputDataset(int step, String outputPath) throws IOException {
		agents.select("location", "species").write().json(outputPath + "/steps/step" + step);
//		particles.write().json(outputPath + "/steps/step" + step);
		//jdbc:mariadb://localhost:3306/DB?user=root&password=myPassword
//		particles.select("dead").write().mode(SaveMode.Append).jdbc("jdbc:mariadb://localhost:3306/Particle_Simulation?user=root&password=1234", "Particles", jdbcProperties);
	}
	
	public long count() {
		return agents.count();
	}
}