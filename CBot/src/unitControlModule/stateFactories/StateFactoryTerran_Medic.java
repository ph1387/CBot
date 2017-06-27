package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_Medic;
import unitControlModule.stateFactories.goals.UnitGoalStateProtect;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_Medic;
import unitControlModule.stateFactories.updater.GoalStateUpdaterProtect;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterAbilityUsingUnitsTerran_Medic;
import unitControlModule.stateFactories.worldStates.UnitWorldStateAbilityUsingUnitsTerran_Medic;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * StateFactoryTerran_Medic.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_Medic.
 * 
 * @author P H - 27.06.2017
 *
 */
public class StateFactoryTerran_Medic implements StateFactory {

	// -------------------- Functions

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateAbilityUsingUnitsTerran_Medic();
	}

	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateProtect();
	}

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_Medic();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterAbilityUsingUnitsTerran_Medic(playerUnit);
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterProtect(playerUnit);
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_Medic(playerUnit);
	}
}
