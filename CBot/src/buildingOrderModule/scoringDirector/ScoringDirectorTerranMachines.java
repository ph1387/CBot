package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;

/**
 * ScoringDirectorTerranDefault.java --- A {@link ScoringDirector} that changes
 * the scores of the Actions used in the {@link ActionQueueSimulationResults}
 * for a Terran-Machine configuration and therefore providing the machine
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
