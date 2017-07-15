package buildingOrderModule.stateFactories.actions;

import java.util.HashSet;

import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueStartingTerranRaxFE;
import javaGOAP.GoapAction;

/**
 * AvailableActionsTerran.java --- Available actions for the Terran race.
 * 
 * @author P H - 30.04.2017
 *
 */
public class AvailableActionsTerran extends HashSet<GoapAction> {

	public AvailableActionsTerran() {
		this.add(new ActionQueueStartingTerranRaxFE(1));
		this.add(new ActionQueueSimulationResults(1));
	}
}
