package unitControlModule.stateFactories;

import java.util.HashSet;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_Vulture;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_Vulture;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterAbilityUsingUnitsTerran_Vulture;
import unitControlModule.stateFactories.worldStates.UnitWorldStateAbilityUsingUnitsTerran_Vulture;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryTerran_Vulture.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_Vulture.
 * 
 * @author P H - 04.07.2017
 *
 */
public class StateFactoryTerran_Vulture extends StateFactoryDefault {

	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_Vulture();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_Vulture(playerUnit);
	}

	// TODO: UML ADD
	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateAbilityUsingUnitsTerran_Vulture();
	}

	// TODO: UML ADD
	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterAbilityUsingUnitsTerran_Vulture(playerUnit);
	}
}
