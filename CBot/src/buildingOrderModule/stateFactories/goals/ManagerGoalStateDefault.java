package buildingOrderModule.stateFactories.goals;

import java.util.ArrayList;

import javaGOAP.GoapState;

/**
 * ManagerGoalStateDefault.java --- Default GoalState for a BuildActionManager.
 * 
 * @author P H - 28.04.2017
 *
 */
public class ManagerGoalStateDefault extends ArrayList<GoapState> {

	public ManagerGoalStateDefault() {
		this.add(new GoapState(1, "unitsNeeded", false));
		this.add(new GoapState(1, "buildingsNeeded", false));
	}
}
