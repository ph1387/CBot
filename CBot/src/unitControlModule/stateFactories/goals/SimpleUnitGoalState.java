package unitControlModule.stateFactories.goals;

import java.util.ArrayList;

import unitControlModule.goapActionTaking.GoapState;

/**
 * SimpleUnitGoalState.java --- A simple GoalState for a Unit containing all
 * basic states.
 * 
 * @author P H - 26.02.2017
 *
 */
public class SimpleUnitGoalState extends ArrayList<GoapState> {

	public SimpleUnitGoalState() {
		this.add(new GoapState(1, "enemyKnown", true));
		this.add(new GoapState(2, "destroyUnit", true));
		this.add(new GoapState(1, "retreatFromUnit", true));
	}
}
