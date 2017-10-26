package unitControlModule.stateFactories.goals;

import javaGOAP.GoapState;

/**
 * UnitGoalStateStealth.java --- GoalState containing goals regarding different
 * stealth aspects of the Game like i.e. cloaking.
 * 
 * @author P H - 07.10.2017
 *
 */
public class UnitGoalStateStealth extends UnitGoalStateDefault {

	public UnitGoalStateStealth() {
		// Problem: Goal state can not be switched. Therefore a second one is
		// needed for decloaking actions!
		this.add(new GoapState(1, "isCloaked", true));
		this.add(new GoapState(1, "isDecloaked", true));
	}
}
