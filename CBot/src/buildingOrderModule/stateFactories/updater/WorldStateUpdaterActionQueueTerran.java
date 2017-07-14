package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueTerran;

// TODO: UML ADD
/**
 * WorldStateUpdaterActionQueueTerran.java --- Updater for updating a
 * {@link ManagerGoalStateActionQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class WorldStateUpdaterActionQueueTerran extends WorldStateUpdaterActionQueue {

	public WorldStateUpdaterActionQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

		// TODO: ADD UPDATER

	}
}
