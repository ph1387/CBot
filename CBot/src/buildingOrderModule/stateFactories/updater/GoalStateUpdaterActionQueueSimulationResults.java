package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueSimulationResults;
import core.Core;

/**
 * GoalStateUpdaterActionQueueSimulationResults.java --- Updater for updating a
 * {@link ManagerGoalStateActionQueueSimulationResults} instance.
 * 
 * @author P H - 02.09.2017
 *
 */
public class GoalStateUpdaterActionQueueSimulationResults extends GoalStateUpdaterActionQueue {

	// The goal of the manager after a certain amount of time has passed.
	private boolean mainGoalSimulationRunning = false;

	public GoalStateUpdaterActionQueueSimulationResults(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager buildActionManager) {
		super.update(buildActionManager);

		// Force the execution of the simulation after a certain amount of time
		// has passed.
		if (!this.mainGoalSimulationRunning
				&& Core.getInstance().getGame().getFrameCount() >= this.simulationFrameInit) {
			// Swap the simulation goal with the initial starting goal.
			this.changeGoalStateImportance("simulationRunning", 2);

			this.mainGoalSimulationRunning = true;
		}
	}

}
