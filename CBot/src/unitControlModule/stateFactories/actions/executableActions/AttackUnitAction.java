package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapi.Unit;
import core.Core;
import core.Display;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * AttackUnitAction.java --- An attack action for attacking a single unit.
 * @author P H - 09.02.2017
 *
 */
public class AttackUnitAction extends BaseAction {

	/**
	 * @param target type: Unit
	 */
	public AttackUnitAction(Object target) {
		super(target);
		
		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "unitsInRange", true));
		this.addPrecondition(new GoapState(0, "unitsInSight", true));
	}

	// -------------------- Functions
	
	@Override
	protected boolean isDone(GoapUnit goapUnit) {
		return !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().contains(this.target);
	}

	@Override
	protected boolean performSpecificAction(GoapUnit goapUnit) {
		// TODO: CHANGE TO USE THIS.ACTIONTRIGGER
		return ((PlayerUnit) goapUnit).getUnit().attack(((Unit) this.target));
	}

	@Override
	protected float generateBaseCost(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected float generateCostRelativeToTarget(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return (this.target != null && ((PlayerUnit) goapUnit).getUnit().canAttack((Unit) this.target));
	}

	@Override
	protected boolean requiresInRange(GoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean isInRange(GoapUnit goapUnit) {
		return false;
	}
}
