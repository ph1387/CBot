package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.worldStates.ManagerWorldStateActionQueue;

// TODO: UML ADD
/**
 * WorldStateUpdaterActionQueue.java --- Updater for updating a
 * {@link ManagerWorldStateActionQueue} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class WorldStateUpdaterActionQueue extends WorldStateUpdaterDefault {

	public WorldStateUpdaterActionQueue(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

		// TODO: ADD UPDATER

	}

}
