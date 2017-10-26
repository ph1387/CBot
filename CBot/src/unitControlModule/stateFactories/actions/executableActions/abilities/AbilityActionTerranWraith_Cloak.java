package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.TechType;

/**
 * AbilityActionTerranWraith_Cloak.java --- The cloak ability of a
 * Terran_Wraith.
 * 
 * @author P H - 07.10.2017
 *
 */
public class AbilityActionTerranWraith_Cloak extends AbilityAction_Cloak {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTerranWraith_Cloak(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected TechType defineType() {
		return TechType.Cloaking_Field;
	}

}
