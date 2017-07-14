package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueTerran;

// TODO: UML ADD
/**
 * GoalStateUpdaterActionQueueTerran.java --- Updater for updating a
 * {@link ManagerGoalStateActionQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class GoalStateUpdaterActionQueueTerran extends GoalStateUpdaterActionQueue {

	public GoalStateUpdaterActionQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

		// TODO: WIP ADD UPDATER

	}

}
