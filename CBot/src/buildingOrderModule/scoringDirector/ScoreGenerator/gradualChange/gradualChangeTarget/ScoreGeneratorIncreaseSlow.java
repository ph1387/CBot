package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * ScoreGeneratorIncreaseSlow.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} increasing the score (Slow) until
 * the maximum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorIncreaseSlow extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorIncreaseSlow(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_SLOW, ConfigRate.MAX_INCREASE);
	}

	// -------------------- Functions

}