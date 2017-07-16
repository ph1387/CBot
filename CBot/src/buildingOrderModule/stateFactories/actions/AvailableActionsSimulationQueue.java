package buildingOrderModule.stateFactories.actions;

import java.util.HashSet;

import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;
import javaGOAP.GoapAction;

// TODO: UML ADD
/**
 * AvailableActionsSimulationQueue.java --- Available Actions for all simulation
 * users.
 * 
 * @author P H - 16.07.2017
 *
 */
public class AvailableActionsSimulationQueue extends HashSet<GoapAction> {

	public AvailableActionsSimulationQueue() {
		this.add(new ActionQueueSimulationResults(1));
	}
}
