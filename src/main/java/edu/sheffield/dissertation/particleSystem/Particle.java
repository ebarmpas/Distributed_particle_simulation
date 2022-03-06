/*
 * Particles are the building blocks of the simulation. 
 * They are a collection of vectors, whose job it is to keep track of location, velocity, acceleration,
 * as well a species and a bunch of parameters that affect the behavior of the particle,  plus a unique identifier.
 */
package edu.sheffield.dissertation.particleSystem;
import java.io.Serializable;

public class Particle implements Serializable{
	
	private static final long serialVersionUID = 3L;
	private String id;
	private Vector2D location;
	private Vector2D velocity;
	private Vector2D acceleration;
	private int species;
	private double attractionMultiplier;
	private double repulsionMultiplier;
	private double forceMultiplier;
	private int maxLibido;
	private int currentLibido;
	
	//Empty constructor for Spark, attributes get set using the setters and getter by Spark Automatically.
	public Particle() {
	
	}
	
	public Particle(String id, Vector2D location, Vector2D velocity, Vector2D acceleration, int species, double attractionMultiplier, double repulsionMultiplier, double forceMultiplier, int maxLibido) {
		this.id = id;
		this.location = location;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.species = species;
		this.attractionMultiplier = attractionMultiplier;
		this.repulsionMultiplier = repulsionMultiplier;
		this.forceMultiplier = forceMultiplier;
		this.maxLibido = maxLibido;
		currentLibido = 0;
	}



	//Add the acceleration to the velocity, and the velocity to the acceleration.
	public void step(int width, int height) {

		velocity.add(acceleration);
		location.add(velocity);
		location.mod(width, height);
		
		currentLibido++;
	}
	
	//Set acceleration to zero, used at the beginning of each step.
	public void resetAcc() {
		acceleration.setX(0);
		acceleration.setY(0);
	}
	
	//Add to the acceleration.
	public void applyForce(Vector2D force) {
		acceleration.add(force);
	}
	
	//Checks the reproductive criteria: Sufficient libido for both parents, same species, small distance and ensuring they are different particles (different ids).
	public boolean canReproduce(Particle other) {

		return (this.currentLibido >= this.maxLibido) && 
				(other.getCurrentLibido() >= other.getMaxLibido()) && 
				(sameSpecies(other)) && 
				(this.location.distSq(other.getLocation()) <= 5) && 
				(!this.isSame(other));
	}
	
	//Check if the two particles are the same species.
	public boolean sameSpecies(Particle p) {
		return p.getSpecies() == this.species;
	}

	//The reproduction algorithm. The location and multipliers are averaged. The species remains the same as the parents.
	//The velocity and acceleration are zero. New unique id is generated too.
	public static Particle reproduce(Particle p1, Particle p2) {
		
		//Calculate the location.
		Vector2D loc = Vector2D.add(p1.getLocation(), p2.getLocation());
		loc.div(2);
		
		//calculate the multipliers.
		double attractionMult = (p1.getAttractionMultiplier() + p2.getAttractionMultiplier()) / 2;
		double repulsionMult = (p1.getRepulsionMultiplier() + p2.getForceMultiplier()) / 2;
		double forceMult = (p1.getForceMultiplier() + p2.getForceMultiplier()) / 2;
		int maxLib = Math.round((p1.getMaxLibido() + p2.getMaxLibido()) / 2);
		
		//Make the libido zero.
		p1.setCurrentLibido(0);
		p2.setCurrentLibido(0);
		
		//Create a new unique id for the new particle.
		String sid = Long.valueOf(System.nanoTime()).toString();
		sid += Long.valueOf(System.currentTimeMillis());
		sid += Long.valueOf((long) (Math.random() * 1000000000));
		
		return new Particle(sid,
				loc,
				new Vector2D(),
				new Vector2D(),
				p1.getSpecies(),
				attractionMult, repulsionMult,
				forceMult, maxLib);
	}

	
	@Override
	public String toString() {
		return location + " " + velocity + " " + acceleration + " " + species + " " + attractionMultiplier + " "
				+ repulsionMultiplier + " " + forceMultiplier + " " + maxLibido + " " + currentLibido + " " + id;
	}
	
	public boolean isSame(Particle other) {
		//If two particles have the same id, they are the same particle.
		return this.id.equals(other.getId());
	}

	//Standard getters and setters for all attributes. Needed by Spark.
	public Vector2D getLocation() {
		return location;
	}

	public void setLocation(Vector2D location) {
		this.location = location;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public Vector2D getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2D acceleration) {
		this.acceleration = acceleration;
	}

	public int getSpecies() {
		return species;
	}

	public void setSpecies(int species) {
		this.species = species;
	}

	public double getAttractionMultiplier() {
		return attractionMultiplier;
	}

	public void setAttractionMultiplier(double attractionMultiplier) {
		this.attractionMultiplier = attractionMultiplier;
	}

	public double getRepulsionMultiplier() {
		return repulsionMultiplier;
	}

	public void setRepulsionMultiplier(double repulsionMultiplier) {
		this.repulsionMultiplier = repulsionMultiplier;
	}

	public double getForceMultiplier() {
		return forceMultiplier;
	}

	public void setForceMultiplier(double forceMultiplier) {
		this.forceMultiplier = forceMultiplier;
	}

	public int getMaxLibido() {
		return maxLibido;
	}
	
	public void setMaxLibido(int maxLibido) {
		this.maxLibido = maxLibido;
	}	
	
	public int getCurrentLibido() {
		return currentLibido;
	}

	public void setCurrentLibido(int currentLibido) {
		this.currentLibido = currentLibido;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
