package unitControlModule.stateFactories.actions.executableActions;

import java.util.List;

import bwapi.Pair;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperation;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationChokePoints;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationEnemiesInConfidenceRange;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationStartingLocation;
import unitControlModule.stateFactories.actions.executableActions.steering.SteeringOperationStrongestPlayerArea;
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
	private static final double INFLUENCE_INITIAL = 1.2;
	private static final double INFLUENCE_CHOKEPOINT = 5.7;
	private static final double INFLUENCE_ENEMIES = 0.9;
	private static final double INFLUENCE_BASE = 0.3;
	private static final double INFLUENCE_COMPANIONS = 1.2;

	private SteeringOperation steeringChokePoints, steeringEnemiesInConfidenceRange, steeringStartingLocation,
			steeringStrongestPlayerArea;

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

		// Instantiate the different SteeringOperations that are being used.
		if (this.steeringChokePoints == null) {
			this.instantiateSteeringOperations(goapUnit);
		}

		try {
			// Position missing -> Action not performed yet.
			if (this.retreatPosition == null) {
				Chokepoint nearestChoke = BWTA.getNearestChokepoint(((PlayerUnit) goapUnit).getUnit().getPosition());
				Pair<Region, Polygon> matchingRegionPolygonPair = findBoundariesPositionIsIn(
						((PlayerUnit) goapUnit).getUnit().getPosition());
				Polygon currentPolygon = matchingRegionPolygonPair.second;

				// Use a generalized Vector which combines all direction-Vectors
				// from all sources influencing the Unit. This generalized
				// Vector is the retreat Vector emerging from the Unit in
				// regards to the closest enemy Unit in it's confidence range.
				Vector generalizedTargetVector = this.vecUTP.clone();
				generalizedTargetVector.normalize();
				generalizedTargetVector.setDirX(generalizedTargetVector.getDirX() * INFLUENCE_INITIAL);
				generalizedTargetVector.setDirY(generalizedTargetVector.getDirY() * INFLUENCE_INITIAL);

				// Update the direction of the generalized Vector based on
				// various influences.
				((SteeringOperationChokePoints) this.steeringChokePoints).setPolygonPairUnitIsIn(matchingRegionPolygonPair);
				this.steeringChokePoints.applySteeringForce(generalizedTargetVector, INFLUENCE_CHOKEPOINT);
				this.steeringEnemiesInConfidenceRange.applySteeringForce(generalizedTargetVector, INFLUENCE_ENEMIES);
				this.steeringStartingLocation.applySteeringForce(generalizedTargetVector, INFLUENCE_BASE);
				this.steeringStrongestPlayerArea.applySteeringForce(generalizedTargetVector, INFLUENCE_COMPANIONS);
				
				// Use the generalized Vector to find a valid retreat Position
				// using the previously generalized Vector as main steering
				// direction.
				Vector possibleRetreatVector = this.generateSteeringRetreatVector(goapUnit, generalizedTargetVector,
						currentPolygon, nearestChoke);

				// Use the Vector's end-Position as retreat-Position.
				if (possibleRetreatVector != null) {
					this.generatedTempRetreatPosition = new Position(
							possibleRetreatVector.getX() + (int) (possibleRetreatVector.getDirX()),
							possibleRetreatVector.getY() + (int) (possibleRetreatVector.getDirY()));

					precondtionsMet = true;
				}
			}
			// Position known -> Action performed once.
			else {
				precondtionsMet = ((PlayerUnit) goapUnit).getUnit().hasPath(this.retreatPosition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return precondtionsMet;
	}

	/**
	 * Function used for instantiating all SteertingOperations that are being
	 * used by the Action itself.
	 * 
	 * @param goapUnit
	 *            the Unit that is executing the Action.
	 */
	private void instantiateSteeringOperations(IGoapUnit goapUnit) {
		this.steeringChokePoints = new SteeringOperationChokePoints(goapUnit);
		this.steeringEnemiesInConfidenceRange = new SteeringOperationEnemiesInConfidenceRange(goapUnit);
		this.steeringStartingLocation = new SteeringOperationStartingLocation(goapUnit);
		this.steeringStrongestPlayerArea = new SteeringOperationStrongestPlayerArea(goapUnit);
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
		if (currentPolygon.findIntersections(vecLeft).isEmpty() && ((PlayerUnit) goapUnit).getUnit().hasPath(
				new Position(vecLeft.getX() + (int) (vecLeft.getDirX()), vecLeft.getY() + (int) (vecLeft.getDirY())))) {
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
				Position vecLeftEndPosition = new Position(vecLeft.getX() + (int) (vecLeft.getDirX()),
						vecLeft.getY() + (int) (vecLeft.getDirY()));
				Position vecRightEndPosition = new Position(vecRight.getX() + (int) (vecRight.getDirX()),
						vecRight.getY() + (int) (vecRight.getDirY()));

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
		Position endPositionVec = new Position(vector.getX() + (int) (vector.getDirX()),
				vector.getY() + (int) (vector.getDirY()));
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
