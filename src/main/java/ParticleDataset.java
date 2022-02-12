import java.util.List;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;

public class ParticleDataset {

	final static int X =0, Y= 1;

	Dataset<Particle> particles;	
		
	public ParticleDataset(Dataset<String> source) {
		
		particles = source.map((MapFunction<String, Particle>) f -> {
			String[] fields = f.split(" ");
			return new Particle(Double.parseDouble(fields[0]), Double.parseDouble(fields[1]), 0,0,0,0);
		}, Encoders.bean(Particle.class)).cache();
	}
	public void show() {
		particles.show();
	}
	public void step() {
		
		List<Particle> p = particles.collectAsList();
		
		particles = particles.map((MapFunction<Particle, Particle>) (particle)->{
			
			particle.resetAcc();
			
			p.forEach((elem) ->{
				double x = particle.getLocX() - elem.getLocX();
				double y = particle.getLocY() - elem.getLocY();
				
				if(x != 0)
					particle.applyForceX(x/(x*x));
				if(y != 0)
					particle.applyForceY(y/(y*y));
			});
			
			particle.step();
			return particle;
		}, Encoders.bean(Particle.class));
		
	}
}