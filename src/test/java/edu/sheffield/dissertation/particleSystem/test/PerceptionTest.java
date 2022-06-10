package edu.sheffield.dissertation.particleSystem.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import edu.sheffield.dissertation.particleSystem.Agent;
import edu.sheffield.dissertation.particleSystem.Vector2D;

public class PerceptionTest {
	
	@Test
	public void testGeneralVisionInRange() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(520, 520), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
		
		assertEquals(true, a1.canSee(a2));
		System.out.println("testGeneralVisionInRange");
	}
	
	@Test
	public void testGeneralVisionOutOfRange() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(800, 800), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
		
		assertEquals(false, a1.canSee(a2));
		System.out.println("testGeneralVisionOutOfRange");
	}
	
	@Test
	public void testBattleVisionInRange() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(501, 501), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100);
		
		assertEquals(true, a1.canAttack(a2));
		System.out.println("testBattleVisionInRange");
	}
	
	@Test
	public void testBattleVisionOutOfRange() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 1, 1, 1, 1, 100, 100, 100, 100, 100);
		Agent a2 = new Agent(new Vector2D(530, 530), new Vector2D(), new Vector2D(), 0, 1, 1, 1, 100, 100, 100, 100, 100);
		
		assertEquals(false, a1.canAttack(a2));
		System.out.println("testBattleVisionOutOfRange");
	}
	
	@Test
	public void testCanReproduce() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 2, 1, 2, 100, 100, 200, 100, 100);
		Agent a2 = new Agent(new Vector2D(501, 501), new Vector2D(), new Vector2D(), 0, 1, 2, 1, 100, 200, 100, 200, 200);	
		
		a1.getLibido().fill();
		a2.getLibido().fill();
		
		assertEquals(true, a1.canReproduce(a2));
		System.out.println("testCanReproduce");
	}
	
	@Test
	public void testCannotReproduceRange() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 2, 1, 2, 100, 100, 200, 100, 100);
		Agent a2 = new Agent(new Vector2D(510, 510), new Vector2D(), new Vector2D(), 0, 1, 2, 1, 100, 200, 100, 200, 200);	
		
		a1.getLibido().fill();
		a2.getLibido().fill();
		
		assertEquals(false, a1.canReproduce(a2));
		System.out.println("testCannotReproduceRange");
	}
	
	@Test
	public void testCannotReproduceLibido() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 2, 1, 2, 100, 100, 200, 100, 100);
		Agent a2 = new Agent(new Vector2D(501, 501), new Vector2D(), new Vector2D(), 0, 1, 2, 1, 100, 200, 100, 200, 200);	
		
		a1.getLibido().empty();
		a2.getLibido().empty();
		
		assertEquals(false, a1.canReproduce(a2));
		System.out.println("testCannotReproduceLibido");
	}
	
	@Test
	public void testCannotReproduceSpecies() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 1, 2, 1, 2, 100, 100, 200, 100, 100);
		Agent a2 = new Agent(new Vector2D(501, 501), new Vector2D(), new Vector2D(), 0, 1, 2, 1, 100, 200, 100, 200, 200);	
		
		a1.getLibido().fill();
		a2.getLibido().fill();
		
		assertEquals(false, a1.canReproduce(a2));
		System.out.println("testCannotReproduceSpecies");
	}
	
	@Test
	public void testCannotReproduceSame() {
		Agent a1 = new Agent(new Vector2D(500, 500), new Vector2D(), new Vector2D(), 0, 2, 1, 2, 100, 100, 200, 100, 100);
		Agent a2 = new Agent(new Vector2D(501, 501), new Vector2D(), new Vector2D(), 0, 1, 2, 1, 100, 200, 100, 200, 200);	
		
		a1.getLibido().fill();
		a2.getLibido().fill();
		
		a1.setId("0");
		a2.setId("0");
		
		assertEquals(false, a1.canReproduce(a2));
		System.out.println("testCannotReproduceSame");
	}
	
}
