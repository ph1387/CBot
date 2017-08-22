package unitControlModule.stateFactories.actions.executableActions;

import bwapi.TilePosition;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * DestroyUnitAction.java --- An attacking action with which the unit can
 * perform an attack move to the specified target TilePosition.
 * 
 * @author P H - 07.02.2017
 *
 */
public class AttackMoveAction extends AttackActionGeneralSuperclass {

	// TODO: UML ADD
	private int maxGroupSize = 5;
	// TODO: UML ADD
	private int maxLeaderTileDistance = 5;
	
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
		// Either the Unit is near the target or an enemy is in weapon range and
		// therefore can be attacked.
		return ((PlayerUnit) goapUnit).isNearTilePosition((TilePosition) this.target, null)
				|| !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().isEmpty();
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().attack(((TilePosition) this.target).toPosition());
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance(((TilePosition) this.target).toPosition());
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().canAttack(((TilePosition) this.target).toPosition());
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return true;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return ((PlayerUnit) groupMember).getUnit().attack(((TilePosition) this.target).toPosition());
	}

	// TODO: UML ADD
	@Override
	public int defineMaxGroupSize() {
		return this.maxGroupSize;
	}
	
	// TODO: UML ADD
	@Override
	public int defineMaxLeaderTileDistance() {
		return this.maxLeaderTileDistance;
	}
	
}
