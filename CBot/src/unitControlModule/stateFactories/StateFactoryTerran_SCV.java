package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_SCV;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_SCV;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;
// TODO: UML
/**
 * StateFactoryTerran_SCV.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_SCV.
 * 
 * @author P H - 25.03.2017
 *
 */
public class StateFactoryTerran_SCV extends StateFactoryWorker {

	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_SCV();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_SCV(playerUnit);
	}
}
