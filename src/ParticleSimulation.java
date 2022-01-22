import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ParticleSimulation {

	public static class StepMapper extends Mapper<LongWritable, Text, LongWritable, ParticleWritable> {
		
		private Configuration conf;
		private final static int KEY = 0, VALUE = 1;
		private final static int SPECIES = 0, LOCATION_X = 1, LOCATION_Y = 2, VELOCITY_X = 3, VELOCITY_Y = 4;//, ACCELERATION_X = 5, ACCELERATION_Y = 6;
		private long numberOfParticles;
		
		@Override
	    public void setup(Context context) throws IOException, InterruptedException {
			conf = context.getConfiguration();
			numberOfParticles = conf.getLong("particleNumber", 0);
		}
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			String[] keyvalue = value.toString().split("\t");
			String[] vectorAttributes = keyvalue[VALUE].split(" ");
						
			for(long i = 0; i < numberOfParticles; i++) {
				ParticleWritable p = new ParticleWritable(Integer.parseInt(vectorAttributes[SPECIES]), new Vector2D(Double.parseDouble(vectorAttributes[LOCATION_X]), Double.parseDouble(vectorAttributes[LOCATION_Y])), new Vector2D(Double.parseDouble(vectorAttributes[VELOCITY_X]), Double.parseDouble(vectorAttributes[VELOCITY_Y])));
				p.setHead(i == Integer.parseInt(keyvalue[KEY]));

				context.write(new LongWritable(i), p);
			}
		}
	}
	
	public static class StepReducer extends Reducer<LongWritable, ParticleWritable, LongWritable, ParticleWritable> {

		@Override
		public void reduce(LongWritable key, Iterable<ParticleWritable> values, Context context) throws IOException, InterruptedException {
		
			ParticleWritable head = null;
			
			Vector2D cohesionAcceleration = new Vector2D(0, 0), repulsionAcceleration = new Vector2D(0, 0);
			
			HashMap<Integer, Vector2D> speciesAccelerations = new HashMap<>();
			HashMap<Integer, Integer> speciesPopulation = new HashMap<>();
			
			int sameSpeciesPopulation = 0, differentSpeciesPopulation = 0;
					
			for(ParticleWritable part : values ){
				//Check whether this specific particle's species is in the hash map. If not, initialize with with 0,0
				if(speciesAccelerations.get(part.getSpecies()) == null) 
					speciesAccelerations.put(part.getSpecies(), new Vector2D(0,0));		
				
				//Check whether this specific particle's species is in the hash map. If not, initialize with with 0
				if(speciesPopulation.get(part.getSpecies()) == null) 
					speciesPopulation.put(part.getSpecies(), 0);		
				
				//Add the acceleration of this particle to the acceleration of the particles of this species.
				speciesAccelerations.put(part.getSpecies(), (Vector2D.add(speciesAccelerations.get(part.getSpecies()),part.getLocation())));
				//Increment the amount of particles in this species
				speciesPopulation.put(part.getSpecies(), speciesPopulation.get(part.getSpecies()) + 1);
				
				//Check if the current particle is the head.
				if (part.isHead()) 
					head = new ParticleWritable(part);
			}
			//Go through the hash map and add up all the values depending on whether they are the same species as the head or not. Also increment the species-segregated population count
			for (Entry<Integer, Vector2D> entry : speciesAccelerations.entrySet()) {
				
			    if(entry.getKey() == head.getSpecies()) {
			    	cohesionAcceleration.add(entry.getValue());
			    	sameSpeciesPopulation += speciesPopulation.get(entry.getKey());
			    }
			    else {
			    	repulsionAcceleration.add(entry.getValue());
			    	differentSpeciesPopulation += speciesPopulation.get(entry.getKey());
			    }
			}
			
			//Calculate and apply the cohesion force. Formula : Sum all vectors of all eligible particles. Multiply the location of the head by the number of the eligible particles. Subtract the multiplied location of the head and the vector sum of the eligible particles. Divide the result by the number of eligible particles minus 1
			if(sameSpeciesPopulation > 0)
				head.applyForce(Vector2D.div(Vector2D.sub(speciesAccelerations.get(head.getSpecies()), Vector2D.mult(sameSpeciesPopulation, head.getLocation())), sameSpeciesPopulation -1 ));
			
			//Calculate and apply the repulsion force. Formula : Sum all vectors of all eligible particles. Multiply the location of the head by the number of the eligible particles. Subtract and the vector sum of the eligible particles and the multiplied location of the head. Divide the result by the number of eligible particles minus 1
			if(differentSpeciesPopulation > 0)
				head.applyForce(Vector2D.div(Vector2D.sub(Vector2D.mult(differentSpeciesPopulation, head.getLocation()), speciesAccelerations.get(head.getSpecies())), differentSpeciesPopulation -1 ));
			
			head.step();
			context.write(key, head);
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		long stepParticleNumber = Long.parseLong(args[1]);
			
		System.out.println("Number of steps : " + args[2]);		
		
		for(int i = 0; i < Integer.parseInt(args[2]); i++) {
			Configuration conf = new Configuration();
			Job job = Job.getInstance(conf);
			
			job.setJobName("ParticleSimulation");
			
			job.setJarByClass(ParticleSimulation.class);
			
			job.getConfiguration().setLong("particleNumber", stepParticleNumber);
			
			job.setMapperClass(StepMapper.class);
			job.setReducerClass(StepReducer.class);
			
			job.setOutputKeyClass(LongWritable.class);
			job.setOutputValueClass(ParticleWritable.class);
			
			FileInputFormat.addInputPath(job, new Path(args[0] + i));
			FileOutputFormat.setOutputPath(job, new Path(args[0] + (i + 1)));
			
			System.out.println("Step " + (i + 1));
			System.out.println("Number of particles : " + stepParticleNumber);	
			
			job.waitForCompletion(true);
			
			stepParticleNumber = job.getCounters().findCounter("org.apache.hadoop.mapreduce.TaskCounter","MAP_INPUT_RECORDS").getValue();
		}
		
		System.exit(0);
	}
}

/*
 *  MAPPER : Take the input, and disseminate it to the reducer after changing the Text to ParticleWritable and usable IDs.
 * REDUCER : Calculate forces (Cohesion, repulsion, heading), as well as things about its life (Procreation/evolution, death). Output to text
*/

/*
 * inputPath0, outputPath0 MapReduce0 : input = inputPath0, output = outputPath0
 * MapReduce1 : input = outputPath0, output = outputPath1 MapReduce2 : input =
 * outputPath1, output = outputPath2 ... ... MapReduceN : input = inputPathN-1,
 * output = outputPathN
 */
