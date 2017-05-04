package buildingOrderModule.stateFactories.goals;

import javaGOAP.GoapState;

/**
 * ManagerGoalStateActionQueue.java --- GoalState for a BuildActionManager that
 * is using action queues.
 * 
 * @author P H - 30.04.2017
 *
 */
public class ManagerGoalStateActionQueue extends ManagerGoalStateDefault {

	public ManagerGoalStateActionQueue() {
		this.add(new GoapState(10, "buildOrderAllowed", false));
		this.add(new GoapState(10, "startingBuildOrderNeeded", false));
	}
}
