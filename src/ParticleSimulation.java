import java.io.IOException;

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
			
			for(int i = 0; i < numberOfParticles; i++) {
				ParticleWritable p = new ParticleWritable(Integer.parseInt(vectorAttributes[SPECIES]), new Vector2D(Double.parseDouble(vectorAttributes[LOCATION_X]), Double.parseDouble(vectorAttributes[LOCATION_Y])), new Vector2D(Double.parseDouble(vectorAttributes[VELOCITY_X]), Double.parseDouble(vectorAttributes[VELOCITY_Y])));
				p.setHead(i == Integer.parseInt(keyvalue[KEY]));

				context.write(new LongWritable(i), p);
			}
		}
	}
	
	public static class StepReducer extends Reducer<LongWritable, ParticleWritable, LongWritable, ParticleWritable> {
		
		private Configuration conf;
		private long numberOfParticles;
		
		@Override
		public void setup(Context context) throws IOException, InterruptedException {
			conf = context.getConfiguration();
			numberOfParticles = conf.getLong("particleNumber", 0);
		}
		
		@Override
		public void reduce(LongWritable key, Iterable<ParticleWritable> values, Context context) throws IOException, InterruptedException {
			
			ParticleWritable head = null;
			Vector2D finalAcceleration, cohesionAcceleration = new Vector2D(0, 0);

			for(ParticleWritable part : values ){
				cohesionAcceleration.add(part.getLocation());
				
				if (part.isHead()) 
					head = new ParticleWritable(part);
			}
			
			finalAcceleration = Vector2D.div(Vector2D.sub(cohesionAcceleration, Vector2D.mult(numberOfParticles, head.getLocation())), numberOfParticles -1 );
			head.applyForce(finalAcceleration);
			
			head.step();
			context.write(key, head);
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		
		Job job = Job.getInstance(conf, "ParticleSimulation");
		job.setJarByClass(ParticleSimulation.class);
		
		job.getConfiguration().setLong("particleNumber", Long.parseLong(args[2]));
		
		job.setMapperClass(StepMapper.class);
		job.setReducerClass(StepReducer.class);
		
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(ParticleWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
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
