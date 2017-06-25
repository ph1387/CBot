package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * UnitWorldStateAbilityUsingUnitsTerran_SiegeTank.java --- WorldState for a
 * Terran_SiegeTank using its corresponding abilities.
 * 
 * @author P H - 24.06.2017
 *
 */
public class UnitWorldStateAbilityUsingUnitsTerran_SiegeTank extends UnitWorldStateAbilityUsingUnits {

	public UnitWorldStateAbilityUsingUnitsTerran_SiegeTank() {
		this.add(new GoapState(0, "isSieged", false));
	}
}
