package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * ScoringDirectorTerran_Bio.java --- A ScoringDirector whose goal is to score
 * Bio Units and the corresponding upgrades / technologies.
 * 
 * @author P H - 17.07.2017
 *
 */
public class ScoringDirectorTerran_Bio extends ScoringDirector {

	// -------------------- Functions

	public ScoringDirectorTerran_Bio(BuildActionManager manager) {
		super(manager);
	}

	@Override
	protected ScoreGeneratorFactory defineScoreGeneratorFactory(BuildActionManager manager) {
		return new ScoreGeneratorFactoryTerran_Bio(manager);
	}

	// ------------------------------ Getter / Setter

}
