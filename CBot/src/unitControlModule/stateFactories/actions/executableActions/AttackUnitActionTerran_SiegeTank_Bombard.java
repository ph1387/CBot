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
		this.addPrecondition(new GoapState(0, "belowSiegeRange", false));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		PlayerUnitTerran_SiegeTank siegeTank = (PlayerUnitTerran_SiegeTank) goapUnit;
		Unit target = (Unit) this.target;

		// Only rudimentary check performed since the Unit could otherwise end
		// up morphing most of the time!
		return this.target != null && siegeTank.isInSiegeRange(target) && !siegeTank.isBelowSiegeRange(target);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 2;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		PlayerUnitTerran_SiegeTank siegeTank = (PlayerUnitTerran_SiegeTank) goapUnit;
		Unit target = (Unit) this.target;
		boolean isDone = true;

		if (target != null) {
			boolean isConfidenceLow = siegeTank.isConfidenceBelowThreshold();
			boolean isEnemyOutOfWeaponRange = !siegeTank.getAllEnemyUnitsInWeaponRange().contains(this.target);
			// Only rudimentary check performed since the Unit could otherwise
			// end up morphing most of the time!
			boolean isEnemyTooClose = siegeTank.isBelowSiegeRange(target);

			isDone = isEnemyOutOfWeaponRange || isConfidenceLow || isEnemyTooClose;
		}

		return isDone;
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.actionChangeTrigger) {
			// Do NOT change the return value here. This is due to the action
			// failing under certain circumstances i.e. when Units are either
			// too close or too far away. Since the target is constantly being
			// adjusted and the attack parameters therefore changing the Action
			// would often return false. Further checks in the "isDone" or
			// "checkProceduralPrecondition" functions are not advised since the
			// Unit could end up morphin between the Tank- and SiegeMode most of
			// the time.
			((PlayerUnit) goapUnit).getUnit().attack(((Unit) this.target));
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
