package unitControlModule.stateFactories.actions.executableActions;

import bwapi.TilePosition;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML SUPERCLASS + FUNCTIONS
/**
 * DestroyUnitAction.java --- An attacking action with which the unit can
 * perform an attack move to the specified target TilePosition.
 * 
 * @author P H - 07.02.2017
 *
 */
public class AttackMoveAction extends AttackActionGeneralSuperclass {

	/**
	 * @param target
	 *            type: TilePosition
	 */
	public AttackMoveAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isSpecificDone(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNearTilePosition((TilePosition) this.target, 2) || !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().isEmpty();
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.actionChangeTrigger) {
			success = ((PlayerUnit) goapUnit).getUnit().attack(((TilePosition) this.target).toPosition());
		}

		return success;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance(((TilePosition) this.target).toPosition());
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().canAttack(((TilePosition) this.target).toPosition());
	}

}
