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
	
	private double damage;
	
	private ParticleStat libido;
	private ParticleStat age;
	private ParticleStat health;
	private ParticleStat energy;
	
	private boolean dead;
		
	//Empty constructor for Spark, attributes get set using the setters and getter by Spark Automatically.
	public Particle() {
	
	}
	
	public Particle( Vector2D location, Vector2D velocity, Vector2D acceleration, int species, double attractionMultiplier, double repulsionMultiplier, double forceMultiplier, double libido, double age, double health, double damage, double energy) {
		//Generate a unique ID for this particle. IDs are based on the current Unix timestamp since epoch time, the current VM's uptime and a random number.
		this.id = Long.valueOf(System.nanoTime()).toString();
		id += Long.valueOf(System.currentTimeMillis());
		id += Long.valueOf((long) (Math.random() * 1000000000));
		
		this.location = location;
		this.velocity = velocity;
		this.acceleration = acceleration;
		this.species = species;
		this.attractionMultiplier = attractionMultiplier;
		this.repulsionMultiplier = repulsionMultiplier;
		this.forceMultiplier = forceMultiplier;
		
		this.damage = damage;
		
		this.libido = new ParticleStat(libido);
		this.age = new ParticleStat(age);
		this.health = new ParticleStat(health, health);
		this.energy = new ParticleStat(energy, energy);
		

		
		this.age.setMax(age);
		this.age.setCurrent(0);
		
		this.health.setMax(health);
		this.health.setCurrent(health);
				
		dead = false;
	}


	//Add the acceleration to the velocity, and the velocity to the acceleration.
	public void step(double mult) {

		acceleration.mult(mult);
		velocity.add(acceleration);
		
		location.add(velocity);
		location.mod(1000, 1000);
		
		if(age.isFull() || health.isZero() || energy.isZero())
			dead = true;
		else {
			libido.increment();
			age.increment();
			health.increment();
			energy.decrement();
		}

	}

	public void calculateAttraction(Particle other) {
		Vector2D distance = Vector2D.sub(other.location, this.location);

		if(Math.abs(distance.getX()) > 500)
			distance.setX(distance.getX() - (1000 * Math.signum(distance.getX())));

		if(Math.abs(distance.getY()) > 500)
			distance.setY(distance.getY() - (1000 * Math.signum(distance.getY())));
		
		distance.mult(this.attractionMultiplier);

		applyForce(distance);
		
	}
	public void calculateRepulsion(Particle other) {
		Vector2D distance = Vector2D.sub(this.location, other.location);
		
		if(Math.abs(distance.getX()) > 500)
			distance.setX(distance.getX() - (1000 * Math.signum(distance.getX())));

		if(Math.abs(distance.getY()) > 500)
			distance.setY(distance.getY() - (1000 * Math.signum(distance.getY())));
	
		distance.mult(this.repulsionMultiplier * calculateStatMultipler(energy) * calculateStatMultipler(health));
		
		applyForce(distance);
	}
	
	//Add to the acceleration.
	public void applyForce(Vector2D force) {
		acceleration.add(force);
	}
	public void resetAcc() {
		acceleration.setX(0);
		acceleration.setY(0);
	}
	//Checks the reproductive criteria: Sufficient libido for both parents, same species, small distance and ensuring they are different particles (different ids).
	public boolean canReproduce(Particle other) {

		return (libido.isFull()) && 
				(other.libido.isFull()) && 
				(sameSpecies(other)) && 
				(this.getLocation().distSq(other.getLocation()) <= 5) && 
				(!this.isSame(other));
	}
	
	//Check if the two particles are the same species.
	public boolean sameSpecies(Particle p) {
		return p.getSpecies() == this.species;
	}

	//The reproduction algorithm. The location and multipliers are averaged. The species remains the same as the parents.
	//The velocity and acceleration are zero. New unique id is generated too.
	public static Particle reproduce(Particle p1, Particle p2, double variance) {
		
		//Calculate the location.
		Vector2D loc = Vector2D.add(p1.getLocation(), p2.getLocation());
		loc.div(2);

		//calculate the multipliers.
		double attractionMult = ((p1.getAttractionMultiplier() + p2.getAttractionMultiplier()) / 2) * (1 - (variance / 2) + Math.random() * variance);
		double repulsionMult = ((p1.getRepulsionMultiplier() + p2.getForceMultiplier()) / 2) * (1 - (variance / 2) + Math.random() * variance);
		double forceMult = ((p1.getForceMultiplier() + p2.getForceMultiplier()) / 2) * (1 - (variance / 2) + Math.random() * variance);
		int libido = (int) Math.round((p1.libido.getMax() + p2.libido.getMax()  / 2) * (1 - (variance / 2) + Math.random() * variance));
		double age = (p1.age.getMax() + p2.age.getMax() / 2) * (1 - (variance / 2) + Math.random() * variance);
		double health = ((p1.health.getMax() + p2.health.getMax()) / 2) * (1 - (variance / 2) + Math.random() * variance);
		double damage =  ((p1.getDamage() + p2.getDamage()) / 2) * (1 - (variance / 2) + Math.random() * variance);
		double energy = ((p1.getEnergy().getMax() + p2.getEnergy().getMax()) / 2) * (1 - (variance / 2) + Math.random() * variance);
		
		//Make the libido zero.
//		p1.getLibido().reset();
//		p2.getLibido().reset();
		
		return new Particle(loc,
				new Vector2D(),
				new Vector2D(),
				p1.getSpecies(),
				attractionMult, repulsionMult,
				forceMult, libido, age, health, damage, energy);
	}
	public boolean canAttack(Particle other) {
		return this.getLocation().distSq(other.getLocation()) <= 5; 
	}
	public void attack(Particle other) {
		other.getHealth().sub(damage);
		this.getEnergy().fill();
	}
	public double calculateStatMultipler(ParticleStat stat) {
		double percentage = stat.percentage();
	
		if(percentage < 0.5)
			percentage = (1 - percentage) * -1;
		
		return percentage * 10;	
	}
	public boolean isSame(Particle other) {
		//If two particles have the same id, they are the same particle.
		return this.id.equals(other.getId());
	}
	//Standard getters and setters for all attributes. Needed by Spark.

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public double getDamage() {
		return damage;
	}

	public void setDamage(double damage) {
		this.damage = damage;
	}

	public ParticleStat getLibido() {
		return libido;
	}

	public void setLibido(ParticleStat libido) {
		this.libido = libido;
	}

	public ParticleStat getAge() {
		return age;
	}

	public void setAge(ParticleStat age) {
		this.age = age;
	}

	public ParticleStat getHealth() {
		return health;
	}

	public void setHealth(ParticleStat health) {
		this.health = health;
	}

	public ParticleStat getEnergy() {
		return energy;
	}

	public void setEnergy(ParticleStat energy) {
		this.energy = energy;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
