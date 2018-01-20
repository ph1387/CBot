package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;

/**
 * ScoringDirectorTerranBio.java --- A {@link ScoringDirector} that changes the
 * scores of the Actions used in the {@link ActionQueueSimulationResults} for a
 * Terran-Bio configuration and therefore providing the bio version of the
 * Terran {@link ScoreGeneratorFactory}.
 * 
 * @author P H - 18.11.2017
 *
 */
public class ScoringDirectorTerranBio extends ScoringDirector {

	public ScoringDirectorTerranBio(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	protected ScoreGeneratorFactory defineScoreGeneratorFactory(BuildActionManager manager) {
		return new ScoreGeneratorFactoryTerranBio(manager);
	}

}
