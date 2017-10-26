package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_Goliath;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_Goliath;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryTerran_Goliath.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_Goliath.
 * 
 * @author P H - 22.09.2017
 *
 */
public class StateFactoryTerran_Goliath extends StateFactoryDefault {

	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_Goliath();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_Goliath(playerUnit);
	}
}
