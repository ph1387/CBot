package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;

import bwapi.Color;
import bwapi.Position;
import bwapiMath.Vector;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatAction_GeneralSuperclass.java --- Superclass for RetreatActions.
 * 
 * @author P H - 10.03.2017
 *
 */
public abstract class RetreatActionGeneralSuperclass extends BaseAction {
	private static final int DIST_TO_GATHERING_POINT = Core.getInstance().getTileSize();
	protected static final int TILE_RADIUS_NEAR = 1;
	// Has to be larger than DIST_TO_GATHERING_POINT
	// -> isDone() condition! v
	protected static final int MIN_PIXELDISTANCE_TO_UNIT = 320; // 240
	protected static final int MAX_PIXELDISTANCE_TO_UNIT = 20 * Core.getInstance().getTileSize(); // 15

	protected static HashSet<Position> gatheringPoints = new HashSet<Position>();

	protected Position generatedTempRetreatPosition = null;
	protected Position retreatPosition = null;

	// Vector related stuff
	protected static final int ALPHA_MAX = 90;
	protected double maxDistance = PlayerUnit.CONFIDENCE_TILE_RADIUS * Core.getInstance().getTileSize();
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

		// TODO: DEBUG INFO
		// // Position to which the Unit retreats to
		Core.getInstance().getGame().drawLineMap(((PlayerUnit) goapUnit).getUnit().getPosition(), this.retreatPosition,
				new Color(255, 255, 0));

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
			this.updateVecEU(goapUnit);
			this.updateVecUTP();

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
			// Targeted retreat-Position
			bwapi.Unit unit = ((PlayerUnit) goapUnit).getUnit();
			Position targetEndPosition = new Position(vecUTP.getX() + (int) (vecUTP.dirX),
					vecUTP.getY() + (int) (vecUTP.dirY));
			Core.getInstance().getGame().drawLineMap(unit.getPosition(), targetEndPosition, new Color(255, 128, 255));
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
	 * Used for updating the Vector from the closest enemy Unit in the
	 * confidence range to the PlayerUnit.
	 * 
	 * @param goapUnit
	 *            the Unit whose Vectors are being calculated.
	 */
	private void updateVecEU(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;

		// uPos -> Unit Position, ePos -> Enemy Position
		int uPosX = playerUnit.getUnit().getPosition().getX();
		int uPosY = playerUnit.getUnit().getPosition().getY();
		int ePosX = playerUnit.getClosestEnemyUnitInConfidenceRange().getPosition().getX();
		int ePosY = playerUnit.getClosestEnemyUnitInConfidenceRange().getPosition().getY();

		this.vecEU = new Vector(ePosX, ePosY, uPosX - ePosX, uPosY - ePosY);
	}

	/**
	 * Used for updating the Vector from the PlayerUnit to a possible retreat
	 * position.
	 */
	private void updateVecUTP() {
		double vecRangeMultiplier = (this.maxDistance - vecEU.length()) / this.maxDistance;
		double neededDistanceMultiplier = this.maxDistance / vecEU.length();

		// The direction-Vector is projected on the maxDistance and then
		// combined with the rangeMultiplier to receive a representation of
		// the distance between the enemyUnit and the currentUnit based on
		// their distance to another.
		int tPosX = (int) (vecRangeMultiplier * neededDistanceMultiplier * vecEU.dirX);
		int tPosY = (int) (vecRangeMultiplier * neededDistanceMultiplier * vecEU.dirY);

		this.vecUTP = new Vector(this.vecEU.getX(), this.vecEU.getY(), tPosX, tPosY);
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
