package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.AvailableActionsTerran_SCV;
import unitControlModule.stateFactories.goals.UnitGoalStateWorker;
import unitControlModule.stateFactories.updater.ActionUpdaterTerran_SCV;
import unitControlModule.stateFactories.updater.GoalStateUpdaterWorker;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * StateFactoryTerran_SCV.java --- A StateFactory used for generating all
 * necessary Objects for the Terran_SCV.
 * 
 * @author P H - 25.03.2017
 *
 */
public class StateFactoryTerran_SCV extends WorldStateFactoryDefault {

	// -------------------- Functions
	
	@Override
	public List<GoapState> generateGoalState() {
		return new UnitGoalStateWorker();
	}

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran_SCV();
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new GoalStateUpdaterWorker(playerUnit);
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new ActionUpdaterTerran_SCV(playerUnit);
	}
}
