package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

//TODO: UML ADD
/**
 * ScoreGeneratorIncreaseVeryFast.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} increasing the score (Very fast)
 * until the maximum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorIncreaseVeryFast extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorIncreaseVeryFast(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_VERY_FAST, ConfigRate.MAX_INCREASE);
	}

	// -------------------- Functions

}