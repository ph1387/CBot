package unitControlModule.stateFactories.goals;

import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * UnitGoalStateProtect.java --- GoalState containing goals regarding the
 * protection of the Player's own Units. Mostly used for supporting Units.
 * 
 * @author P H - 27.06.2017
 *
 */
public class UnitGoalStateProtect extends UnitGoalStateDefault {

	public UnitGoalStateProtect() {
		this.add(new GoapState(3, "healing", true));
	}
}
