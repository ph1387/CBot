package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * ScoreGeneratorDecreaseNormal.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} decreasing the score (Normal) until
 * the minimum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorDecreaseNormal extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorDecreaseNormal(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_NORMAL, ConfigRate.MAX_DECREASE);
	}

	// -------------------- Functions
	
}
