package buildingOrderModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * ManagerWorldStateActionQueue.java --- WorldState for a BuildActionManager
 * using action queues in the beginning.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ManagerWorldStateActionQueue extends ManagerWorldStateDefault {

	public ManagerWorldStateActionQueue() {
		this.add(new GoapState(0, "buildOrderAllowed", true));
		this.add(new GoapState(0, "startingBuildOrderNeeded", true));
		this.add(new GoapState(0, "simulationRunning", false));
	}
}
