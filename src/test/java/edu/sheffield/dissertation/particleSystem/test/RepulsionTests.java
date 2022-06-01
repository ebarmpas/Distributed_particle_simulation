package edu.sheffield.dissertation.particleSystem.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.sheffield.dissertation.particleSystem.Agent;
import edu.sheffield.dissertation.particleSystem.Vector2D;
import org.junit.jupiter.api.Test;

public class RepulsionTests {
	
	@Test
	public void testRepulsionNoWrapAroundFull() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(600, 600), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		
		a1.getEnergy().fill();
		
		a1.calculateRepulsion(a2);
		a1.step(1);

		assertEquals(new Vector2D(400, 400), a1.getLocation());
		System.out.println("testRepulsionNoWrapAroundFull");
	}
	
	@Test
	public void testRepulsionWrapAroundFull() {
		Agent a1 = new Agent(new Vector2D(900, 900), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(100 , 100), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		
		a1.getEnergy().fill();
		
		a1.calculateRepulsion(a2);
		a1.step(1);	

		assertEquals(new Vector2D(700, 700), a1.getLocation() );
		System.out.println("testRepulsionWrapAroundFull");
	}
	
	
	
	@Test
	public void testRepulsionNoWrapAroundHalf() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(600, 600), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		
		a1.getEnergy().setCurrent(50);

		a1.calculateRepulsion(a2);
		a1.step(1);

		assertEquals(new Vector2D(450, 450), a1.getLocation());
		System.out.println("testRepulsionNoWrapAroundHalf");
	}
	
	@Test
	public void testRepulsionWrapAroundHalf() {
		Agent a1 = new Agent(new Vector2D(900, 900), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(100 , 100), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		
		a1.getEnergy().setCurrent(50);
				
		a1.calculateRepulsion(a2);
		a1.step(1);	

		assertEquals(new Vector2D(800, 800), a1.getLocation() );
		System.out.println("testRepulsionWrapAroundHalf");
	}
	@Test
	public void testRepulsionNoWrapBelowHalf() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(600, 600), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		
		a1.getEnergy().setCurrent(25);

		a1.calculateRepulsion(a2);
		a1.step(1);

		assertEquals(new Vector2D(575, 575), a1.getLocation());
		System.out.println("testRepulsionNoWrapBelowHalf");
	}
	
	@Test
	public void testRepulsionWrapBelowHalf() {
		Agent a1 = new Agent(new Vector2D(900, 900), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(100 , 100), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		
		a1.getEnergy().setCurrent(25);
				
		a1.calculateRepulsion(a2);
		a1.step(1);	

		assertEquals(new Vector2D(50, 50), a1.getLocation() );
		System.out.println("testRepulsionWrapBelowHalf");
	}
}
