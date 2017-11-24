package buildingOrderModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueTerran;
import buildingOrderModule.stateFactories.updater.GoalStateUpdaterActionQueueTerran;
import buildingOrderModule.stateFactories.updater.Updater;
import buildingOrderModule.stateFactories.updater.WorldStateUpdaterActionQueueTerran;
import buildingOrderModule.stateFactories.worldStates.ManagerWorldStateActionQueueTerran;
import javaGOAP.GoapState;

// TODO: UML CHANGE ABSTRACT
// TODO: UML RENAME StateFactoryTerranBasic
/**
 * StateFactoryTerranDefault.java --- Default Terran StateFactory containing
 * default values.
 * 
 * @author P H - 28.04.2017
 *
 */
public abstract class StateFactoryTerranDefault extends StateFactoryDefault {

	// -------------------- Functions

	@Override
	public HashSet<GoapState> generateWorldState() {
		return new ManagerWorldStateActionQueueTerran();
	}

	@Override
	public List<GoapState> generateGoalState() {
		return new ManagerGoalStateActionQueueTerran();
	}

	// TODO: UML REMOVE
	// @Override
	// public LinkedHashSet<GoapAction> generateAvailableActions() {
	// return new AvailableActionsSimulationQueueTerran();
	// }

	@Override
	public Updater getMatchingWorldStateUpdater(BuildActionManager manager) {
		return new WorldStateUpdaterActionQueueTerran(manager);
	}

	@Override
	public Updater getMatchingGoalStateUpdater(BuildActionManager manager) {
		return new GoalStateUpdaterActionQueueTerran(manager);
	}

	// TODO: UML REMOVE
	// @Override
	// public Updater getMatchingActionUpdater(BuildActionManager manager) {
	// return new ActionUpdaterSimulationQueueTerran(manager);
	// }
}
