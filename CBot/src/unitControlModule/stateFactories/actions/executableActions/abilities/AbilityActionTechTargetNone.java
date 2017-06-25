package unitControlModule.stateFactories.actions.executableActions.abilities;

import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * AbilityActionTechTargetNone.java --- Action for simply performing an ability
 * without any specific target.
 * 
 * @author P H - 23.06.2017
 *
 */
public abstract class AbilityActionTechTargetNone extends AbilityActionGeneralSuperclass {

	/**
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTechTargetNone(Object target) {
		// super(target);
		// 0 is passed to the constructor of the superclass since passing null
		// would cause an error message to be shown in the console even though
		// this behavior is intended.
		super(0);
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().useTech(this.ability);
	}
}
