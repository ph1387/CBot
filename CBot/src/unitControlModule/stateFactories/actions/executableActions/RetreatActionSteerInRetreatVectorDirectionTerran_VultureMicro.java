package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Vector;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * RetreatActionSteerInRetreatVectorDirectionTerran_VultureMicro.java ---
 * Improved RetreatAction for a Terran_Vulture that allows a kiting of enemy
 * Units using the Terran_Vulture micro that utilizes the patrol command.
 * 
 * @author P H - 30.09.2017
 *
 */
public class RetreatActionSteerInRetreatVectorDirectionTerran_VultureMicro
		extends RetreatActionSteerInRetreatVectorDirection {

	// State machine used in the microing of the Unit.
	private enum MicroState {
		MOVE_INTO_RANGE, RETREAT, PATROL
	};

	private MicroState currentMircoState = MicroState.MOVE_INTO_RANGE;

	// Timer for the retreat action to switch to the patrol command.
	private int minFramesToWaitRetreat = 15;
	private int lastExecutionTimeStampRetreat = 0;
	// Timer for the patrol command to switch to the retreat action.
	private int minFramesToWaitPatrol = 3;
	private int lastExecutionTimeStampPatrol = 0;
	private boolean issuedPatrolCommand = false;
	private int turnAngle = 15;

	// The distance that the enemy must be away from the executing Unit before
	// the patrol command is issued.
	private int minEnemyDistance = 48;

	// The additional range that is added towards the executing Unit's range.
	// The lower this value the less Unit's can be targeted by this action. This
	// is necessary to prevent the Unit from being attacked while microing!
	private int rangeOffset = -1;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionSteerInRetreatVectorDirectionTerran_VultureMicro(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "allowFighting", true));
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		boolean success = true;

		// Move towards the enemy Unit until the executing one is barely in
		// attack range.
		if (this.currentMircoState == MicroState.MOVE_INTO_RANGE) {
			if (!playerUnit.getUnit().isInWeaponRange((Unit) this.target)) {
				success &= playerUnit.getUnit().move(((Unit) this.target).getPosition());
			} else {
				this.currentMircoState = MicroState.RETREAT;
			}
		}

		// Proceed with the "normal" performance of the superclass.
		if (this.currentMircoState == MicroState.RETREAT) {
			// Action change trigger has to be set in order for the super
			// function to properly work since it relies on it!
			this.actionChangeTrigger = true;
			success &= super.performSpecificAction(goapUnit);

			// Move back into range if necessary.
			if (playerUnit.getUnit().getDistance((Unit) this.target) >= playerUnit.getUnit().getType().groundWeapon()
					.maxRange()) {
				this.currentMircoState = MicroState.MOVE_INTO_RANGE;
			}
			// A patrol command has to be executed for the micro to have an
			// effect.
			else if (playerUnit.getUnit().getDistance((Unit) this.target) >= this.minEnemyDistance
					&& Core.getInstance().getGame().getFrameCount()
							- this.lastExecutionTimeStampRetreat >= this.minFramesToWaitRetreat) {
				this.currentMircoState = MicroState.PATROL;
				this.lastExecutionTimeStampRetreat = Core.getInstance().getGame().getFrameCount();
			}
		}

		// Patrol towards the enemy Unit in a specific angle.
		if (this.currentMircoState == MicroState.PATROL) {
			if (!this.issuedPatrolCommand) {
				Vector vecToEnemy = new Vector(playerUnit.getUnit().getPosition(), ((Unit) this.target).getPosition());
				vecToEnemy.rotateLeftDEG(this.turnAngle);
				Position patrolPosition = new Position(vecToEnemy.getX() + (int) (vecToEnemy.getDirX()),
						vecToEnemy.getY() + (int) (vecToEnemy.getDirY()));
				success &= playerUnit.getUnit().patrol(patrolPosition);

				this.lastExecutionTimeStampPatrol = Core.getInstance().getGame().getFrameCount();
				this.issuedPatrolCommand = true;
			}

			// After a short while (The Unit must / should have attacked by now)
			// proceed with the execution of the superclass' implementation.
			if (Core.getInstance().getGame().getFrameCount()
					- this.lastExecutionTimeStampPatrol >= this.minFramesToWaitPatrol) {
				this.currentMircoState = MicroState.RETREAT;
				this.issuedPatrolCommand = false;
			}
		}

		return success;
	}

	// TODO: UML ADD
	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean superPreconditions = super.checkProceduralSpecificPrecondition(goapUnit);
		boolean matchingUnitTargeted = false;

		// Make sure only Units with a smaller range are considered for microing
		// against!
		if (this.target != null) {
			matchingUnitTargeted = ((Unit) this.target).getType().groundWeapon()
					.maxRange() < ((PlayerUnit) goapUnit).getUnit().getType().groundWeapon().maxRange()
							+ this.rangeOffset;
		}

		return superPreconditions && matchingUnitTargeted;
	}

	// TODO: UML ADD
	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 20.f;
	}

	// TODO: UML ADD
	@Override
	protected void resetSpecific() {
		super.resetSpecific();

		this.currentMircoState = MicroState.MOVE_INTO_RANGE;
		this.issuedPatrolCommand = false;
	}

	// -------------------- Group

	// TODO: UML ADD
	@Override
	public boolean canPerformGrouped() {
		return false;
	}
}
