package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashSet;
import java.util.List;

import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatActionSteerInGoalDirection.java --- A Retreat Action with which a
 * PlayerUnit (!) moves away from an enemy. This Action's retreat Path has a
 * fixed length and is turned around the Unit until not collision with the map's
 * boundaries are detected. ChokePoints are ignored in this calculation. <br>
 * <b>Notice:</b> <br>
 * The length of the retreat path being taken has to be greater than the
 * DIST_TO_GATHERING_POINT from the superclass of this Action since this
 * determines when the Action is finished!
 * 
 * @author P H - 05.06.2017
 *
 */
public class RetreatActionSteerInGoalDirection extends RetreatActionGeneralSuperclass {
	private static final double TOTAL_RETREAT_DISTANCE = 96;
	private static final int TURN_RADIUS = 10;

	// Different influence sources for Vector calculations. Higher numbers
	// indicate a larger impact in the specific sector.
	private static final double INFLUENCE_INITIAL = 0.1;
	private static final double INFLUENCE_CHOKEPOINT = 1.4;
	private static final double INFLUENCE_ENEMIES = 0.9;
	private static final double INFLUENCE_BASE = 0.3;
	private static final double INFLUENCE_COMPANIONS = 0.6;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionSteerInGoalDirection(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		boolean precondtionsMet = false;

		// Position missing -> Action not performed yet.
		if (this.retreatPosition == null) {
			Chokepoint nearestChoke = BWTA.getNearestChokepoint(((PlayerUnit) goapUnit).getUnit().getPosition());
			Pair<Region, Polygon> matchingRegionPolygonPair = findBoundariesPositionIsIn(
					((PlayerUnit) goapUnit).getUnit().getPosition());
			Polygon currentPolygon = matchingRegionPolygonPair.second;

			// Use a generalized Vector which combines all direction-Vectors
			// from all sources influencing the Unit. This generalized Vector is
			// the retreat Vector emerging from the Unit in regards to the
			// closest enemy Unit in it's confidence range.
			Vector generalizedTargetVector = this.vecUTP.clone();
			generalizedTargetVector.normalize();
			generalizedTargetVector.dirX *= INFLUENCE_INITIAL;
			generalizedTargetVector.dirY *= INFLUENCE_INITIAL;

			// Update the direction of the generalized Vector based on various
			// influences.
			this.changeVecBaseOnChokePoints(generalizedTargetVector, goapUnit, matchingRegionPolygonPair);
			this.changeVecBasedOnEnemies(generalizedTargetVector, goapUnit);
			this.changeVecBasedOnStartingLocation(generalizedTargetVector, goapUnit);
			this.changeVecBasedOnStrongestPlayerArea(generalizedTargetVector, goapUnit);

			// Use the generalized Vector to find a valid retreat Position using
			// the previously generalized Vector as main steering direction.
			Vector possibleRetreatVector = this.generateSteeringRetreatVector(goapUnit, generalizedTargetVector,
					currentPolygon, nearestChoke);

			// Use the Vector's end-Position as retreat-Position.
			if (possibleRetreatVector != null) {
				this.generatedTempRetreatPosition = new Position(
						possibleRetreatVector.getX() + (int) (possibleRetreatVector.dirX),
						possibleRetreatVector.getY() + (int) (possibleRetreatVector.dirY));

				precondtionsMet = true;
			}
		}
		// Position known -> Action performed once.
		else {
			precondtionsMet = ((PlayerUnit) goapUnit).getUnit().hasPath(this.retreatPosition);
		}

		return precondtionsMet;
	}

