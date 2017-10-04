package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

//TODO: UML ADD
/**
 * ScoreGeneratorIncreaseVerySlow.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} increasing the score (Very slow)
 * until the maximum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorIncreaseVerySlow extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorIncreaseVerySlow(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_VERY_SLOW, ConfigRate.MAX_INCREASE);
	}

	// -------------------- Functions

}