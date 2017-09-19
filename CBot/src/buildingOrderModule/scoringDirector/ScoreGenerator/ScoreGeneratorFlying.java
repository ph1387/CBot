package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML ADD
/**
 * ScoreGeneratorFlying.java --- A {@link ScoreGenerator} regarding flying
 * Units.
 * 
 * @author P H - 19.09.2017
 *
 */
public class ScoreGeneratorFlying extends ScoreGeneratorGradualChangeTarget {

	private static double DefaultRate = 0.1;
	private static double DefaultFrameDiff = 5000;
	private static double DefaultTargetValue = 1.;

	public ScoreGeneratorFlying(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultTargetValue);
	}

	// -------------------- Functions

}
