package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorFixed_Force.java --- A {@link ScoreGenerator} returning a
 * score-divider combination in such a way that the {@link GameState}s using
 * this {@link ScoreGenerator} will return a high multiplier.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorFixed_Force extends ScoreGeneratorFixed {

	// The value can be chosen as desired. It should not be set to
	// Double.MAX_VALUE since the scores are added together in the final
	// calculation and then divided by the sum of all dividers.
	private static double DefaultScore = 1000;
	private static int DefaultDivider = 1;

	public ScoreGeneratorFixed_Force(BuildActionManager manager) {
		super(manager, DefaultScore, DefaultDivider);
	}

	// -------------------- Functions

}
