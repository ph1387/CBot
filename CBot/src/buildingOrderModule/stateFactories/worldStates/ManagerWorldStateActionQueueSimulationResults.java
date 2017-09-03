package buildingOrderModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * ManagerWorldStateActionQueueSimulationResults.java --- WorldState for a
 * BuildActionManager that uses simulations.
 * 
 * @author P H - 02.09.2017
 *
 */
public class ManagerWorldStateActionQueueSimulationResults extends ManagerWorldStateActionQueue {

	public ManagerWorldStateActionQueueSimulationResults() {
		this.add(new GoapState(0, "simulationAllowed", false));
		this.add(new GoapState(0, "simulationRunning", false));
	}

}
