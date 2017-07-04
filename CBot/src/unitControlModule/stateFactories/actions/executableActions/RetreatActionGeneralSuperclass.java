package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Color;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Vector;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatAction_GeneralSuperclass.java --- Superclass for RetreatActions. <br>
 * <b>Notice:</b> <br>
 * The temporary retreat-Position has to be set by subclasses since this
 * determines if this Action can actually be taken or not.
 * 
 * @author P H - 10.03.2017
 *
 */
public abstract class RetreatActionGeneralSuperclass extends BaseAction {

	// Has to be smaller than MIN_PIXELDISTANCE_TO_UNIT!
	// -> isDone() condition! v
	private static final int DIST_TO_GATHERING_POINT = Core.getInstance().getTileSize();
	protected static final int TILE_RADIUS_NEAR = 1;

	protected static HashSet<Position> gatheringPoints = new HashSet<Position>();

	protected Position generatedTempRetreatPosition = null;
	protected Position retreatPosition = null;

	// Vector related stuff
	protected static final int ALPHA_MAX = 90;
	protected double maxDistance = 10 * Core.getInstance().getTileSize();
	// vecEU -> Vector(enemyUnit, playerUnit)
	// vecUTP -> Vector(playerUnit, targetPosition)
	protected Vector vecEU, vecUTP;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionGeneralSuperclass(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "retreatFromUnit", true));
		this.addPrecondition(new GoapState(0, "enemyKnown", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		if (((PlayerUnit) goapUnit).isNearPosition(this.retreatPosition, DIST_TO_GATHERING_POINT)
				|| this.target == null) {
			RetreatActionGeneralSuperclass.gatheringPoints.remove(this.retreatPosition);
		}

		return !RetreatActionGeneralSuperclass.gatheringPoints.contains(this.retreatPosition);
	}

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Only override the current retreatPosition if the action trigger is
		// set and move towards it. This enables the ability of storing the
		// Positions inside a HashSet and moving other Units towards them
		// instead of constantly updating them, which would result in a Unit
		// only following them in one general direction. Once a Position is set,
		// stick with it.
		if (this.actionChangeTrigger && this.generatedTempRetreatPosition != null) {
			this.retreatPosition = this.generatedTempRetreatPosition;
			RetreatActionGeneralSuperclass.gatheringPoints.add(this.retreatPosition);
			success &= this.retreatPosition != null && ((PlayerUnit) goapUnit).getUnit().move(this.retreatPosition);
		} else if (this.actionChangeTrigger && this.generatedTempRetreatPosition == null) {
			success = false;
		}

		if (this.retreatPosition != null) {

			// TODO: DEBUG INFO
			// // Position to which the Unit retreats to
			Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(),
					this.retreatPosition, new Color(255, 255, 0));
			Core.getInstance().getGame().drawCircleMap(this.retreatPosition.getPoint(), 5, new Color(0, 255, 0), true);
		}
		return this.retreatPosition != null && success;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		float returnValue = 0.f;

		try {
			returnValue = ((PlayerUnit) goapUnit).getUnit().getDistance(this.generatedTempRetreatPosition);
		} catch (Exception e) {
			returnValue = Float.MAX_VALUE;
		}

		return returnValue;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		if (this.target != null && ((PlayerUnit) goapUnit).getClosestEnemyUnitInConfidenceRange() != null) {
			this.updateVectors(goapUnit);

			success = this.checkProceduralSpecificPrecondition(goapUnit);

			// The first ever found Position has to be added as temp retreat
			// Position. This ensures, that isDone() returns false and the
			// action gets actually executed. The actual retreatPosition gets
			// set when performAction() gets called.
			if (this.retreatPosition == null) {
				this.retreatPosition = this.generatedTempRetreatPosition;
				RetreatActionGeneralSuperclass.gatheringPoints.add(this.generatedTempRetreatPosition);
			}

			// TODO: DEBUG INFO
			// Targeted retreat-Position (Vector Unit -> TargetPosition)
			bwapi.Unit unit = ((PlayerUnit) goapUnit).getUnit();
			Position targetEndPosition = new Position(vecUTP.getX() + (int) (vecUTP.getDirX()),
					vecUTP.getY() + (int) (vecUTP.getDirY()));
			Core.getInstance().getGame().drawLineMap(unit.getPosition(), targetEndPosition, new Color(255, 255, 255));
		}
		return success;
	}

	/**
	 * Each Subclass implements its own specific precondition test.
	 * 
	 * @param goapUnit
	 *            the Unit the test is performed on.
	 * @return true or false depending if the test was successful or not.
	 */
	protected abstract boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit);

	/**
	 * Function for updating the main Vectors this Class provides.
	 * 
	 * @param goapUnit
	 *            the Unit that is currently trying to retreat.
	 */
	private void updateVectors(IGoapUnit goapUnit) {
		this.vecEU = this.generateVectorFromEnemyToUnit(goapUnit,
				((PlayerUnit) goapUnit).getClosestEnemyUnitInConfidenceRange());
		this.vecUTP = this.generateVectorUnitToRetreatPosition(this.vecEU, goapUnit);
	}

	/**
	 * Function for generating a Vector from an (enemy) Unit to this current
	 * Unit.
	 * 
	 * @param goapUnit
	 *            the Unit at which the Vector will point.
	 * @param enemyUnit
	 *            the Unit the Vector will emerge from.
	 * @return a Vector pointing from an enemy Unit to the provided Unit.
	 */
	protected Vector generateVectorFromEnemyToUnit(IGoapUnit goapUnit, Unit enemyUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		// uPos -> Unit Position, ePos -> Enemy Position
		int uPosX = playerUnit.getUnit().getPosition().getX();
		int uPosY = playerUnit.getUnit().getPosition().getY();
		int ePosX = enemyUnit.getX();
		int ePosY = enemyUnit.getY();

		return new Vector(ePosX, ePosY, uPosX - ePosX, uPosY - ePosY);
	}

	// TODO: UML REMOVE + RENAME
