package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_SiegeTank;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_SiegeTank;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterAbilityUsingUnitsTerran_SiegeTank;
import unitControlModule.stateFactories.worldStates.UnitWorldStateAbilityUsingUnitsTerran_SiegeTank;
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

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateAbilityUsingUnitsTerran_SiegeTank();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterAbilityUsingUnitsTerran_SiegeTank(playerUnit);
	}
}
