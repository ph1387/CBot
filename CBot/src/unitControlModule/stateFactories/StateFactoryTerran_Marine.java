package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_Marine;
import unitControlModule.stateFactories.goals.UnitGoalStateTerran_Marine;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_Marine;
import unitControlModule.stateFactories.updater.GoalStateUpdaterTerran_Marine;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterAbilityUsingUnitsTerran_Marine;
import unitControlModule.stateFactories.worldStates.UnitWorldStateAbilityUsingUnitsTerran_Marine;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryTerran_Marine.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_Marine.
 * 
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

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateAbilityUsingUnitsTerran_Marine();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterAbilityUsingUnitsTerran_Marine(playerUnit);
	}
	
	// TODO: UML ADD
	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateTerran_Marine();
	}
	
	// TODO: UML ADD
	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterTerran_Marine(playerUnit);
	}
}
