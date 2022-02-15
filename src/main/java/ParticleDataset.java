import java.io.IOException;
import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;

public class ParticleDataset {

	final static int X =0, Y= 1, SPECIES = 6;

	Dataset<Particle> particles;	
	
	public ParticleDataset(Dataset<String> source) {
		
		particles = source.map((MapFunction<String, Particle>) f -> {
			String[] fields = f.split(" ");

			return new Particle(Double.parseDouble(fields[X]), Double.parseDouble(fields[Y]), 0,0,0,0, Integer.parseInt(fields[SPECIES]));
		}, Encoders.bean(Particle.class)).cache();
	}
	public void show() {
		particles.show();
//		particles.describe().show();
	}
	public void step() {
		
		List<Particle> p = particles.collectAsList();
		
		particles = particles.map((MapFunction<Particle, Particle>) (particle)->{
			
			particle.resetAcc();
			
			p.forEach((elem) ->{
				double x = elem.getLocX() - particle.getLocX();
				double y = elem.getLocY() - particle.getLocY();
				double xForce = 0, yForce = 0;

				if(x != 0)
					xForce = x/(x*x);
				if(y != 0)
					yForce = y/(y*y);
				
				if(!(particle.sameSpecies(elem))) {
					
					xForce *= -1;
					yForce *= -1;
				}
				
				particle.applyForce(xForce, yForce);
			});
			return particle;
		}, Encoders.bean(Particle.class));
		
		particles = particles.map((MapFunction<Particle, Particle>) (particle) -> {
			particle.step();
			return particle;
		}, Encoders.bean(Particle.class));
		
	}
	public void output(int step, String outputPath) throws IOException {
		particles.write().csv(outputPath + "/steps/step" + step);
	}
}