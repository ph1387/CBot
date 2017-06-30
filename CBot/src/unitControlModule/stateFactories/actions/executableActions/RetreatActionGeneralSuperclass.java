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
	// TODO: UML REMOVE
//	private static final int EXPAND_MULTIPLIER_MAX = 5;
	// TODO: UML REMOVE
//	private static final int TILE_RADIUS_AROUND_UNITS_SEARCH = 1;

	// Has to be smaller than MIN_PIXELDISTANCE_TO_UNIT!
	// -> isDone() condition! v
	private static final int DIST_TO_GATHERING_POINT = Core.getInstance().getTileSize();
	protected static final int TILE_RADIUS_NEAR = 1;
	// TODO: UML REMOVE
//	protected static final int MIN_PIXELDISTANCE_TO_UNIT = 320; // 240
	// TODO: UML REMOVE
//	protected static final int MAX_PIXELDISTANCE_TO_UNIT = 20 * Core.getInstance().getTileSize(); // 15

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
		this.vecUTP = this.projectVectorOntoMaxLength(this.vecEU);
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

	/**
	 * Function for generating a (retreat) Vector from an incoming provided one,
	 * whose end-Position is the start of the newly created Vector. The provided
	 * Vector is projected onto a predefined length which shortens or lengthens
	 * the outgoing (generated) Vector based the incoming Vector's length. The
	 * returned Vector's length can not be larger than the specified length.
	 *
	 * @param incomingVector
	 *            the Vector which is going to be projected onto a predefined
	 *            length.
	 * @return a Vector starting from the provided Vectors end-Position and has
	 *         a length that represents the provided Vector's length in relation
	 *         to the maximum possible length.
	 */
	protected Vector projectVectorOntoMaxLength(Vector incomingVector) {
		double vecRangeMultiplier = (this.maxDistance - incomingVector.length()) / this.maxDistance;
		double neededDistanceMultiplier = this.maxDistance / incomingVector.length();

		// The direction-Vector is projected on the maxDistance and then
		// combined with the rangeMultiplier to receive a representation of
		// the distance between the enemyUnit and the currentUnit based on
		// their distance to another.
		int tPosX = (int) (vecRangeMultiplier * neededDistanceMultiplier * incomingVector.getDirX());
		int tPosY = (int) (vecRangeMultiplier * neededDistanceMultiplier * incomingVector.getDirY());

		return new Vector(incomingVector.getX() + (int) (incomingVector.getDirX()),
				incomingVector.getY() + (int) (incomingVector.getDirY()), tPosX, tPosY);
	}

	// TODO: UML REMOVE
//	/**
//	 * Function for finding the Polygon that the Position is in.
//	 * 
//	 * @param position
//	 *            the Position that is being checked.
//	 * @return the Region and the Polygon that the Position is located in.
//	 */
//	public static Pair<Region, Polygon> findBoundariesPositionIsIn(Position position) {
//		Pair<Region, Polygon> matchingRegionPolygonPair = null;
//
//		// Search for the Pair of Regions and Polygons that includes the Unit's
//		// Position.
//		for (Pair<Region, Polygon> pair : CBot.getInstance().getInformationStorage().getMapInfo().getMapBoundaries()) {
//			if (pair.first.getPolygon().isInside(position)) {
//				matchingRegionPolygonPair = pair;
//				break;
//			}
//		}
//		return matchingRegionPolygonPair;
//	}

	// TODO: UML REMOVE
//	/**
//	 * Function for retrieving all Units in an increasing range around the given
//	 * PlayerUnit. The range at which the Units are searched for increases
//	 * stepwise until Units are found or the preset maximum is reached.
//	 * 
//	 * @param goapUnit
//	 *            the PlayerUnit that the search is based around.
//	 * @return a HashSet containing all Units in a range around the given
//	 *         PlayerUnit with at least minimum distance to it.
//	 */
//	public static HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit goapUnit) {
//		HashSet<Unit> unitsTooClose = new HashSet<Unit>();
//		HashSet<Unit> unitsInRange = new HashSet<Unit>();
//		int iterationCounter = 1;
//
//		// Increase range until a Unit is found or the threshold is reached.
//		while (unitsInRange.isEmpty() && iterationCounter <= EXPAND_MULTIPLIER_MAX) {
//			HashSet<Unit> foundUnits = goapUnit
//					.getAllPlayerUnitsInRange((int) (iterationCounter * MAX_PIXELDISTANCE_TO_UNIT));
//			HashSet<Unit> unitsToBeRemoved = new HashSet<Unit>();
//
//			// Test all found Units, a Unit has to have a minimum distance to
//			// the PlayerUnit.
//			for (Unit unit : foundUnits) {
//				if (!unitsTooClose.contains(unit)
//						&& goapUnit.getUnit().getDistance(unit.getPosition()) < MIN_PIXELDISTANCE_TO_UNIT) {
//					unitsToBeRemoved.add(unit);
//					unitsTooClose.add(unit);
//				}
//			}
//
//			for (Unit unit : unitsToBeRemoved) {
//				foundUnits.remove(unit);
//			}
//
//			unitsInRange.addAll(foundUnits);
//			iterationCounter++;
//		}
//		return unitsInRange;
//	}

	// TODO: UML REMOVE
//	/**
//	 * Function for retrieving the Unit with the greatest sum of strengths
//	 * around the units TilePosition.
//	 * 
//	 * @param units
//	 *            a HashSet containing all units which are going to be cycled
//	 *            through.
//	 * @param goapUnit
//	 *            the currently executing IGoapUnit.
//	 * @return the Unit with the greatest sum of strengths at its TilePosition.
//	 */
//	public static Unit getUnitWithGreatestTileStrengths(HashSet<Unit> units, IGoapUnit goapUnit) {
//		Unit bestUnit = null;
//		int bestUnitStrengthTotal = 0;
//
//		// Iterate over the Units and over their TilePositions in a specific
//		// radius.
//		for (Unit unit : units) {
//			int currentStrengths = 0;
//
//			for (int i = -TILE_RADIUS_AROUND_UNITS_SEARCH; i <= TILE_RADIUS_AROUND_UNITS_SEARCH; i++) {
//				for (int j = -TILE_RADIUS_AROUND_UNITS_SEARCH; j <= TILE_RADIUS_AROUND_UNITS_SEARCH; j++) {
//
//					// TODO: Possible Change: AirStrength Implementation
//					Integer value = ((PlayerUnit) goapUnit).getInformationStorage().getTrackerInfo()
//							.getPlayerGroundAttackTilePositions().get(new TilePosition(
//									unit.getTilePosition().getX() + i, unit.getTilePosition().getY() + j));
//
//					if (value != null) {
//						currentStrengths += value;
//					}
//				}
//			}
//
//			if (bestUnit == null || currentStrengths > bestUnitStrengthTotal) {
//				bestUnit = unit;
//				bestUnitStrengthTotal = currentStrengths;
//			}
//		}
//		return bestUnit;
//	}

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
