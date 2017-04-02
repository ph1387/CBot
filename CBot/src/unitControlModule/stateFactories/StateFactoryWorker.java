package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsWorker;
import unitControlModule.stateFactories.goals.UnitGoalStateWorker;
import unitControlModule.stateFactories.updater.ActionUpdaterWorker;
import unitControlModule.stateFactories.updater.GoalStateUpdaterWorker;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterWorker;
import unitControlModule.stateFactories.worldStates.UnitWorldStateWorker;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryWorker.java --- A StateFactory for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public class StateFactoryWorker implements StateFactory {

	// -------------------- Functions

	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateWorker();
	}

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsWorker();
	}

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateWorker();
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterWorker(playerUnit);
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterWorker(playerUnit);
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterWorker(playerUnit);
	}

}
