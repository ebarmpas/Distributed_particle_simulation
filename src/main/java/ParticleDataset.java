import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.ReduceFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;

public class ParticleDataset {
	
	final static int X =0, Y= 1;

	Dataset<Particle> particles;	
		
	public ParticleDataset(Dataset<String> source) {
		
		particles = source.map((MapFunction<String, Particle>) f -> {
			String[] fields = f.split(" ");
			return new Particle(Double.parseDouble(fields[0]), Double.parseDouble(fields[1]), 0,0,0,0);
		}, Encoders.bean(Particle.class));
	
	}
	public void show() {
		
		particles.show();
	}
	public void step() {
		
		Particle superParticle = particles.reduce((ReduceFunction<Particle>) (p1, p2) -> new Particle( p1.getLocX() + p2.getLocX(),p1.getLocY() + p2.getLocY(),0,0,0,0));
		System.out.println("\n\nSUPER PARTICLE : " + superParticle +"\n");
		long len = particles.count();
		
		particles = particles.map((MapFunction<Particle, Particle>) (particle)->{		
			particle.resetAcc();			
			if(len > 1) {
				particle.applyForceX(((superParticle.getLocX() - (len * particle.getLocX()))/ (len -1)));
				particle.applyForceY(((superParticle.getLocY() - (len * particle.getLocY())) / (len -1)));
			}
			particle.step();
			return particle;
		}, Encoders.bean(Particle.class));
		
	}
}