//	/**
//	 * Function for generating a (retreat) Vector from an incoming provided one,
//	 * whose end-Position is the start of the newly created Vector. The provided
//	 * Vector is projected onto a predefined length which shortens or lengthens
//	 * the outgoing (generated) Vector based the incoming Vector's length. The
//	 * returned Vector's length can not be larger than the specified length.
//	 *
//	 * @param incomingVector
//	 *            the Vector which is going to be projected onto a predefined
//	 *            length.
//	 * @return a Vector starting from the provided Vectors end-Position and has
//	 *         a length that represents the provided Vector's length in relation
//	 *         to the maximum possible length.
//	 */
//	protected Vector projectVectorOntoMaxLength(Vector incomingVector) {
//		double vecRangeMultiplier = (this.maxDistance - incomingVector.length()) / this.maxDistance;
//		double neededDistanceMultiplier = this.maxDistance / incomingVector.length();
//
//		// The direction-Vector is projected on the maxDistance and then
//		// combined with the rangeMultiplier to receive a representation of
//		// the distance between the enemyUnit and the currentUnit based on
//		// their distance to another.
//		int tPosX = (int) (vecRangeMultiplier * neededDistanceMultiplier * incomingVector.getDirX());
//		int tPosY = (int) (vecRangeMultiplier * neededDistanceMultiplier * incomingVector.getDirY());
//
//		return new Vector(incomingVector.getX() + (int) (incomingVector.getDirX()),
//				incomingVector.getY() + (int) (incomingVector.getDirY()), tPosX, tPosY);
//	}
	
	// TODO: UML ADD
	/**
	 * Function for generating a Vector emerging from the executing Unit with the same direction Vector as a provided incoming Vector.
	 * @param incomingVector the Vector whose directions are projected onto the executing Unit's Position.
	 * @param goapUnit the currently executing Unit.
	 * @return a Vector emerging from the executing Unit's Position with the directions of a provided Vector.
	 */
	protected Vector generateVectorUnitToRetreatPosition(Vector incomingVector, IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		// uPos -> Unit Position
		int uPosX = playerUnit.getUnit().getPosition().getX();
		int uPosY = playerUnit.getUnit().getPosition().getY();

		// Generate a Vector starting from the goapUnit itself with the
		// direction Vector from the enemy Unit to the goapUnit applied.
		return new Vector(uPosX, uPosY, incomingVector.getDirX(), incomingVector.getDirY());
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
	protected void resetSpecific() {
		this.retreatPosition = null;
		this.generatedTempRetreatPosition = null;
	}
}
