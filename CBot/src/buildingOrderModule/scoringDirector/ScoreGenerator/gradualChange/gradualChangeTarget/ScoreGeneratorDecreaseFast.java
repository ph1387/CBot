package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML ADD
/**
 * ScoreGeneratorDecreaseFast.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} decreasing the score (Fast) until
 * the minimum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorDecreaseFast extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorDecreaseFast(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_FAST, ConfigRate.MAX_DECREASE);
	}

	// -------------------- Functions

}
