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
	 * Abilities with no target require the Unit to assign itself as a target.
	 * 
	 * @param target
	 *            type: Null
	 */
	public AbilityActionTechTargetNone(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().useTech(this.ability);
	}

	@Override
	protected void resetSpecific() {
		super.resetSpecific();

		// Needed since the JavaGOAP library throws errors otherwise.
		this.target = new Object();
	}
}
