package unitControlModule.stateFactories.actions.executableActions.steering;

import java.util.List;

import bwapi.Pair;
import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Line;
import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.Chokepoint;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * SteeringFactory.java --- Factory for generating the final version of a
 * steering Vector. The factory consists of various functions of which the main
 * one
 * ({@link #transformSteeringVector(IGoapUnit, Vector, Polygon, Chokepoint, double, double)})
 * transforms a steering Vector in a way in which it's end Position is located
 * inside the accessible map.
 * 
 * @author P H - 30.06.2017
 *
 */
public class SteeringFactory {

	// -------------------- Functions

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
	 * @param toalRetreatDistance
	 *            the total length the generated Vector will have.
	 * @param turnRadius
	 *            the turn radius in <b>degrees</b> that each Vector will be
	 *            turned in one single iteration.
	 * @return a Vector to which's end-Position the Unit can retreat to.
	 */
	public static Vector transformSteeringVector(IGoapUnit goapUnit, Vector generalizedTargetVector,
			Polygon currentPolygon, Chokepoint nearestChoke, double toalRetreatDistance, double turnRadius) {
		Vector possibleRetreatVector = null;
		int totalVectorTurnDegrees = 0;

		// Generate two Vectors with the specified length.
		Vector vecLeft = generalizedTargetVector.clone();
		Vector vecRight = generalizedTargetVector.clone();
		vecLeft.setToLength(toalRetreatDistance);
		vecRight.setToLength(toalRetreatDistance);

		// Test if any intersections are found along the retreat path.
		// TODO: Possible Change: Remove hasPath check.
		// NOTICE:
		// The "hasPath" test is only used because some intersections were not
		// found, which caused the Unit to simply abort the retreat Action and
		// stand on one Point waiting for the enemy to kill it.
		if (currentPolygon.findIntersections(new Line(vecLeft)).isEmpty() && ((PlayerUnit) goapUnit).getUnit().hasPath(
				new Position(vecLeft.getX() + (int) (vecLeft.getDirX()), vecLeft.getY() + (int) (vecLeft.getDirY())))) {
			possibleRetreatVector = vecLeft; // OR vecRight, does not matter
		} else if (nearestChoke != null) {
			boolean tryFindingPath = true;

			// Turn the Vectors around the Unit until one of them does not
			// intersect with the map's boundaries.
			while (possibleRetreatVector == null && tryFindingPath) {
				// Turn the Vectors around.
				vecLeft.rotateLeftDEG(turnRadius);
				vecRight.rotateRightDEG(turnRadius);
				totalVectorTurnDegrees += turnRadius;

				// If both Vectors were turned more than 180° degrees, abort the
				// loop, since no Position can be found.
				if (totalVectorTurnDegrees >= 180) {
					tryFindingPath = false;
				}

				// Gather the intersections with the map's boundaries.
				List<Pair<Line, Point>> intersectionsVecLeft = currentPolygon.findIntersections(new Line(vecLeft));
				List<Pair<Line, Point>> intersectionsVecRight = currentPolygon.findIntersections(new Line(vecRight));

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
						|| doesOneIntersectionBelongToChokePoint(nearestChoke, intersectionsVecLeft);
				boolean vecRightValid = (intersectionsVecRight.isEmpty()
						&& ((PlayerUnit) goapUnit).getUnit().hasPath(vecRightEndPosition))
						|| doesOneIntersectionBelongToChokePoint(nearestChoke, intersectionsVecRight);

				// Ensure that a generated Position at the end of the Vector
				// is actually a valid Point inside the map's boundaries. This
				// check is necessary since other checks depend on comparing
				// the boundaries of the checked Positions. Therefore these must
				// NOT be null!
				boolean vecLeftEndPositionValid = BaseAction.findBoundariesPositionIsIn(vecLeftEndPosition) != null;
				boolean vecRightEndPositionValid = BaseAction.findBoundariesPositionIsIn(vecRightEndPosition) != null;

				if (!isEndPositionBlockedByNeutralOrBuilding(vecLeft) && vecLeftValid && vecLeftEndPositionValid) {
					possibleRetreatVector = vecLeft;
				} else if (!isEndPositionBlockedByNeutralOrBuilding(vecRight) && vecRightValid
						&& vecRightEndPositionValid) {
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

	// TODO: UML CHANGE PARAMS
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
	private static boolean doesOneIntersectionBelongToChokePoint(Chokepoint chokePoint,
			List<Pair<Line, Point>> intersections) {
		boolean success = false;

		for (Pair<Line, Point> intersection : intersections) {
			if (doesIntersectionBelongToChokePoint(chokePoint, intersection)) {
				success = true;
				break;
			}
		}
		return success;
	}

	// TODO: UML CHANGE PARAMS
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
	private static boolean doesIntersectionBelongToChokePoint(Chokepoint chokePoint, Pair<Line, Point> intersection) {
		Position chokePositionOne = chokePoint.getSides().first;
		Position chokePositionTwo = chokePoint.getSides().second;

		return (new Line(new Point(chokePositionOne), new Point(chokePositionTwo))).contains(intersection.second);
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
	public static boolean isEndPositionBlockedByNeutralOrBuilding(Vector vector) {
		Position endPositionVec = new Position(vector.getX() + (int) (vector.getDirX()),
				vector.getY() + (int) (vector.getDirY()));

		return isEndPositionBlockedByNeutralOrBuilding(endPositionVec);
	}

	/**
	 * Function for checking if any neutral Units or buildings are blocking a
	 * Position.
	 * 
	 * @param position
	 *            the Position that will be checked for buildings and neutral
	 *            Units.
	 * @return true or false depending if the Position is being blocked or not.
	 */
	public static boolean isEndPositionBlockedByNeutralOrBuilding(Position position) {
		List<Unit> endTilePositionVecRightUnits = Core.getInstance().getGame()
				.getUnitsOnTile(position.toTilePosition());
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
}
