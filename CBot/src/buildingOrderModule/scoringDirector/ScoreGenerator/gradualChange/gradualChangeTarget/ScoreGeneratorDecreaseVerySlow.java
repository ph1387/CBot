package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML ADD
/**
 * ScoreGeneratorDecreaseVerySlow.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} decreasing the score (Very slow)
 * until the minimum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorDecreaseVerySlow extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorDecreaseVerySlow(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_VERY_SLOW, ConfigRate.MAX_DECREASE);
	}

	// -------------------- Functions

}
