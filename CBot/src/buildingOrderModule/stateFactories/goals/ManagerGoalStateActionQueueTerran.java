package buildingOrderModule.stateFactories.goals;

import javaGOAP.GoapState;

/**
 * ManagerGoalStateActionQueueTerran.java --- GoalState for a Terran
 * BuildActionManager.
 * 
 * @author P H - 30.04.2017
 *
 */

public class ManagerGoalStateActionQueueTerran extends ManagerGoalStateActionQueue {

	public ManagerGoalStateActionQueueTerran() {
		this.add(new GoapState(1, "simulationRunning", true));
	}
}
