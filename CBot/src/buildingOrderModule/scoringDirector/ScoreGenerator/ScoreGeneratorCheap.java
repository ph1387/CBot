package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * ScoreGeneratorCheap.java --- A {@link ScoreGenerator} regarding cheap Units.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorCheap extends ScoreGeneratorGradualChangeTarget {

	private static double DefaultRate = 0.1;
	private static double DefaultFrameDiff = 1000;
	private static double DefaultTargetValue = 1.;

	public ScoreGeneratorCheap(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultTargetValue);
	}

	// -------------------- Functions

}
