package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_SiegeTank;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_SiegeTank;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SiegeTankStateFactory.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_SiegeTank.
 * 
 * @author P H - 25.03.2017
 *
 */
public class StateFactoryTerran_SiegeTank extends StateFactoryDefault {

	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_SiegeTank();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_SiegeTank(playerUnit);
	}
}
