package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * ScoringDirectorTerranDefault.java --- A ScoringDirector providing the machine
 * version of the Terran {@link ScoreGeneratorFactory}.
 * 
 * @author P H - 17.07.2017
 *
 */
public class ScoringDirectorTerranMachines extends ScoringDirector {

	// -------------------- Functions

	public ScoringDirectorTerranMachines(BuildActionManager manager) {
		super(manager);
	}

	@Override
	protected ScoreGeneratorFactory defineScoreGeneratorFactory(BuildActionManager manager) {
		return new ScoreGeneratorFactoryTerranMachines(manager);
	}

	// ------------------------------ Getter / Setter

}
