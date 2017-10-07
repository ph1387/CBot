package unitControlModule.stateFactories.goals;

import javaGOAP.GoapState;

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
		this.add(new GoapState(3, "shielding", true));
		this.add(new GoapState(2, "isNearSupportableUnit", true));
		this.add(new GoapState(2, "isNearHealableUnit", true));
	}
}
