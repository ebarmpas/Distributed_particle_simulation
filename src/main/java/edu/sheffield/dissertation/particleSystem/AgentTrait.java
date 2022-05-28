package edu.sheffield.dissertation.particleSystem;

import java.io.Serializable;
import java.util.Objects;

public class AgentTrait  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private double max;
	private double current;
	
	public AgentTrait(double max, double current){
		this.max = max;
		this.current = current;
	}
	public AgentTrait(double max) {
		this.max = max;
		this.current = 0;
	}
	public AgentTrait() {
		
	}
	public void increment() {
		if(!isFull())
			current++;
	}
	public void decrement() {
		if(!isZero())
			current--;
	}
	public void add(int add) {
		current += add;
	}
	public void sub(double damage) {
		current -= damage;
	}
	public void empty() {
		current = 0;
	}
	public void fill() {
		current = max;
	}
	public boolean isFull() {
		return current >= max;
	}
	public boolean isZero() {
		return current <= 0;
	}
	public double percentage() {
		return current / max;
	}
	@Override
	public String toString() {
		return max + " " + current;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(current, max);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AgentTrait other = (AgentTrait) obj;
		return Double.doubleToLongBits(current) == Double.doubleToLongBits(other.current)
				&& Double.doubleToLongBits(max) == Double.doubleToLongBits(other.max);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public double getMax() {
		return max;
	}
	public double getCurrent() {
		return current;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public void setCurrent(double current) {
		this.current = current;
	}

	
}
