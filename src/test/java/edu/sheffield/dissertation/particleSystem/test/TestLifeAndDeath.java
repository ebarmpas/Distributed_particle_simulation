package edu.sheffield.dissertation.particleSystem.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.sheffield.dissertation.particleSystem.Agent;
import edu.sheffield.dissertation.particleSystem.Vector2D;

public class TestLifeAndDeath {
	
	
	@Test
	public void testReproduction(){
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 2, 1, 2, 100, 100, 200, 100, 200, 100);
		Agent a2 = new Agent(new Vector2D(600, 600), new Vector2D(), new Vector2D(), 0, 1, 2, 1, 100, 200, 100, 200, 100, 200);			
		
		double attractionMult = a1.getAttractionMultiplier();
		double repulsionMult = a2.getRepulsionMultiplier();
		double forceMult = a1.getForceMultiplier();
		double age = a2.getAge().getMax();
		double health =a1.getHealth().getMax();
		double damage =  a2.getDamage();
		double energy = a1.getEnergy().getMax();
		double visionRange = a2.getVisionRange();
		double libido = a1.getLibido().getMax();
		
		Agent actual = Agent.reproduce(a1, a2);
		Agent expected = new Agent(new Vector2D(550, 550), new Vector2D(), new Vector2D(), a1.getSpecies(),
				attractionMult, repulsionMult, forceMult, libido, age, health, damage, energy, visionRange);
		
		//Normally, you want these two be different, but they are set as the same for the test so that JUnit won't throw an error when normally there is on error
		expected.setId("0");
		actual.setId("0");
		
		assertEquals(expected.toString(), actual.toString());
		System.out.println("TestReproduction");
			
	}
	
	@Test 
	public void testBattle() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(600, 600), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 50, 100, 100);	
		
		a2.getEnergy().setCurrent(20);
		a2.attack(a1);
		
		assertEquals(50, a1.getHealth().getCurrent());
		assertEquals(true, a1.getEnergy().isFull());
		
		System.out.println("TestBattle");
	}
}