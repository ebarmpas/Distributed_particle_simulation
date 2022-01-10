import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class Step {
	private Configuration conf;
	private Job job;
	
	public Step(String jobName) throws IOException {
		conf = new Configuration();
		job = Job.getInstance(conf, jobName);
	}
	public void setJarByClass(Class <?> cls) {
		job.setJarByClass(cls);
	}
	
	public void setLong(String key, long value) {
		job.getConfiguration().setLong(key, value);
	}
	
	public void setMapperClass(Class <? extends Mapper> cls) {
		job.setMapperClass(cls);
	}
	public void setReducerClass(Class <? extends Reducer> cls) {
		job.setReducerClass(cls);
	}
	public void setOutputKeyClass(Class <?> theClass) {
		job.setOutputKeyClass(theClass);
	}
	public void setOutputValueClass(Class <?> theClass) {
		job.setOutputValueClass(theClass);
	}
	public Job getJob() {
		return job;
	}
}
