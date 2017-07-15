package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueTerran;
import core.Core;

// TODO: UML ADD
/**
 * WorldStateUpdaterActionQueueTerran.java --- Updater for updating a
 * {@link ManagerGoalStateActionQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class WorldStateUpdaterActionQueueTerran extends WorldStateUpdaterActionQueue {

	private boolean simuationAllowed = false;

	public WorldStateUpdaterActionQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		super.update(manager);

		// Allow the use of the Simulator after a certain amount of time.
		if (!this.simuationAllowed
				&& Core.getInstance().getGame().getFrameCount() >= this.startingBuildingOrderNeededFrameMax) {
			this.changeWorldStateEffect("simulationAllowed", true);

			this.simuationAllowed = true;
		}
	}
}
