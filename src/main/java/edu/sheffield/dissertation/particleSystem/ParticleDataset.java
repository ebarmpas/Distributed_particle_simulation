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

	final static int X =0, Y= 1, SPECIES = 6;

	//Holds all the particles for the simulation.
	private Dataset<Particle> particles;	
	private SimulationConfiguration simConf; 
	//Initialize the dataset from file. Takes the String Dataset that is passed and parses the Strings into numbers.
	public ParticleDataset(Dataset<String> source, SimulationConfiguration sc) {
		
		//Map the Strings into particles.
		particles = source.map((MapFunction<String, Particle>) f -> {
			
			//Split the line into fields that can be parsed and passed to the particle constructor.
			String[] fields = f.split(" ");
			
			//Generate a unique ID for this particle. IDs are based on the current Unix timestamp since epoch time, the current VM's uptime and a random number.
			String sid = Long.valueOf(System.nanoTime()).toString();
			sid += Long.valueOf(System.currentTimeMillis());
			sid += Long.valueOf((long) (Math.random() * 1000000000));
			
			//Parse the fields into the appropriate data type to instantiate the Particle Object
			return new Particle(sid,
								new Vector2D(Double.parseDouble(fields[X]), Double.parseDouble(fields[Y])),
								new Vector2D(0, 0), 
								new Vector2D(0, 0), 
								Integer.parseInt(fields[SPECIES]), 
								1D, 1D, 1D, 5,5);
			
			
		}, Encoders.bean(Particle.class)).cache();
		
		//Delete the old String dataset after we are done.
		source.unpersist();
		
		simConf = sc;
	}
	
	//Print the dataset; just used for debugging.
	public void show() {
		particles.show();
	}
	//Calculates the reproduction and movement of the particles by updating their position, velocity and acceleration.
	//Currently there are two forces at play: attraction, which happens between particles of the same species and repulsion, which happens between particles of different species.
	//Reproduction is done by comparing each particle with every other particle.
	public void step(ParticleAccumulator newParticles) {
		double multiplier = simConf.getForceMultiplier();
		int width = simConf.getPlaneWidth();
		int height  = simConf.getPlaneHeight();
		
		List<Particle> p = particles.collectAsList();
		//Iterates through all the particles and transforms them.
		particles = particles.map((MapFunction<Particle, Particle>) (particle)->{
			
			//Reset the acceleration of the particle for this step.
			particle.resetAcc();
			
			//Check if the particle is dead, if not, iterate through each particle and calculate breeding and forces.
			if(!particle.isDead())
			p.forEach((elem) ->{
					
				//The two particles need to be of the same species, close enough (under a distance of 5) and not the same particle.
				if(particle.canReproduce(elem)) {
					//The velocity and acceleration of the particle is zero, but the species is the same as the parents' and the location is the average of its parents' .
					Vector2D loc = new Vector2D(particle.getLocation());
					loc.add(elem.getLocation());
					loc.div(2);
					newParticles.add(Particle.reproduce(particle, elem));
				}
				//Calculate the distance for x and y. 
				Vector2D distance = Vector2D.sub(elem.getLocation(), particle.getLocation());
				Vector2D force = new Vector2D();
					
				//Since the plane is a sphere, and points can be connected from two directions, we need to find the one that is closer and use that.
				if(Math.abs(distance.getX()) > (width - Math.abs(distance.getX()))) 
					if(Math.signum(distance.getX()) == 1)
						distance.setX(distance.getX() - width);
					else
						distance.setX(width + distance.getX());
					
				if(Math.abs(distance.getY()) > (height - Math.abs(distance.getY()))) 
					if(Math.signum(distance.getY()) == 1)
						distance.setY(distance.getY() - height);
					else
						distance.setY(distance.getY() + height);
					
				//If the distance is not zero, calculate the force.
				if(distance.getX() != 0)
					force.setX(distance.getX()/(distance.getX()*distance.getX()));
					
				if(distance.getY() != 0)
					force.setY(distance.getY()/(distance.getY()*distance.getY()));
	
						
				//Attraction and repulsion are opposites, so check whether the two particles are of a different species and invert the force if that's the case.
				if(!(particle.sameSpecies(elem))) 
					force.mult(-1);
	
				force.mult(multiplier);
				//Finally, apply the force and then proceed to the next particle.
				particle.applyForce(force);
			});
			
			return particle;
			
		}, Encoders.bean(Particle.class));
		
		//Only return particles who are alive.
		particles = particles.filter((FilterFunction<Particle>) (particle) -> !particle.isDead());
		
		//Apply all the changes that were calculated previously. This is done separately to make sure everything is done uniformly.
		particles = particles.map((MapFunction<Particle, Particle>) (particle) -> {
			particle.step(width, height);
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
		particles = particles.localCheckpoint(true);
	}
	
	//Write the current state of the dataset onto a file.
	public void output(int step, String outputPath) throws IOException {
		particles.write().json(outputPath + "/steps/step" + step);
	}
}