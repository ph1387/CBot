package unitControlModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import javaGOAP.GoapAction;
import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.SimpleUnitAvailableActions;
import unitControlModule.stateFactories.goals.SimpleUnitGoalState;
import unitControlModule.stateFactories.updater.SimpleActionUpdater;
import unitControlModule.stateFactories.updater.SimpleGoalStateUpdater;
import unitControlModule.stateFactories.updater.SimpleWorldStateUpdater;
import unitControlModule.stateFactories.updater.Updater;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SimpleStateFactory.java --- A basic StateFactory containing to all basic Unit
 * actions and states.
 * 
 * @author P H - 26.02.2017
 *
 */
public class SimpleStateFactory extends GeneralWorldStateFactory {

	public SimpleStateFactory() {

	}

	// -------------------- Functions

	@Override
	public List<GoapState> generateGoalState() {
		return new SimpleUnitGoalState();
	}

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new SimpleUnitAvailableActions();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(PlayerUnit playerUnit) {
		return new SimpleWorldStateUpdater(playerUnit);
	}

	@Override
	public Updater getMatchingGoalStateUpdater(PlayerUnit playerUnit) {
		return new SimpleGoalStateUpdater(playerUnit);
	}

	@Override
	public Updater getMatchingActionUpdater(PlayerUnit playerUnit) {
		return new SimpleActionUpdater(playerUnit);
	}
}
