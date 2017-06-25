package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * UnitWorldStateAbilityUsingUnits.java --- WorldState for all Units using
 * abilities.
 * 
 * @author P H - 24.06.2017
 *
 */
public class UnitWorldStateAbilityUsingUnits extends UnitWorldStateDefault {

	public UnitWorldStateAbilityUsingUnits() {
		this.add(new GoapState(0, "canUseAbilities", true));
	}
}
