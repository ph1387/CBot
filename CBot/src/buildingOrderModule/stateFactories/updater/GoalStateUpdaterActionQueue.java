package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueue;

// TODO: UML ADD
/**
 * GoalStateUpdaterActionQueue.java --- Updater for updating a
 * {@link ManagerGoalStateActionQueue} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class GoalStateUpdaterActionQueue extends GoalStateUpdaterDefault {

	public GoalStateUpdaterActionQueue(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

		// TODO: WIP ADD UPDATER

	}

}
