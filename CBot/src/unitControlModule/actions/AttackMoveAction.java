package unitControlModule.actions;

import bwapi.TilePosition;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * DestroyUnitAction.java --- An attacking action with which the unit can
 * perform an attack move to the specified target TilePosition.
 * 
 * @author P H - 07.02.2017
 *
 */
public class AttackMoveAction extends BaseAction {

	/**
	 * @param target
	 *            type: TilePosition
	 */
	public AttackMoveAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNear((TilePosition) this.target, 1);
	}

	@Override
	protected boolean performAction(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().attack(((TilePosition) this.target).toPosition());
	}

	@Override
	protected float generateBaseCost(GoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected float generateCostRelativeToTarget(GoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance(((TilePosition) this.target).toPosition());
	}

	@Override
	protected boolean checkProceduralPrecondition(GoapUnit goapUnit) {
		return (this.target != null && ((PlayerUnit) goapUnit).getUnit().canAttack(((TilePosition) this.target).toPosition()));
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
