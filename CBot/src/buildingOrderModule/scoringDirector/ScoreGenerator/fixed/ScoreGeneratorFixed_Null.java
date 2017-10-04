package buildingOrderModule.scoringDirector.ScoreGenerator.fixed;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;

/**
 * ScoreGeneratorFixed_Null.java --- A {@link ScoreGenerator} returning zero as
 * score.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorFixed_Null extends ScoreGeneratorFixed {

	private static double DefaultScore = 0;
	private static int DefaultDivider = 1;

	public ScoreGeneratorFixed_Null(BuildActionManager manager) {
		super(manager, DefaultScore, DefaultDivider);
	}

	// -------------------- Functions

}
