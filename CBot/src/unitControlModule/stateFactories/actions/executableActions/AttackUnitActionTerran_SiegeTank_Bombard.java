package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * AttackUnitActionTerran_SiegeTank_Bombard.java --- A specific AttackUnitAction
 * with which the Terran_SiegeTank can attack enemies.
 * 
 * @author P H - 24.06.2017
 *
 */
public class AttackUnitActionTerran_SiegeTank_Bombard extends BaseAction {

	// Below this distance the SiegeTank_SiegeMode will / can not attack.
	private static final int MIN_TILE_RANGE = 6;
	private static final int MAX_TILE_RANGE = 12;

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
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		if (this.target != null) {
			boolean minDistanceMet = !((PlayerUnit) goapUnit).isNearTilePosition(((Unit) this.target).getTilePosition(),
					MIN_TILE_RANGE);
			boolean maxDistanceMet = ((PlayerUnit) goapUnit).isNearTilePosition(((Unit) this.target).getTilePosition(),
					MAX_TILE_RANGE);

			success = minDistanceMet && maxDistanceMet;
		}
		return success;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean isEnemyDead = !((PlayerUnit) goapUnit).getAllEnemyUnitsInWeaponRange().contains(this.target);
		boolean isConfidenceLow = ((PlayerUnit) goapUnit).isConfidenceBelowThreshold();
		boolean isEnemyTooClose = ((PlayerUnit) goapUnit).isNearTilePosition(((Unit) this.target).getTilePosition(),
				MIN_TILE_RANGE);

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
}
