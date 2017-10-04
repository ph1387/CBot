package buildingOrderModule.scoringDirector.ScoreGenerator.fixed;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorFixed_Forbid.java --- A {@link ScoreGenerator} returning a
 * score-divider combination in such a way that the {@link GameState}s using
 * this {@link ScoreGenerator} will return a very low / null multiplier.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorFixed_Forbid extends ScoreGeneratorFixed {

	private static double DefaultScore = 0;
	// The value can be chosen as desired. It should not be set to
	// Integer.MAX_VALUE since the dividers are added together in the final
	// calculation.
	private static int DefaultDivider = 1000;

	public ScoreGeneratorFixed_Forbid(BuildActionManager manager) {
		super(manager, DefaultScore, DefaultDivider);
	}

	// -------------------- Functions

}
