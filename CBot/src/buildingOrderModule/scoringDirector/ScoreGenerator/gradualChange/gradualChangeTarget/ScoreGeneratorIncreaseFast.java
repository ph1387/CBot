package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * ScoreGeneratorIncreaseFast.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} increasing the score (Fast) until
 * the maximum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorIncreaseFast extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorIncreaseFast(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_FAST, ConfigRate.MAX_INCREASE);
	}

	// -------------------- Functions

}