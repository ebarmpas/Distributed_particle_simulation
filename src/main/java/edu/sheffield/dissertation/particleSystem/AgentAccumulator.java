/*
 * holds agents in a list added from the worker nodes so that they can be collected as a list and added to the main agent Dataset.
 */

package edu.sheffield.dissertation.particleSystem;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.spark.util.AccumulatorV2;


public class AgentAccumulator extends AccumulatorV2<Agent, List<Agent>> {

	private static final long serialVersionUID = 1L;
	
	private List<Agent> outputList;

    public AgentAccumulator(){
        this.outputList = new CopyOnWriteArrayList<Agent>();
    }

    @Override
    public boolean isZero() {
        return outputList.size() == 0;
    }

    @Override
    public AccumulatorV2<Agent, List<Agent>> copy() {
        AgentAccumulator customAccumulatorCopy = new AgentAccumulator();
        customAccumulatorCopy.merge(this);
        return customAccumulatorCopy;
    }

    @Override
    public void reset() {
        this.outputList = new CopyOnWriteArrayList<Agent>();
    }

    @Override
    public void add(Agent v) {
    	outputList.add(v);
    }

    @Override
    public void merge(AccumulatorV2<Agent, List<Agent>> other) {
    	outputList.addAll(other.value());
    }

    @Override
    public List<Agent> value() {
        return this.outputList;
    }
}