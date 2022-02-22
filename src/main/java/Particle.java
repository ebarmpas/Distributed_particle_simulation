/*
 * Particles are the building blocks of the simulation. 
 * They are a collection of vectors, whose job it is to keep track of location, velocity, acceleration, and a species.
 * 
 */

import java.io.Serializable;

public class Particle implements Serializable{
	
	private static final long serialVersionUID = 3L;
	private Vector2D location;
	private Vector2D velocity;
	private Vector2D acceleration;
	private int species;
	
	
	//Empty constructor for Spark, attributes get set using the setters and getter by Spark Automatically
	public Particle() {

	}

	
	public Particle(Vector2D location, Vector2D velocity, Vector2D acceleration, int species) {
		super();
		this.location = location;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.species = species;
	}


	//Add the acceleration to the velocity, and the velocity to the acceleration
	public void step() {

		velocity.add(acceleration);
		location.add(velocity);
	}
	
	//Set acceleration to zero, used at the beginning of each step
	public void resetAcc() {
		acceleration.setX(0);
		acceleration.setY(0);
	}
	
	//Add to the acceleration
	public void applyForce(Vector2D force) {
		acceleration.add(force);
	}
	
	
	public boolean sameSpecies(Particle p) {
		return p.getSpecies() == this.species;
	}
	@Override
	public String toString() {
		return (location.getX() + " " + location.getY() + " " + velocity.getX() + " " + velocity.getY() + " " + acceleration.getX() + " " + acceleration.getY() + " " + species);
	}

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
}
