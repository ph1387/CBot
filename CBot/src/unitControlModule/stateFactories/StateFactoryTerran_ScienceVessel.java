package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_ScienceVessel;
import unitControlModule.stateFactories.goals.UnitGoalStateProtect;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_ScienceVessel;
import unitControlModule.stateFactories.updater.GoalStateUpdaterProtect;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterAbilityUsingUnitsTerran_ScienceVessel;
import unitControlModule.stateFactories.worldStates.UnitWorldStateAbilityUsingUnitsTerran_ScienceVessel;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * StateFactoryTerran_ScienceVessel.java --- A StateFactory used for generating
 * all necessary Objects for the Terran_Science_Vessel.
 * 
 * @author P H - 22.09.2017
 *
 */
public class StateFactoryTerran_ScienceVessel implements StateFactory {

	// -------------------- Functions

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateAbilityUsingUnitsTerran_ScienceVessel();
	}

	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateProtect();
	}

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_ScienceVessel();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterAbilityUsingUnitsTerran_ScienceVessel(playerUnit);
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterProtect(playerUnit);
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_ScienceVessel(playerUnit);
	}
}
