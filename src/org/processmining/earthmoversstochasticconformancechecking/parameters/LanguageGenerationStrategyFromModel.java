package org.processmining.earthmoversstochasticconformancechecking.parameters;

public interface LanguageGenerationStrategyFromModel extends Cloneable {

	public void initialise();
	
	public boolean isTerminated(double massCovered, long traces);
	
	public LanguageGenerationStrategyFromModel clone();

}
