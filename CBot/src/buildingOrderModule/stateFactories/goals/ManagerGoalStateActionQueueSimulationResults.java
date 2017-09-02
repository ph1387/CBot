package buildingOrderModule.stateFactories.goals;

import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * ManagerGoalStateActionQueueSimulationResults.java --- GoalState for a
 * BuildActionManager that is using simulation action queues.
 * 
 * @author P H - 02.09.2017
 *
 */
public class ManagerGoalStateActionQueueSimulationResults extends ManagerGoalStateActionQueue {

	public ManagerGoalStateActionQueueSimulationResults() {
		this.add(new GoapState(1, "simulationRunning", true));
	}

}
