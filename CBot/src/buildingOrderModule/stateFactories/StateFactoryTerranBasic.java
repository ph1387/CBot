package buildingOrderModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.AvailableActionsTerran;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueTerran;
import buildingOrderModule.stateFactories.updater.ActionUpdaterTerran;
import buildingOrderModule.stateFactories.updater.GoalStateUpdaterActionQueueTerran;
import buildingOrderModule.stateFactories.updater.Updater;
import buildingOrderModule.stateFactories.updater.WorldStateUpdaterActionQueueTerran;
import buildingOrderModule.stateFactories.worldStates.ManagerWorldStateActionQueueTerran;
import javaGOAP.GoapAction;
import javaGOAP.GoapState;

/**
 * StateFactoryTerranBasic.java --- Default Terran StateFactory containing basic
 * building sequences.
 * 
 * @author P H - 28.04.2017
 *
 */
public class StateFactoryTerranBasic extends StateFactoryDefault {

	// -------------------- Functions

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new ManagerWorldStateActionQueueTerran();
	}

	@Override
	public List<GoapState> generateGoalState() {
		return new ManagerGoalStateActionQueueTerran();
	}

	@Override
	public HashSet<GoapAction> generateAvailableActions() {
		return new AvailableActionsTerran();
	}

	@Override
	public Updater getMatchingWorldStateUpdater(BuildActionManager manager) {
		return new WorldStateUpdaterActionQueueTerran(manager);
	}

	@Override
	public Updater getMatchingGoalStateUpdater(BuildActionManager manager) {
		return new GoalStateUpdaterActionQueueTerran(manager);
	}

	@Override
	public Updater getMatchingActionUpdater(BuildActionManager manager) {
		return new ActionUpdaterTerran(manager);
	}
}
