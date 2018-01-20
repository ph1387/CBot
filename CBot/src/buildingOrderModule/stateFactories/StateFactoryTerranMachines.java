package buildingOrderModule.stateFactories;

import java.util.LinkedHashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import buildingOrderModule.stateFactories.updater.ActionUpdaterSimulationQueueTerranMachines;
import buildingOrderModule.stateFactories.updater.Updater;
import javaGOAP.GoapAction;

/**
 * StateFactoryTerranMachines.java --- Terran StateFactory containing values and
 * actions for a Terran-Machine configuration.
 * 
 * @author P H - 18.11.2017
 *
 */
public class StateFactoryTerranMachines extends StateFactoryTerranDefault {

	// -------------------- Functions

	@Override
	public LinkedHashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsSimulationQueueTerran();
	}

	@Override
	public Updater getMatchingActionUpdater(BuildActionManager manager) {
		return new ActionUpdaterSimulationQueueTerranMachines(manager);
	}

}
