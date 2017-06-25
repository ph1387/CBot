package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_SiegeTank_SiegeMode;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_SiegeTank_SiegeMode;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryTerran_SiegeTank_SiegeMode.java --- StateFactory used for
 * generating all necessary Objects for the Terran_SiegeTank_SiegeMode.
 * 
 * @author P H - 24.06.2017
 *
 */
public class StateFactoryTerran_SiegeTank_SiegeMode extends StateFactoryTerran_SiegeTank {

	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_SiegeTank_SiegeMode();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_SiegeTank_SiegeMode(playerUnit);
	}

}
