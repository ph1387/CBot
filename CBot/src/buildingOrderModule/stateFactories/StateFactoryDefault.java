package buildingOrderModule.stateFactories;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.AvailableActionsDefault;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateDefault;
import buildingOrderModule.stateFactories.updater.ActionUpdaterDefault;
import buildingOrderModule.stateFactories.updater.GoalStateUpdaterDefault;
import buildingOrderModule.stateFactories.updater.Updater;
import buildingOrderModule.stateFactories.updater.WorldStateUpdaterDefault;
import buildingOrderModule.stateFactories.worldStates.ManagerWorldStateDefault;
import javaGOAP.GoapAction;
import javaGOAP.GoapState;

/**
 * StateFactoryDefault.java --- Default state factory containing the default
 * actions and states.
 * 
 * @author P H - 28.04.2017
 *
 */
public class StateFactoryDefault implements StateFactory {

	// -------------------- Functions

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new ManagerWorldStateDefault();
	}

	@Override
	public List<GoapState> generateGoalState() {
		return new ManagerGoalStateDefault();
	}

	// TODO: UML RETURN TYPE
	@Override
	public LinkedHashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsDefault();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(BuildActionManager manager) {
		return new WorldStateUpdaterDefault(manager);
	}

	@Override
	public Updater getMatchingGoalStateUpdater(BuildActionManager manager) {
		return new GoalStateUpdaterDefault(manager);
	}

	@Override
	public Updater getMatchingActionUpdater(BuildActionManager manager) {
		return new ActionUpdaterDefault(manager);
	}
}
