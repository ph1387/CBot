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
	private static final double INFLUENCE_INITIAL = 1.2;
	private static final double INFLUENCE_CHOKEPOINT = 5.7;
	private static final double INFLUENCE_ENEMIES = 0.9;
	private static final double INFLUENCE_BASE = 0.3;
	private static final double INFLUENCE_COMPANIONS = 1.2;

	// Index that is used while steering towards a ChokePoint. The index implies
	// the path TilePosition count to the ChokePoint's center that must be met
	// for using the path with the element at the provided index towards the
	// ChokePoint rather than the general Vector towards the center.
	private static final int CHOKE_POINT_PATH_INDEX = 4;

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
				this.changeVecBaseOnChokePoints(generalizedTargetVector, goapUnit, matchingRegionPolygonPair);
				this.changeVecBasedOnEnemies(generalizedTargetVector, goapUnit);
				this.changeVecBasedOnStartingLocation(generalizedTargetVector, goapUnit);
				this.changeVecBasedOnStrongestPlayerArea(generalizedTargetVector, goapUnit);

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
		try {
			Region regionToFallBackTo = ((PlayerUnit) goapUnit).getInformationStorage().getMapInfo()
					.getReversedRegionAccessOrder()
					.get(BWTA.getRegion(((PlayerUnit) goapUnit).getUnit().getPosition()));

			// Only change the Vectors direction if the Unit is not currently
			// inside the Player's starting region since this would cause the
			// Unit to uncontrollably circle around the closest ChokePoint.
			if (regionToFallBackTo != null) {
				Chokepoint closestChoke = this.findChokePointToRetreatTo(goapUnit, matchingRegionPolygonPair,
						regionToFallBackTo);

				if (closestChoke != null) {
					Unit unit = ((PlayerUnit) goapUnit).getUnit();

					// Get the shortest Path from the Unit to the ChokePoint.
					List<TilePosition> shortestPath = BWTA.getShortestPath(unit.getTilePosition(),
							closestChoke.getCenter().toTilePosition());
					Vector vecUnitToChokePoint = null;

					// Use the first TilePosition as direction for moving the
					// Unit to the ChokePoint. This is necessary since
					// generating a Vector directly towards the ChokePoint would
					// cause the Unit to move uncontrollably in some cases where
					// it is "trapped" in between two sides of the Polygon and
					// therefore moves left, right, left, right, ...
					if (shortestPath.size() > CHOKE_POINT_PATH_INDEX) {
						Position firstStep = shortestPath.get(CHOKE_POINT_PATH_INDEX).toPosition();
						vecUnitToChokePoint = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
								firstStep.getX() - unit.getPosition().getX(),
								firstStep.getY() - unit.getPosition().getY());
					}
					// Generate a Vector leading directly towards the
					// ChokePoint.
					else {
						vecUnitToChokePoint = new Vector(unit.getPosition().getX(), unit.getPosition().getY(),
								closestChoke.getCenter().getX() - unit.getPosition().getX(),
								closestChoke.getCenter().getY() - unit.getPosition().getY());
					}

					// Apply the influence to the targeted Vector.
					if (vecUnitToChokePoint.length() > 0.) {
						vecUnitToChokePoint.normalize();
						targetVector
								.setDirX(targetVector.getDirX() + vecUnitToChokePoint.getDirX() * INFLUENCE_CHOKEPOINT);
						targetVector
								.setDirY(targetVector.getDirY() + vecUnitToChokePoint.getDirY() * INFLUENCE_CHOKEPOINT);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function for determining the ChokePoint which the Unit must travel to to
	 * get closer towards the Player's starting location.
	 * 
	 * @param goapUnit
	 *            the Unit that is going to retreat.
	 * @param matchingRegionPolygonPair
	 *            a Pair of the Region and Polygon that the Unit is currently
	 *            in.
	 * @param regionToFallBackTo
	 *            the next Region the Unit has to travel to in order to move
	 *            towards the Player's starting location.
	 * @return the ChokePoint from the List of given ChokePoints that leads
	 *         towards the Player's starting location.
	 */
	private Chokepoint findChokePointToRetreatTo(IGoapUnit goapUnit, Pair<Region, Polygon> matchingRegionPolygonPair,
			Region regionToFallBackTo) {
		Chokepoint retreatChokePoint = null;
		Position centerPlayerStartingRegion = BWTA
				.getRegion(BWTA.getStartLocation(Core.getInstance().getPlayer()).getTilePosition()).getCenter();

		for (Chokepoint chokePoint : matchingRegionPolygonPair.first.getChokepoints()) {
			if (chokePoint.getRegions().first.equals(regionToFallBackTo)
					|| chokePoint.getRegions().second.equals(regionToFallBackTo)) {
				// The Region might be connected to the other Region by two
				// separate ChokePoints. The closest one towards the Player's
				// starting location is being chosen.
				if (retreatChokePoint == null || retreatChokePoint.getDistance(centerPlayerStartingRegion) > chokePoint
						.getDistance(centerPlayerStartingRegion)) {
					retreatChokePoint = chokePoint;
				}
			}
		}

		return retreatChokePoint;
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

			// Apply the influence to the targeted Vector.
			if (retreatVectorFromUnit.length() > 0.) {
				retreatVectorFromUnit.normalize();
				targetVector.setDirX(targetVector.getDirX() + retreatVectorFromUnit.getDirX() * INFLUENCE_ENEMIES);
				targetVector.setDirY(targetVector.getDirY() + retreatVectorFromUnit.getDirY() * INFLUENCE_ENEMIES);
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

			// Apply the influence to the targeted Vector.
			if (vecToBaseLocation.length() > 0.) {
				vecToBaseLocation.normalize();
				targetVector.setDirX(targetVector.getDirX() + vecToBaseLocation.getDirX() * INFLUENCE_BASE);
				targetVector.setDirY(targetVector.getDirY() + vecToBaseLocation.getDirY() * INFLUENCE_BASE);
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

			// Apply the influence to the targeted Vector.
			if (vecToStrongestUnitArea.length() > 0.) {
				vecToStrongestUnitArea.normalize();
				targetVector.setDirX(targetVector.getDirX() + vecToStrongestUnitArea.getDirX() * INFLUENCE_COMPANIONS);
				targetVector.setDirY(targetVector.getDirY() + vecToStrongestUnitArea.getDirY() * INFLUENCE_COMPANIONS);
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
