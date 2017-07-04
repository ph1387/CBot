package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

//TODO: UML SUPERCLASS + FUNCTIONS
/**
 * AttackUnitAction.java --- An attack action for attacking a single unit.
 * 
 * @author P H - 09.02.2017
 *
 */
public class AttackUnitAction extends AttackActionGeneralSuperclass {

	/**
	 * @param target
	 *            type: Unit
	 */
	public AttackUnitAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "unitsInRange", true));
		this.addPrecondition(new GoapState(0, "unitsInSight", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isSpecificDone(IGoapUnit goapUnit) {
		boolean isEnemyDead = !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().contains(this.target);

		return isEnemyDead;
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.actionChangeTrigger) {
			success = ((PlayerUnit) goapUnit).getUnit().attack(((Unit) this.target));
		}

		return success;
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().canAttack((Unit) this.target);
	}

}
