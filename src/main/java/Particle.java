import java.io.Serializable;

public class Particle implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private double locX, locY;
	private double velX, velY;
	private double accX, accY;
	
	public Particle(double locX, double locY, double velX, double velY, double accX, double accY) {
		this.locX = locX;
		this.locY = locY;
		this.velX = velX;
		this.velY = velY;
		this.accX = accX;
		this.accY = accY;
	}
	
	public void step() {
		velX += accX;
		velY += accY;
	
		locX += velX;
		locY += velY;
	}
	public void resetAcc() {
		accX =0;
		accY =0;
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

	@Override
	public String toString() {
		return locX + " " + locY + " " + velX + " " + velY + " " + accX + " " + accY;
	}
	
	
}
