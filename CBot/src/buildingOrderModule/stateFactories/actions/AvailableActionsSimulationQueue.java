package buildingOrderModule.stateFactories.actions;

import java.util.LinkedHashSet;

import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;
import javaGOAP.GoapAction;

/**
 * AvailableActionsSimulationQueue.java --- Available Actions for all simulation
 * users.
 * 
 * @author P H - 16.07.2017
 *
 */
public class AvailableActionsSimulationQueue extends LinkedHashSet<GoapAction> {

	public AvailableActionsSimulationQueue() {
		this.add(new ActionQueueSimulationResults(1));
	}
}
