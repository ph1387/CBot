package buildingOrderModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * ManagerWorldStateActionQueueTerran.java --- WorldState for a Terran
 * BuildActionManager.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ManagerWorldStateActionQueueTerran extends ManagerWorldStateActionQueue {

	public ManagerWorldStateActionQueueTerran() {
		this.add(new GoapState(0, "simulationAllowed", false));
		this.add(new GoapState(0, "simulationRunning", false));
	}
}
