package buildingOrderModule.stateFactories.actions;

import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueStartingTerranRaxFE;

/**
 * AvailableActionsSimulationQueueTerran.java --- Available actions for the Terran race.
 * 
 * @author P H - 30.04.2017
 *
 */
public class AvailableActionsSimulationQueueTerran extends AvailableActionsSimulationQueue {

	public AvailableActionsSimulationQueueTerran() {
		this.add(new ActionQueueStartingTerranRaxFE(1));
	}
}
