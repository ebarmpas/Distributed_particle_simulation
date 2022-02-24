import java.io.Serializable;
import java.util.Objects;

public class Vector2D implements Comparable<Vector2D>, Serializable{

	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	
	
	public Vector2D() {
		super();
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2D(Vector2D v) {
		super();
		this.x = v.getX();
		this.y = v.getY();
	}
	
	public Vector2D(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public void add(Vector2D v) {
		x += v.getX();
		y += v.getY();
	}
	
	public void add(double x, double y) {
		this.x += x;
		this.y += y;
	}
	
	public void sub(Vector2D v) {
		x -= v.getX();
		y -= v.getY();
	}
	
	public void sub(double x, double y) {
		this.x += x;
		this.y += y;
	}
	public void mod(int w, int h) {
		x %= w;
		y %= h;
	}
	public void mult(double c) {
		x *= c;
		y *= c;
	}
	
	public void div(double c) {
		if(c != 0) {
			x *= c;
			y *= c;
		}	
	}

	public double mag() {
		return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
	}
	
	public double magSq() {
		return Math.pow(x,2) + Math.pow(y,2);
	}
	public void limit(double lim) {
		if(x > lim)
			x = lim;
		else if(x < lim * -1)
			x = lim * -1;
		
		if(y > lim)
			y = lim;
		else if(y < lim * -1)
			y = lim * -1;
	}
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Vector2D other = (Vector2D) obj;
		return Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
				&& Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
	}
	
	@Override
	public int compareTo(Vector2D o) {
		
		double sum0 = x+y;
		double sum1 = o.getX() + o.getY();
		
		if(sum0 > sum1)
			return 1;
		else if(sum0 < sum1)
			return -1;
		else
			return 0;
	}
	
	@Override
	public String toString() {
		return x + " " + y;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public static Vector2D add(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.getX() + v2.getX(), v1.getY() + v2.getY());
	}
	
	public static Vector2D sub(Vector2D v1, Vector2D v2) {
		return new Vector2D(v1.getX() - v2.getX(), v1.getY() - v2.getY());
	}
	
	public static Vector2D mult(double c, Vector2D v) {
		return new Vector2D(c * v.getX(), c * v.getY());
	}
	
	public static Vector2D div(Vector2D v, double c) {
		if(c == 0)
			c = 1;
		return new Vector2D(v.getX() / c, v.getY() / c);
	}
}