/*
 * Particles are the building blocks of the simulation. 
 * They are a collection of vectors, whose job it is to keep track of location, velocity, acceleration, and a species.
 * 
 */

import java.io.Serializable;

public class Particle implements Serializable{
	
	private static final long serialVersionUID = 2L;
	private double locX, locY;
	private double velX, velY;
	private double accX, accY;
	private int species;
	
	
	//Empty constructor for Spark, attributes get set using the setters and getter by Spark Automatically
	public Particle() {

	}
	public Particle(double locX, double locY, double velX, double velY, double accX, double accY, int species) {
		this.locX = locX;
		this.locY = locY;
		this.velX = velX;
		this.velY = velY;
		this.accX = accX;
		this.accY = accY;
		this.species = species;
	}
	
	//Add the acceleration to the velocity, and the velocity to the acceleration
	public void step() {

		velX += accX;
		velY += accY;
		
		locX += velX;
		locY += velY;
	}
	
	//Set acceleration to zero, used at the beginning of each step
	public void resetAcc() {
		accX = 0;
		accY = 0;
	}
	
	//Add to the acceleration
	public void applyForce(double x, double y) {
		accX += x;
		accY += y;
	}
	public void applyForceX(double force) {
		accX += force;
	}
	public void applyForceY(double force) {
		accY += force;
	}
	public double getLocX() {
		return locX;
	}

	public void setLocX(double locX) {
		this.locX = locX;
	}

	public double getLocY() {
		return locY;
	}

	public void setLocY(double locY) {
		this.locY = locY;
	}

	public double getVelX() {
		return velX;
	}

	public void setVelX(double velX) {
		this.velX = velX;
	}

	public double getVelY() {
		return velY;
	}

	public void setVelY(double velY) {
		this.velY = velY;
	}

	public double getAccX() {
		return accX;
	}

	public void setAccX(double accX) {
		this.accX = accX;
	}

	public double getAccY() {
		return accY;
	}

	public void setAccY(double accY) {
		this.accY = accY;
	}
	public int getSpecies() {
		return species;
	}
	public void setSpecies(int species) {
		this.species = species;
	}
	public boolean sameSpecies(Particle p) {
		return p.getSpecies() == this.species;
	}
	@Override
	public String toString() {
		return (locX + " " + locY + " " + velX + " " + velY + " " + accX + " " + accY + " " + species);
	}	
}
