import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class ParticleWritable implements WritableComparable<ParticleWritable>, Writable, Comparable<ParticleWritable>{
	private int species;
	private Vector2D location, velocity, acceleration;
	private boolean isHead;

	//default constructor for Hadoop
	public ParticleWritable() {
		
		species = 0;
		
		location = new Vector2D(0, 0);
		velocity = new Vector2D(0, 0);
		acceleration = new Vector2D(0, 0);
		
		isHead = false;
	}

	public ParticleWritable(int species, Vector2D location, Vector2D velocity) {
		
		super();
		
		this.species = species;
		this.location = location;
		this.velocity = velocity;
		acceleration = new Vector2D(0, 0);
		
		isHead = false;
	}

	public ParticleWritable(ParticleWritable p) {
		
		species = p.species;
		
		location = new Vector2D(p.getLocation());
		velocity = new Vector2D(p.getVelocity());
		acceleration = new Vector2D(p.getAcceleration());
		
		isHead = p.isHead;
	}
	
	public void step() {
		velocity.add(acceleration);
		location.add(velocity);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(acceleration, isHead, location, velocity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParticleWritable other = (ParticleWritable) obj;
		return Objects.equals(acceleration, other.acceleration) && isHead == other.isHead
				&& Objects.equals(location, other.location) && Objects.equals(velocity, other.velocity);
	}

	@Override
	public int compareTo(ParticleWritable o) {
		return location.compareTo(o.getLocation());
	}

	@Override
	public void readFields(DataInput in) throws IOException {

		species = in.readInt();
		
		location.setX(in.readDouble());
		location.setY(in.readDouble());
		
		velocity.setX(in.readDouble());
		velocity.setY(in.readDouble());
		
		acceleration.setX(in.readDouble());
		acceleration.setY(in.readDouble());
		
		isHead = in.readBoolean();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		
		out.writeInt(species);
		
		out.writeDouble(location.getX());
		out.writeDouble(location.getY());

		out.writeDouble(velocity.getX());
		out.writeDouble(velocity.getY());

		out.writeDouble(acceleration.getX());
		out.writeDouble(acceleration.getY());
		
		out.writeBoolean(isHead);
	}
	
	public void applyForce(Vector2D v) {
		acceleration.add(v);
	}
	
	@Override
	public String toString() {
		return species + " " + location + " " + velocity + " " +acceleration;
	}

	public int getSpecies() {
		return species;
	}

	public void setSpecies(int species) {
		this.species = species;
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


	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}
}