package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

// TODO: UML RENAME ScoringDirectorTerran_Bio
/**
 * ScoringDirectorTerranDefault.java --- A ScoringDirector providing the default
 * Terran {@link ScoreGeneratorFactory}.
 * 
 * @author P H - 17.07.2017
 *
 */
public class ScoringDirectorTerranDefault extends ScoringDirector {

	// -------------------- Functions

	public ScoringDirectorTerranDefault(BuildActionManager manager) {
		super(manager);
	}

	@Override
	protected ScoreGeneratorFactory defineScoreGeneratorFactory(BuildActionManager manager) {
		return new ScoreGeneratorFactoryTerranDefault(manager);
	}

	// ------------------------------ Getter / Setter

}
