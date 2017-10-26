package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * UnitWorldStateAbilityUsingUnitsTerran_ScienceVessel.java --- WorldState for a
 * Terran_Science_Vessel using its corresponding abilities.
 * 
 * @author P H - 22.09.2017
 *
 */
public class UnitWorldStateAbilityUsingUnitsTerran_ScienceVessel extends UnitWorldStateAbilityUsingUnits {

	public UnitWorldStateAbilityUsingUnitsTerran_ScienceVessel() {
		this.add(new GoapState(0, "isNearSupportableUnit", false));
		this.add(new GoapState(0, "isFollowingUnit", false));
	}
}