	/**
	 * Function for changing a given Vector's direction properties based on a
	 * ChokePoint's direction towards the Unit itself. This direction is a
	 * Vector which gets normalized whilst multiplying its direction properties
	 * with predefined values.
	 * 
	 * @param targetVector
	 *            the Vector whose direction properties are going to be changed.
	 * @param goapUnit
	 *            the Unit from which the Vector pointing towards the ChokePoint
	 *            is starting at.
	 * @param matchingRegionPolygonPair
	 *            a Pair of the Region and Polygon that the Unit is currently
	 *            in.
	 */
	private void changeVecBaseOnChokePoints(Vector targetVector, IGoapUnit goapUnit,
			Pair<Region, Polygon> matchingRegionPolygonPair) {
		Chokepoint farthestChoke = this.getFarthestChokePoint(goapUnit,
				matchingRegionPolygonPair.first.getChokepoints());

		if (farthestChoke != null) {
			Unit unit = ((PlayerUnit) goapUnit).getUnit();
			Vector vecUnitToChokePoint = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
					farthestChoke.getCenter().getX() - unit.getPosition().getX(),
					farthestChoke.getCenter().getY() - unit.getPosition().getY());

			if (vecUnitToChokePoint.length() > 0.) {
				vecUnitToChokePoint.normalize();
				targetVector.dirX += vecUnitToChokePoint.dirX * INFLUENCE_CHOKEPOINT;
				targetVector.dirY += vecUnitToChokePoint.dirY * INFLUENCE_CHOKEPOINT;
			}
		}
	}

	/**
	 * Function for determining the farthest ChokePoint from a given List of
	 * ChokePoints.
	 * 
	 * @param goapUnit
	 *            the Unit to which the farthest ChokePoint is being chosen.
	 * @param chokePoints
	 *            the List of ChokePoints from which the farthest one towards
	 *            the provided Unit is being chosen.
	 * @return the ChokePoint from the List of given ChokePoints that has the
	 *         largest distance to the given Unit.
	 */
	private Chokepoint getFarthestChokePoint(IGoapUnit goapUnit, List<Chokepoint> chokePoints) {
		Chokepoint furthestChoke = null;
		Unit unit = ((PlayerUnit) goapUnit).getUnit();

		for (Chokepoint chokePoint : chokePoints) {
			if (furthestChoke == null
					|| unit.getDistance(chokePoint.getCenter()) > unit.getDistance(furthestChoke.getCenter())) {
				furthestChoke = chokePoint;
			}
		}
		return furthestChoke;
	}

	/**
	 * Function for changing a given Vector's direction properties based on
	 * enemies direction towards the Unit itself. This direction is a Vector
	 * which gets normalized whilst multiplying its direction properties with
	 * predefined values.
	 * 
	 * @param targetVector
	 *            the Vector whose direction properties are going to be changed.
	 * @param goapUnit
	 *            the Unit from which the Vector pointing towards the enemies is
	 *            starting at.
	 */
	private void changeVecBasedOnEnemies(Vector targetVector, IGoapUnit goapUnit) {
		HashSet<Unit> enemiesInConfidenceRange = ((PlayerUnit) goapUnit).getAllEnemyUnitsInConfidenceRange();
		
		for (Unit unit : enemiesInConfidenceRange) {
			Vector retreatVectorFromUnit = this
					.projectVectorOntoMaxLength(this.generateVectorFromEnemyToUnit(goapUnit, unit));

			if (retreatVectorFromUnit.length() > 0.) {
				retreatVectorFromUnit.normalize();
				targetVector.dirX += retreatVectorFromUnit.dirX * INFLUENCE_ENEMIES;
				targetVector.dirY += retreatVectorFromUnit.dirY * INFLUENCE_ENEMIES;
			}
		}
	}

	/**
	 * Function for changing a given Vector's direction properties based on the
	 * Player's starting location direction towards the Unit itself. This
	 * direction is a Vector which gets normalized whilst multiplying its
	 * direction properties with predefined values.
	 * 
	 * @param targetVector
	 *            the Vector whose direction properties are going to be changed.
	 * @param goapUnit
	 *            the Unit from which the Vector pointing towards the Player's
	 *            starting location is starting at.
	 */
	private void changeVecBasedOnStartingLocation(Vector targetVector, IGoapUnit goapUnit) {
		TilePosition playerStartingLocation = Core.getInstance().getPlayer().getStartLocation();
		
		if (playerStartingLocation != null) {
			Unit unit = ((PlayerUnit) goapUnit).getUnit();
			Vector vecToBaseLocation = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
					playerStartingLocation.toPosition().getX() - unit.getPosition().getX(),
					playerStartingLocation.toPosition().getY() - unit.getPosition().getY());

			if (vecToBaseLocation.length() > 0.) {
				vecToBaseLocation.normalize();
				targetVector.dirX += vecToBaseLocation.dirX * INFLUENCE_BASE;
				targetVector.dirY += vecToBaseLocation.dirY * INFLUENCE_BASE;
			}
		}
	}

	/**
	 * Function for changing a given Vector's direction properties based on the
	 * Player Unit with the "strongest" area around it towards the Unit itself.
	 * This direction is a Vector which gets normalized whilst multiplying its
	 * direction properties with predefined values.
	 * 
	 * @param targetVector
	 *            the Vector whose direction properties are going to be changed.
	 * @param goapUnit
	 *            the Unit from which the Vector pointing towards the
	 *            "strongest" area around a Unit is starting at.
	 */
	private void changeVecBasedOnStrongestPlayerArea(Vector targetVector, IGoapUnit goapUnit) {
		Unit unitWithStrongestArea = getUnitWithGreatestTileStrengths(
				getPlayerUnitsInIncreasingRange((PlayerUnit) goapUnit), goapUnit);

		if (unitWithStrongestArea != null && unitWithStrongestArea != ((PlayerUnit) goapUnit).getUnit()) {
			Unit unit = ((PlayerUnit) goapUnit).getUnit();
			Vector vecToStrongestUnitArea = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
					unitWithStrongestArea.getPosition().getX() - unit.getPosition().getX(),
					unitWithStrongestArea.getPosition().getY() - unit.getPosition().getY());

			if (vecToStrongestUnitArea.length() > 0.) {
				vecToStrongestUnitArea.normalize();
				targetVector.dirX += vecToStrongestUnitArea.dirX * INFLUENCE_COMPANIONS;
				targetVector.dirY += vecToStrongestUnitArea.dirY * INFLUENCE_COMPANIONS;
			}
		}
	}

	/**
	 * Function for generating a steering Vector based on a generalized Vector
	 * which holds all necessary directions to all predefined important
	 * locations. This function returns a Vector which lies inside the
	 * accessible map space and follows the map's borders if necessary. If the
	 * boundaries are being met, the function will create two new Vectors, one
	 * pointing X degrees to the left and one pointing X degrees to the right,
	 * which in return are getting checked. This continues until either X >= 180
	 * or one Vector is not intersecting with the map's boundaries and therefore
	 * can be followed by the Unit. The Vector parameter holds all necessary
	 * information regarding the direction the Unit is initially looking.
	 * 
	 * @param goapUnit
	 *            the Unit from whose Position the Vectors are being casted.
	 * @param generalizedTargetVector
	 *            the Vector which holds all necessary direction information
	 *            that are being resembled.
	 * @param currentPolygon
	 *            the Polygon the Unit is currently in.
	 * @param nearestChoke
	 *            the closest ChokePoint to the Unit.
	 * @return a Vector to which's end-Position the Unit can retreat to.
	 */
	private Vector generateSteeringRetreatVector(IGoapUnit goapUnit, Vector generalizedTargetVector,
			Polygon currentPolygon, Chokepoint nearestChoke) {
		Vector possibleRetreatVector = null;
		int totalVectorTurnDegrees = 0;

		// Generate two Vectors with the specified length.
		Vector vecLeft = generalizedTargetVector.clone();
		Vector vecRight = generalizedTargetVector.clone();
		vecLeft.setToLength(TOTAL_RETREAT_DISTANCE);
		vecRight.setToLength(TOTAL_RETREAT_DISTANCE);

		// Test if any intersections are found along the retreat path.
		// TODO: Possible Change: Remove hasPath check.
		// NOTICE:
		// The "hasPath" test is only used because some intersections were not
		// found, which caused the Unit to simply abort the retreat Action and
		// stand on one Point waiting for the enemy to kill it.
		if (currentPolygon.findIntersections(vecLeft).isEmpty() && ((PlayerUnit) goapUnit).getUnit()
				.hasPath(new Position(vecLeft.getX() + (int) (vecLeft.dirX), vecLeft.getY() + (int) (vecLeft.dirY)))) {
			possibleRetreatVector = vecLeft; // OR vecRight, does not matter
		} else if (nearestChoke != null) {
			boolean tryFindingPath = true;

			// Turn the Vectors around the Unit until one of them does not
			// intersect with the map's boundaries.
			while (possibleRetreatVector == null && tryFindingPath) {
				// Turn the Vectors around.
				vecLeft.rotateLeftDEG(TURN_RADIUS);
				vecRight.rotateRightDEG(TURN_RADIUS);
				totalVectorTurnDegrees += TURN_RADIUS;

				// If both Vectors were turned more than 180° degrees, abort the
				// loop, since no Position can be found.
				if (totalVectorTurnDegrees >= 180) {
					tryFindingPath = false;
				}

				// Gather the intersections with the map's boundaries.
				List<Pair<Vector, Point>> intersectionsVecLeft = currentPolygon.findIntersections(vecLeft);
				List<Pair<Vector, Point>> intersectionsVecRight = currentPolygon.findIntersections(vecRight);

				// Determine the end-Points of the Vectors.
				Position vecLeftEndPosition = new Position(vecLeft.getX() + (int) (vecLeft.dirX),
						vecLeft.getY() + (int) (vecLeft.dirY));
				Position vecRightEndPosition = new Position(vecRight.getX() + (int) (vecRight.dirX),
						vecRight.getY() + (int) (vecRight.dirY));

				// The Unit prefers to go left. Also test if the found
				// intersections belong to a ChokePoint, which the Unit can move
				// through.
				boolean vecLeftValid = (intersectionsVecLeft.isEmpty()
						&& ((PlayerUnit) goapUnit).getUnit().hasPath(vecLeftEndPosition))
						|| this.doesAOneIntersectionsBelongToChokePoint(nearestChoke, intersectionsVecLeft);
				boolean vecRightValid = (intersectionsVecRight.isEmpty()
						&& ((PlayerUnit) goapUnit).getUnit().hasPath(vecRightEndPosition))
						|| this.doesAOneIntersectionsBelongToChokePoint(nearestChoke, intersectionsVecRight);

				if (!this.isEndPositionBlockedByNeutralOrBuilding(vecLeft) && vecLeftValid) {
					possibleRetreatVector = vecLeft;
				} else if (!this.isEndPositionBlockedByNeutralOrBuilding(vecRight) && vecRightValid) {
					possibleRetreatVector = vecRight;
				}

				// TODO: DEBUG INFO
				// Position to which the Unit retreats to
				// Core.getInstance().getGame().drawLineMap(((PlayerUnit)
				// goapUnit).getUnit().getPosition(),
				// new Position((int)(vecLeft.getX() + vecLeft.dirX),
				// (int)(vecLeft.getY() + vecLeft.dirY)), new Color(255, 0, 0));
				// Core.getInstance().getGame().drawLineMap(((PlayerUnit)
				// goapUnit).getUnit().getPosition(),
				// new Position((int)(vecRight.getX() + vecRight.dirX),
				// (int)(vecRight.getY() + vecRight.dirY)), new Color(0, 0,
				// 255));
			}
		}

		return possibleRetreatVector;
	}

	/**
	 * Function for checking if any neutral Units or buildings are blocking a
	 * Vector's end-Position.
	 * 
	 * @param vector
	 *            the Vector whose end-Position is being checked.
	 * @return true or false depending if the end-Position is being blocked or
	 *         not.
	 */
	private boolean isEndPositionBlockedByNeutralOrBuilding(Vector vector) {
		Position endPositionVec = new Position(vector.getX() + (int) (vector.dirX),
				vector.getY() + (int) (vector.dirY));
		List<Unit> endTilePositionVecRightUnits = Core.getInstance().getGame()
				.getUnitsOnTile(endPositionVec.toTilePosition());
		boolean blocked = false;

		// Filter the Units that are on these TilePosition. only neutral Units
		// and buildings count.
		for (Unit unit : endTilePositionVecRightUnits) {
			if (unit.getType().isBuilding() || unit.getType().isNeutral()) {
				blocked = true;
				break;
			}
		}
		return blocked;
	}

	/**
	 * Function for determining if any intersection of a List of provided
	 * intersections belongs to a ChokePoint.
	 * 
	 * @param chokePoint
	 *            the ChokePoint that is going to be tested.
	 * @param intersections
	 *            the intersections from which at least one must belong to the
	 *            provided ChokePoint.
	 * @return true or false depending if one of the intersections belongs to
	 *         the provided ChokePoint.
	 */
	private boolean doesAOneIntersectionsBelongToChokePoint(Chokepoint chokePoint,
			List<Pair<Vector, Point>> intersections) {
		boolean success = false;

		for (Pair<Vector, Point> intersection : intersections) {
			if (this.doesIntersectionBelongToChokePoint(chokePoint, intersection)) {
				success = true;
				break;
			}
		}
		return success;
	}

	/**
	 * Function for determining if an intersection belongs to a ChokePoint.
	 * 
	 * @param chokePoint
	 *            the ChokePoint that the intersection is going to be tested
	 *            against.
	 * @param intersection
	 *            the intersection that is going to be tested against the
	 *            provided ChokePoint.
	 * @return true or false depending if the intersection belongs to the
	 *         provided ChokePoint.
	 */
	private boolean doesIntersectionBelongToChokePoint(Chokepoint chokePoint, Pair<Vector, Point> intersection) {
		// Test if the intersection belongs to a ChokePoint.
		Position chokePositionOne = chokePoint.getSides().first;
		Position chokePositionTwo = chokePoint.getSides().second;

		Vector chokeAsVec = new Vector(chokePositionOne.getX(), chokePositionOne.getY(),
				chokePositionTwo.getX() - chokePositionOne.getX(), chokePositionTwo.getY() - chokePositionOne.getY());
		Double neededChokeMultiplier = chokeAsVec.getNeededMultiplier(intersection.second);

		// If the intersection actually belongs to the ChokePoint, then the
		// needed multiplier to display that is in between 0 and 1 (and not
		// null).
		if (neededChokeMultiplier != null && neededChokeMultiplier >= 0. && neededChokeMultiplier <= 1.) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return (float) TOTAL_RETREAT_DISTANCE;
	}

}
