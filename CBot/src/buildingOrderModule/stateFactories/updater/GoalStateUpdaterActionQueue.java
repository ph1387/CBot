package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueue;
import core.Core;

// TODO: UML ADD
/**
 * GoalStateUpdaterActionQueue.java --- Updater for updating a
 * {@link ManagerGoalStateActionQueue} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class GoalStateUpdaterActionQueue extends GoalStateUpdaterDefault {

	// The amount of frames that must pass before the simulation is forced to
	// start.
	protected int simulationFrameInit = 1000;
	// The initial goal of the manager.
	private boolean mainGoalStartingBuildOrderRunning = true;

	public GoalStateUpdaterActionQueue(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

		// Remove the initial goal of using a starting action after a certain
		// amount of time has passed. Therefore no more actions are being used
		// that use the starting build order as their effect.
		if (this.mainGoalStartingBuildOrderRunning
				&& Core.getInstance().getGame().getFrameCount() >= this.simulationFrameInit) {
			this.changeGoalStateImportance("startingBuildOrderNeeded", 0);

			this.mainGoalStartingBuildOrderRunning = false;
		}
	}

}
