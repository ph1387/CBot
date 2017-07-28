package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.worldStates.ManagerWorldStateActionQueue;
import core.Core;

/**
 * WorldStateUpdaterActionQueue.java --- Updater for updating a
 * {@link ManagerWorldStateActionQueue} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class WorldStateUpdaterActionQueue extends WorldStateUpdaterDefault {

	private boolean startingBuildingOrderNeeded = true;
	protected int startingBuildingOrderNeededFrameMax = 1000;

	public WorldStateUpdaterActionQueue(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

		// Remove the need of a starting building order after a certain amount
		// of time has passed.
		if (this.startingBuildingOrderNeeded
				&& Core.getInstance().getGame().getFrameCount() >= startingBuildingOrderNeededFrameMax) {
			this.changeWorldStateEffect("startingBuildOrderNeeded", false);

			this.startingBuildingOrderNeeded = false;
		}
	}

}
