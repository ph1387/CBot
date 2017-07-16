package buildingOrderModule.stateFactories.actions;

import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueStartingTerranRaxFE;

/**
 * AvailableActionsTerran.java --- Available actions for the Terran race.
 * 
 * @author P H - 30.04.2017
 *
 */
public class AvailableActionsTerran extends AvailableActionsSimulationQueue {

	public AvailableActionsTerran() {
		this.add(new ActionQueueStartingTerranRaxFE(1));
	}
}
