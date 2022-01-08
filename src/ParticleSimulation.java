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
		private long numberOfParticles;
		@Override
	    public void setup(Context context) throws IOException, InterruptedException {
			conf = context.getConfiguration();
			numberOfParticles = conf.getLong("particleNumber", 0);
		}
		
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String[] tokens = value.toString().split(" ");

			for(int i = 0; i < numberOfParticles; i++) {
				ParticleWritable p = new ParticleWritable(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]));
				p.setHead(i == Integer.parseInt(tokens[0]));

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
			double xAcc = 0;
			double yAcc = 0;
					
			for(ParticleWritable part : values ){
				xAcc += part.getXLoc();
				yAcc += part.getYLoc();

				if (part.isHead())
					head = new ParticleWritable(part);
			}
			
			head.setXAcc((xAcc -(numberOfParticles * head.getXLoc())) / (numberOfParticles - 1));
			head.setYAcc((yAcc - (numberOfParticles * head.getYLoc())) / (numberOfParticles - 1));
			
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
