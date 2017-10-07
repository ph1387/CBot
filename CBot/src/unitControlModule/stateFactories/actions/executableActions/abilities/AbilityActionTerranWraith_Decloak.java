package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;

// TODO: UML ADD
/**
 * AbilityActionTerranWraith_Decloak.java --- The decloak ability of a
 * Terran_Wraith.
 * 
 * @author P H - 07.10.2017
 *
 */
public class AbilityActionTerranWraith_Decloak extends AbilityAction_Decloak {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTerranWraith_Decloak(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Cloaking_Field;
	}

}
