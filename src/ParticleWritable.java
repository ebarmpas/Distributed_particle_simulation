import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

public class ParticleWritable implements WritableComparable<ParticleWritable>, Writable, Comparable<ParticleWritable>{
	private double xLoc, yLoc, xVel, yVel, xAcc, yAcc;
	private boolean isHead;

	//default constructor for Hadoop
	public ParticleWritable() {
		xLoc = 0;
		yLoc = 0;
		xVel = 0;
		yVel = 0;
		xAcc = 0;
		yAcc = 0;
		isHead = false;
	}

	public ParticleWritable(double xLoc, double yLoc) {
		super();
		this.xLoc = xLoc;
		this.yLoc = yLoc;
		xVel = 0;
		yVel = 0;
		xAcc = 0;
		yAcc = 0;
		isHead = false;
	}
	
	public ParticleWritable(ParticleWritable p) {
		xLoc = p.getXLoc();
		yLoc = p.getYLoc();
		
		xVel = p.getXVel();
		yVel = p.getYVel();
		
		xAcc = p.getXAcc();
		yAcc = p.getYAcc();
		
		isHead = p.isHead();
	} 
	@Override
	public int hashCode() {
		return Objects.hash(xAcc, xLoc, xVel, yAcc, yLoc, yVel);
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
		return Double.doubleToLongBits(xAcc) == Double.doubleToLongBits(other.xAcc)
				&& Double.doubleToLongBits(xLoc) == Double.doubleToLongBits(other.xLoc)
				&& Double.doubleToLongBits(xVel) == Double.doubleToLongBits(other.xVel)
				&& Double.doubleToLongBits(yAcc) == Double.doubleToLongBits(other.yAcc)
				&& Double.doubleToLongBits(yLoc) == Double.doubleToLongBits(other.yLoc)
				&& Double.doubleToLongBits(yVel) == Double.doubleToLongBits(other.yVel);
	}


	@Override
	public String toString() {
		return xLoc + " " + yLoc + " " + xVel + " " + yVel + " "+ xAcc + " " + yAcc;
	}

	@Override
	public int compareTo(ParticleWritable o) {
		double pCoords = xLoc + yLoc, oCoords = o.getXLoc() + o.getYLoc();
		
		if(pCoords > oCoords)
			return 1;
		else if (pCoords < oCoords)
			return -1;

		return 0;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		xLoc = in.readDouble();
		yLoc = in.readDouble();

		xVel = in.readDouble();
		yVel = in.readDouble();

		xAcc = in.readDouble();
		yAcc = in.readDouble();
		
		isHead = in.readBoolean();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeDouble(xLoc);
		out.writeDouble(yLoc);

		out.writeDouble(xVel);
		out.writeDouble(yVel);

		out.writeDouble(xAcc);
		out.writeDouble(yAcc);
	
		out.writeBoolean(isHead);
	}

	public double getXLoc() {
		return xLoc;
	}

	public void setXLoc(double xLoc) {
		this.xLoc = xLoc;
	}

	public double getYLoc() {
		return yLoc;
	}

	public void setYLoc(double yLoc) {
		this.yLoc = yLoc;
	}

	public double getXVel() {
		return xVel;
	}

	public void setXVel(double xVel) {
		this.xVel = xVel;
	}

	public double getYVel() {
		return yVel;
	}

	public void setYVel(double yVel) {
		this.yVel = yVel;
	}

	public double getXAcc() {
		return xAcc;
	}

	public void setXAcc(double xAcc) {
		this.xAcc = xAcc;
	}

	public double getYAcc() {
		return yAcc;
	}

	public void setYAcc(double yAcc) {
		this.yAcc = yAcc;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}



}