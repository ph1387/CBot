package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * ScoreGeneratorDecreaseSlow.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} decreasing the score (Slow) until
 * the minimum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorDecreaseSlow extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorDecreaseSlow(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_SLOW, ConfigRate.MAX_DECREASE);
	}

	// -------------------- Functions
	
}
