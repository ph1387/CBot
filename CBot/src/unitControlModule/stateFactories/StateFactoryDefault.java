package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsDefault;
import unitControlModule.stateFactories.goals.UnitGoalStateDefault;
import unitControlModule.stateFactories.updater.ActionUpdaterDefault;
import unitControlModule.stateFactories.updater.GoalStateUpdaterDefault;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterDefault;
import unitControlModule.stateFactories.worldStates.UnitWorldStateDefault;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SimpleStateFactory.java --- A basic StateFactory containing to all basic Unit
 * actions and states.
 * 
 * @author P H - 26.02.2017
 *
 */
public class StateFactoryDefault implements StateFactory {

	public StateFactoryDefault() {

	}

	// -------------------- Functions

	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateDefault();
	}

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsDefault();
	}

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateDefault();
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterDefault(playerUnit);
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterDefault(playerUnit);
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterDefault(playerUnit);
	}
}
