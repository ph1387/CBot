package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_Marine;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_Marine;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * StateFactoryTerran_Marine.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_Marine.
 * @author P H - 23.06.2017
 *
 */
public class StateFactoryTerran_Marine extends StateFactoryDefault {
	
	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_Marine();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_Marine(playerUnit);
	}
}
