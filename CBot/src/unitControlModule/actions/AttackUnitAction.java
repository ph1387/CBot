package unitControlModule.actions;

import bwapi.TilePosition;
import bwapi.Unit;
import unitControlModule.PlayerUnit;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;

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
		this.addEffect(new GoapState(0, "attackNearestEnemyUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
	}

	// -------------------- Functions
	
	@Override
	protected boolean isDone(GoapUnit goapUnit) {
		return !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().contains(this.target);
	}

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
		// TODO: Implementation: performAction
		
		return  ((PlayerUnit) goapUnit).getUnit().attack(((Unit) this.target));
	}

	@Override
	protected float generateBaseCost(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected float generateCostRelativeToTarget(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance(((Unit) this.target).getPosition());
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().canAttack((Unit) this.target);
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
