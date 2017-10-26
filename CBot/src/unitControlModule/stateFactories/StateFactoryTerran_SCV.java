package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_SCV;
import unitControlModule.stateFactories.goals.UnitGoalStateTerran_SCV;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_SCV;
import unitControlModule.stateFactories.updater.GoalStateUpdaterTerran_SCV;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.stateFactories.updater.WorldStateUpdaterTerran_SCV;
import unitControlModule.stateFactories.worldStates.UnitWorldStateTerran_SCV;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryTerran_SCV.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_SCV.
 * 
 * @author P H - 25.03.2017
 *
 */
public class StateFactoryTerran_SCV extends StateFactoryWorker {

	// -------------------- Functions

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_SCV();
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_SCV(playerUnit);
	}

	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateTerran_SCV();
	}

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new UnitWorldStateTerran_SCV();
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterTerran_SCV(playerUnit);
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new WorldStateUpdaterTerran_SCV(playerUnit);
	}

}
