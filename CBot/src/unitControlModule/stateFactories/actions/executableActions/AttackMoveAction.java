package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.Region;
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
	/**
	 * AttackMoveActionWrapper.java --- Wrapper Class used for smartly moving
	 * between ChokePoints.
	 * 
	 * @author P H - 17.03.2018
	 *
	 */
	private class AttackMoveActionWrapper implements SmartlyMovingActionWrapper {

		@Override
		public boolean performInternalAction(IGoapUnit goapUnit, Object target) {
			return ((PlayerUnit) goapUnit).getUnit().attack(((TilePosition) target).toPosition());
		}

		@Override
		public Position convertTarget(Object target) {
			return ((TilePosition) target).toPosition();
		}

	}

	// TODO: UML ADD
	private SmartlyMovingActionWrapper actionWrapper = new AttackMoveActionWrapper();
	private int maxGroupSize = 5;
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
		boolean success = false;

		try {
			Region targetRegion = BWTA.getRegion((TilePosition) this.target);
			success = this.performSmartlyMovingToRegion(goapUnit, targetRegion, this.actionWrapper);
		} catch (Exception e) {
			e.printStackTrace();
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

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return true;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		boolean success = false;

		try {
			Region targetRegion = BWTA.getRegion((TilePosition) this.target);
			success = this.performSmartlyMovingToRegion(groupMember, targetRegion, this.actionWrapper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return success;
	}

	@Override
	public int defineMaxGroupSize() {
		return this.maxGroupSize;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return this.maxLeaderTileDistance;
	}

}
