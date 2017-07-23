package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueTerran;
import core.Core;

// TODO: UML ADD
/**
 * GoalStateUpdaterActionQueueTerran.java --- Updater for updating a
 * {@link ManagerGoalStateActionQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class GoalStateUpdaterActionQueueTerran extends GoalStateUpdaterActionQueue {

	// The goal of the manager after a certain amount of time has passed.
	private boolean mainGoalSimulationRunning = false;

	public GoalStateUpdaterActionQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

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
