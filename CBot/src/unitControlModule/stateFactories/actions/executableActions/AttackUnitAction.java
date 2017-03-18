package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Color;
import bwapi.Unit;
import core.Core;
import core.Display;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
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
	protected boolean isDone(IGoapUnit goapUnit) {
		return !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().contains(this.target);
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		// TODO: CHANGE TO USE THIS.ACTIONTRIGGER
		return ((PlayerUnit) goapUnit).getUnit().attack(((Unit) this.target));
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return (this.target != null && ((PlayerUnit) goapUnit).getUnit().canAttack((Unit) this.target));
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected void reset() {
		// TODO Auto-generated method stub
		
	}
}
