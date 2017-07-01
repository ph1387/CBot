package unitControlModule.stateFactories.worldStates;

import javaGOAP.GoapState;

/**
 * UnitWorldStateAbilityUsingUnitsTerran_Medic.java --- WorldState for a
 * Terran_Medic using its corresponding abilities.
 * @author P H - 27.06.2017
 *
 */
public class UnitWorldStateAbilityUsingUnitsTerran_Medic extends UnitWorldStateAbilityUsingUnits {
	
	public UnitWorldStateAbilityUsingUnitsTerran_Medic() {
		this.add(new GoapState(0, "isNearSupportableUnit", false));
		this.add(new GoapState(0, "isNearHealableUnit", false));
		this.add(new GoapState(0, "healing", false));
	}
}
