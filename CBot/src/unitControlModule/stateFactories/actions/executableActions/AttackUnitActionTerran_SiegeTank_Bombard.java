package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitTerran_SiegeTank;

/**
 * AttackUnitActionTerran_SiegeTank_Bombard.java --- A specific AttackUnitAction
 * with which the Terran_SiegeTank can attack enemies.
 * 
 * @author P H - 24.06.2017
 *
 */
public class AttackUnitActionTerran_SiegeTank_Bombard extends BaseAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public AttackUnitActionTerran_SiegeTank_Bombard(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "destroyUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "allowFighting", true));
		this.addPrecondition(new GoapState(0, "isSieged", true));

		this.addPrecondition(new GoapState(0, "inSiegeRange", true));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && ((PlayerUnitTerran_SiegeTank) goapUnit).isInSiegeRange((Unit) this.target);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean isEnemyDead = !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().contains(this.target);
		boolean isConfidenceLow = ((PlayerUnit) goapUnit).isConfidenceBelowThreshold();
		boolean isEnemyTooClose = ((PlayerUnit) goapUnit).isNearPosition(((Unit) this.target).getPosition(),
				PlayerUnitTerran_SiegeTank.getMinSiegeRange());

		return isEnemyDead || isConfidenceLow || isEnemyTooClose;
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
	protected void resetSpecific() {

	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		// False since the Unit is static and is unable to move. Therefore any
		// target chosen by the leader might be out of range.
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}

	@Override
	public int defineMaxGroupSize() {
		return 0;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}

}
