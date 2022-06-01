package edu.sheffield.dissertation.particleSystem.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.sheffield.dissertation.particleSystem.Agent;
import edu.sheffield.dissertation.particleSystem.Vector2D;
import org.junit.jupiter.api.Test;

public class AttractionTests {

	@Test
	public void testAttractionNoWrapAround() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(600, 600), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 10);
		
		a1.calculateAttraction(a2);
		a1.step(1);
	
		assertEquals(a2.getLocation(), a1.getLocation());
		System.out.println("testAttractionNoWrapAround");
	}
	
	@Test
	public void testAttractionWrapAround() {
		Agent a1 = new Agent(new Vector2D(950, 950), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(40, 40), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		
		a1.calculateAttraction(a2);
		a1.step(1);	
		
		assertEquals(a2.getLocation(), a1.getLocation());
		System.out.println("testAttractionWrapAround");
		System.out.println(a1.getLocation().toString());
	}

}
