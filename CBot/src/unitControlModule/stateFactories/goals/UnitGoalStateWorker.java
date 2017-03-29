package unitControlModule.stateFactories.goals;

import java.util.ArrayList;

import javaGOAP.GoapState;
//TODO: UML
/**
 * UnitGoalStateWorker.java --- A simple GoalState for a worker Unit.
 * 
 * @author P H - 25.03.2017
 *
 */
public class UnitGoalStateWorker extends ArrayList<GoapState> {
	
	public UnitGoalStateWorker() {
		this.add(new GoapState(1, "destroyUnit", true));
		this.add(new GoapState(1, "retreatFromUnit", true));
		this.add(new GoapState(0, "enemyKnown", true));
		this.add(new GoapState(3, "constructing", true));
		this.add(new GoapState(2, "gatheringMinerals", true));
		this.add(new GoapState(2, "gatheringMinerals", true));
	}
}
