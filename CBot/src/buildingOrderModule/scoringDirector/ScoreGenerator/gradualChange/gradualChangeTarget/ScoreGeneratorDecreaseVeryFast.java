package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML ADD
/**
 * ScoreGeneratorDecreaseVeryFast.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} decreasing the score (Very fast)
 * until the minimum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorDecreaseVeryFast extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorDecreaseVeryFast(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_VERY_FAST, ConfigRate.MAX_DECREASE);
	}

	// -------------------- Functions

}
