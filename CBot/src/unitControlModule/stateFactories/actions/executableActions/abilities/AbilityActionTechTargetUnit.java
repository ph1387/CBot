package unitControlModule.stateFactories.actions.executableActions.abilities;

import bwapi.Unit;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * AbilityActionTechTargetUnit.java --- Action for performing an ability on
 * another Unit.
 * 
 * @author P H - 23.06.2017
 *
 */
public abstract class AbilityActionTechTargetUnit extends AbilityActionGeneralSuperclass {

	/**
	 * @param target
	 *            type: Unit
	 */
	public AbilityActionTechTargetUnit(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().useTech(this.ability, (Unit) this.target);
	}
}
