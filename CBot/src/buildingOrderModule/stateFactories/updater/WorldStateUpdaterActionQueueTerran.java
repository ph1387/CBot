package buildingOrderModule.stateFactories.updater;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.worldStates.ManagerWorldStateActionQueueTerran;

// TODO: UML SUPERCLASS
/**
 * WorldStateUpdaterActionQueueTerran.java --- Updater for updating a
 * {@link ManagerWorldStateActionQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class WorldStateUpdaterActionQueueTerran extends WorldStateUpdaterActionQueueSimulationResults {

	// TODO: UML REMOVE
//	private boolean simuationAllowed = false;

	public WorldStateUpdaterActionQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	// TODO: UML REMOVE
//	@Override
//	public void update(BuildActionManager manager) {
//		super.update(manager);
//
//		// Allow the use of the Simulator after a certain amount of time.
//		if (!this.simuationAllowed
//				&& Core.getInstance().getGame().getFrameCount() >= this.startingBuildingOrderNeededFrameMax) {
//			this.changeWorldStateEffect("simulationAllowed", true);
//
//			// Disable any other actions that are relying on the
//			// ManagerBaseAction since only the simulation results do matter.
//			// The Bot is not allowed to queue any other actions.
//			this.changeWorldStateEffect("managerBaseActionsAllowed", false);
//
//			this.simuationAllowed = true;
//		}
//	}
}
