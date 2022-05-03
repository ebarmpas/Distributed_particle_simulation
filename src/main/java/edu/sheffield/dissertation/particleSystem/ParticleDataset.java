/*
 * Main business logic class
 * It contains all the logic for simulation
 */
package edu.sheffield.dissertation.particleSystem;
import java.io.IOException;
import java.util.List;

import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;

public class ParticleDataset {
	
	//Holds all the particles for the simulation.
	private Dataset<Particle> particles;	
	//Initialize the dataset from file. Takes the String Dataset that is passed and parses the Strings into numbers.
	public ParticleDataset(Dataset<Particle> particles, SimulationConfiguration simConf) {
		this.particles = particles;
	}

	//Print the dataset; just used for debugging.
	public void show() {
		particles.show();
	}
	//Calculates the reproduction and movement of the particles by updating their position, velocity and acceleration.
	//Currently there are two forces at play: attraction, which happens between particles of the same species and repulsion, which happens between particles of different species.
	//Reproduction is done by comparing each particle with every other particle.
	public void step(ParticleAccumulator newParticles) {
		List<Particle> p = particles.collectAsList();
		//Iterates through all the particles and transforms them.
		particles = particles.map((MapFunction<Particle, Particle>) (particle)->{

			particle.resetAcc();
			p.forEach((elem) ->{
				if(particle.sameSpecies(elem) && !particle.isSame(elem)) {
					if(particle.canReproduce(elem))
						newParticles.add(Particle.reproduce(elem, particle));
					particle.calculateAttraction(elem);
				}else
					particle.calculateRepulsion(elem);
			}); 

			return particle;

		}, Encoders.bean(Particle.class));

		//Only return particles who are alive.
		particles = particles.filter((FilterFunction<Particle>) (particle) -> !particle.isDead());

		//Apply all the changes that were calculated previously. This is done separately to make sure everything is done uniformly.
		particles = particles.map((MapFunction<Particle, Particle>) (particle) -> {
			particle.step();
			return particle;
		}, Encoders.bean(Particle.class));

	}

	public void addNewParticles(Dataset<Particle> np) {
		particles = particles.union(np);

		np.unpersist();
	}
	//Checkpoint the dataset. This has two purposes, out of which we are interested in the latter : local backup, and truncating the logical plan (ie force the lazy evaluations to happen).
	//This is very important to save RAM and improve performance. Without it, the program crashes due to a stack overflow error.
	public void checkpoint() {
		particles = particles.coalesce(1);
		particles = particles.localCheckpoint(true);
	}

	//Write the current state of the dataset onto a file.
	public void output(int step, String outputPath) throws IOException {
		particles.write().json(outputPath + "/steps/step" + step);
	}
	
	public long count() {
		return particles.count();
	}
}