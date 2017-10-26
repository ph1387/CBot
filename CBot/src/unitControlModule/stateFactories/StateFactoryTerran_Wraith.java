package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_Wraith;
import unitControlModule.stateFactories.goals.UnitGoalStateStealth;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_Wraith;
import unitControlModule.stateFactories.updater.GoalStateUpdaterStealth;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterAbilityUsingUnitsTerran_Wraith;
import unitControlModule.stateFactories.worldStates.UnitWorldStateAbilityUsingUnitsTerran_Wraith;
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

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateAbilityUsingUnitsTerran_Wraith();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterAbilityUsingUnitsTerran_Wraith(playerUnit);
	}

	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateStealth();
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterStealth(playerUnit);
	}

}
