/*
 * holds particles in a list added from the worker nodes so that they can be collected as a list and added to the main particle dataset.
 */

package edu.sheffield.dissertation.particleSystem;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.spark.util.AccumulatorV2;


public class ParticleAccumulator extends AccumulatorV2<Particle, List<Particle>> {

	private static final long serialVersionUID = 1L;
	
	private List<Particle> outputList;

    public ParticleAccumulator(){
        this.outputList = new CopyOnWriteArrayList<Particle>();
    }

    @Override
    public boolean isZero() {
        return outputList.size() == 0;
    }

    @Override
    public AccumulatorV2<Particle, List<Particle>> copy() {
        ParticleAccumulator customAccumulatorCopy = new ParticleAccumulator();
        customAccumulatorCopy.merge(this);
        return customAccumulatorCopy;
    }

    @Override
    public void reset() {
        this.outputList = new CopyOnWriteArrayList<Particle>();
    }

    @Override
    public void add(Particle v) {
    	outputList.add(v);
    }

    @Override
    public void merge(AccumulatorV2<Particle, List<Particle>> other) {
    	outputList.addAll(other.value());
    }

    @Override
    public List<Particle> value() {
        return this.outputList;
    }
}