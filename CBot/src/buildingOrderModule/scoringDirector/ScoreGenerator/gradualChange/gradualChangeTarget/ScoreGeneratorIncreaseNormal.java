package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;

//TODO: UML ADD
/**
 * ScoreGeneratorIncreaseNormal.java --- A
 * {@link ScoreGeneratorGradualChangeTarget} increasing the score (Normal) until
 * the maximum is reached.
 * 
 * @author P H - 01.10.2017
 *
 */
public class ScoreGeneratorIncreaseNormal extends ScoreGeneratorGradualChangeTarget {

	public ScoreGeneratorIncreaseNormal(BuildActionManager manager) {
		super(manager, ConfigRate.RATE, ConfigRate.FRAME_DIFF_NORMAL, ConfigRate.MAX_INCREASE);
	}

	// -------------------- Functions

}
