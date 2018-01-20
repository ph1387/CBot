package buildingOrderModule.stateFactories;

import java.util.LinkedHashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import buildingOrderModule.stateFactories.updater.ActionUpdaterSimulationQueueTerranBio;
import buildingOrderModule.stateFactories.updater.Updater;
import javaGOAP.GoapAction;

/**
 * StateFactoryTerranBio.java --- Terran StateFactory containing values and
 * actions for a Terran-Bio configuration.
 * 
 * @author P H - 18.11.2017
 *
 */
public class StateFactoryTerranBio extends StateFactoryTerranDefault {

	// -------------------- Functions

	@Override
	public LinkedHashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsSimulationQueueTerran();
	}

	@Override
	public Updater getMatchingActionUpdater(BuildActionManager manager) {
		return new ActionUpdaterSimulationQueueTerranBio(manager);
	}

}
