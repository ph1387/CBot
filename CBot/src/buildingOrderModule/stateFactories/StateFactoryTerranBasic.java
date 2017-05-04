package buildingOrderModule.stateFactories;

import java.util.HashSet;
import java.util.List;

import buildingOrderModule.stateFactories.actions.AvailableActionsTerran;
import buildingOrderModule.stateFactories.goals.ManagerGoalStateActionQueueTerran;
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

	// TODO: Needed Change: Action updater necessary?
}
