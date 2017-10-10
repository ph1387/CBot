package unitControlModule.stateFactories.goals;

import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * UnitGoalStateTerran_SCV.java --- A GoalState for a Terran_SCV. This Class
 * exists due to them being able to repair certain Units and buildings (=>
 * Separate GoalStates!).
 * 
 * @author P H - 09.10.2017
 *
 */
public class UnitGoalStateTerran_SCV extends UnitGoalStateWorker {

	public UnitGoalStateTerran_SCV() {
		this.add(new GoapState(6, "repairing", true));
		this.add(new GoapState(1, "isNearRepairableUnit", true));
	}

}
