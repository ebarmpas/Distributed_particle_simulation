package edu.sheffield.dissertation.particleSystem.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.sheffield.dissertation.particleSystem.Agent;
import edu.sheffield.dissertation.particleSystem.Vector2D;
import org.junit.jupiter.api.Test;

public class RepulsionTests {
	
	@Test
	public void testRepulsionNoWrapAround() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(600, 600), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
			
		a1.calculateRepulsion(a2);
		a1.step(1);

		assertEquals(new Vector2D(400, 400), a1.getLocation());
		System.out.println("testRepulsionNoWrapAroundFull");
	}
	
	@Test
	public void testRepulsionWrapAround() {
		Agent a1 = new Agent(new Vector2D(900, 900), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(100 , 100), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
				
		a1.calculateRepulsion(a2);
		a1.step(1);	

		assertEquals(new Vector2D(700, 700), a1.getLocation() );
		System.out.println("testRepulsionWrapAroundFull");
	}
	
}
