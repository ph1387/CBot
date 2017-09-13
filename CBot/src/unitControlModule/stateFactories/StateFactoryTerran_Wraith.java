package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_Wraith;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_Wraith;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryTerran_Wraith.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_Wraith.
 * 
 * @author P H - 13.09.2017
 *
 */
public class StateFactoryTerran_Wraith extends StateFactoryDefault {

	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_Wraith();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_Wraith(playerUnit);
	}

}
