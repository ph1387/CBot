package buildingOrderModule.scoringDirector.ScoreGenerator.fixed;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;

/**
 * ScoreGeneratorFixed_One.java --- A {@link ScoreGenerator} returning one as
 * score.
 * 
 * @author P H - 15.09.2017
 *
 */
public class ScoreGeneratorFixed_One extends ScoreGeneratorFixed {

	private static double DefaultScore = 1;
	private static int DefaultDivider = 1;

	public ScoreGeneratorFixed_One(BuildActionManager manager) {
		super(manager, DefaultScore, DefaultDivider);
	}

	// -------------------- Functions

}
