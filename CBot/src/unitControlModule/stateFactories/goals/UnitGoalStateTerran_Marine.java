package unitControlModule.stateFactories.goals;

import javaGOAP.GoapState;

// TODO: UML ADD
/**
 * UnitGoalStateTerran_Marine.java --- GoalState for Terran_Marines that takes
 * i.e. moving / loading into Terran_Bunkers into account.
 * 
 * @author P H - 13.05.2018
 *
 */
public class UnitGoalStateTerran_Marine extends UnitGoalStateDefault {

	public UnitGoalStateTerran_Marine() {
		this.add(new GoapState(4, "isLoadedIntoBunker", true));
	}
}
