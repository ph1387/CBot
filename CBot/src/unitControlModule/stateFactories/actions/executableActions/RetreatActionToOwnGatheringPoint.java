package unitControlModule.stateFactories.actions.executableActions;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bwapi.Color;
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
import core.CBot;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatAction_ToOwnGatheringPoint.java --- An action with which a PlayerUnit
 * (!) retreats to a self created gathering point.
 * 
 * @author P H - 10.03.2017
 *
 */
public class RetreatActionToOwnGatheringPoint extends RetreatActionGeneralSuperclass {
	private static final int EXPAND_MULTIPLIER_MAX = 2;
	private static final int TILE_RADIUS_AROUND_UNITS_SEARCH = 1;
	// UML
	private static final int MIN_VERTEX_OFFSET = 0;

	/**
	 * @param target
	 *            type: Unit
	 */
	public RetreatActionToOwnGatheringPoint(Object target) {
		super(target);
	}

	@Override
	protected boolean checkProceduralSpecificPrecondition(IGoapUnit goapUnit) {
		Vector vecUTP = ((PlayerUnit) goapUnit).getVecUTP();
		boolean precondtionsMet = true;

		// Position missing -> action not performed yet
		if (this.retreatPosition == null) {
			Unit retreatableUnit = this.getUnitWithGreatestTileStrengths(
					this.getPlayerUnitsInIncreasingRange((PlayerUnit) goapUnit), goapUnit);
			Vector usedVector = null;

			if (retreatableUnit == null) {
				usedVector = vecUTP;
			} else {
				// Create a Vector from the current Unit to the found Unit.
				Vector vecToUnit = new Vector(vecUTP.getX(), vecUTP.getY(),
						retreatableUnit.getPosition().getX() - vecUTP.getX(),
						retreatableUnit.getPosition().getY() - vecUTP.getY());
				vecToUnit.normalize();

				// Generate a Vector from the normalized Vector to the found
				// Unit with a fixed length.
				usedVector = new Vector(vecToUnit.getX(), vecToUnit.getY(),
						(int) (vecToUnit.dirX * MIN_PIXELDISTANCE_TO_UNIT),
						(int) (vecToUnit.dirY * MIN_PIXELDISTANCE_TO_UNIT));
			}

			// Generate a new temporary retreat Position for the Unit with a
			// provided Vector.
			precondtionsMet = this.generateNewTempRetreatPosition(goapUnit, usedVector);
		}
		// Position known -> action performed once
		else {
			precondtionsMet = ((PlayerUnit) goapUnit).getUnit().hasPath(this.retreatPosition);
		}

		// TODO: REMOVE DEBUG
		// Display the actual retreat Position
		if (this.retreatPosition != null) {
			Core.getInstance().getGame().drawCircleMap(this.retreatPosition.getPoint(), 5, new Color(0, 255, 0), true);
		}

		return precondtionsMet;
	}

	/**
	 * Function for generating a new temporary retreat Position for the current
	 * PlayerUnit. This function uses a provided Vector to determine the
	 * end-Position the Unit has to move to.
	 * <p>
	 * If the Vector intersects with the Polygon's boundaries, in which the Unit
	 * is currently inside, the Vector will be split up according to his length
	 * multiplied with a certain multiplier. This length is then processed along
	 * the Polygon in the direction the provided Vector is facing (One of two
	 * directions is chosen). A path along the Polygon is then created until the
	 * maximum calculated distance of the Vector is reached. Then the last
	 * Vector is being turned inwards until the Position is X degrees inside the
	 * Polygon. In the worst case, this causes the Unit to move backwards, since
	 * the Vector turns until the end Position is completely inside the Polygon.
	 * </p>
	 * <p>
	 * If the Vector does not intersect with the Polygon's boundaries, the
	 * temporary retreat Position is simply set to the end Position of the
	 * provided Vector.
	 * </p>
	 * 
	 * @param goapUnit
	 *            the Unit for which a retreat Position is being generated.
	 * @param vector
	 *            the chosen Vector the Unit follows initially.
	 * @return true or false depending if a Position was found.
	 */
	private boolean generateNewTempRetreatPosition(IGoapUnit goapUnit, Vector vector) {
		boolean success = true;

		try {
			Position unitPosition = ((PlayerUnit) goapUnit).getUnit().getPosition();
			Pair<Region, Polygon> matchingRegionPolygonPair = this.findBoundariesPositionIsIn(unitPosition);

			// Get the List of intersection Points with the retreat Vector.
			List<Pair<Vector, Point>> intersectionWithMapBoundaries = matchingRegionPolygonPair.second
					.findIntersections(vector);

			// Either intersections are found or not.
			if (!intersectionWithMapBoundaries.isEmpty()) {
				success = this.reactOnIntersectionsFound(unitPosition, matchingRegionPolygonPair,
						intersectionWithMapBoundaries, vector);
			} else {
				// No intersections were found, so the temporary end Position is
				// being set to the provided Vector's end.
				success = this.retreatToVectorEndPosition(vector);
			}
		} catch (Exception e) {
			success = false;
			// TODO: Possible Change: Move the Unit back to the nearest Region?
		}
		return success;
	}

	/**
	 * Function for finding the Polygon that the Position is in.
	 * 
	 * @param position
	 *            the Position that is being checked.
	 * @return the Region and the Polygon that the Position is located in.
	 */
	private Pair<Region, Polygon> findBoundariesPositionIsIn(Position position) {
		Pair<Region, Polygon> matchingRegionPolygonPair = null;

		// Search for the Pair of Regions and Polygons that includes the Unit's
		// Position.
		for (Pair<Region, Polygon> pair : CBot.getInstance().getInformationStorage().getMapInfo().getMapBoundaries()) {
			if (pair.first.getPolygon().isInside(position)) {
				matchingRegionPolygonPair = pair;
				break;
			}
		}
		return matchingRegionPolygonPair;
	}

	/**
	 * Function which sets the temporary retreat Position to a generated Point,
	 * whose Position is being calculated. This function is only called, when
	 * intersections with the map's boundaries are found. If no intersections
	 * are found, no special Point along the map's boundaries needs to be
	 * calculated. <br>
	 * Depending on the result of setting a temporary retreat Position, the
	 * function returns true or false.
	 * 
	 * @param unitPosition
	 *            the Position of the Unit whose retreat Position is being
	 *            calculated.
	 * @param matchingRegionPolygonPair
	 *            the Pair of Region and Polygon that the Unit's Position is in.
	 * @param intersectionWithMapBoundaries
	 *            a List of all intersections with the map's boundaries.
	 * @param vector
	 *            the current Vector from the Unit to a Position it is trying to
	 *            retreat to. This is used for determining the total distance
	 *            the Unit has to travel to match the Vector's length.
	 * @return true or false depending if a retreat Position is found and set or
	 *         not.
	 */
	private boolean reactOnIntersectionsFound(Position unitPosition, Pair<Region, Polygon> matchingRegionPolygonPair,
			List<Pair<Vector, Point>> intersectionWithMapBoundaries, Vector vector) {
		List<Point> vertices = matchingRegionPolygonPair.second.getVertices();
		boolean success = true;
		int elementIndex = 0;
		// Distance which determines if the while loop has to do another
		// iteration. Used to shorten / lengthen the distance the end-Position
		// will be away from the Unit starting Position.
		double distanceMultiplier = 0.6;
		double distanceTotal = vector.length() * distanceMultiplier;
		double distanceLeft = distanceTotal
				- (vector.getNeededMultiplier(intersectionWithMapBoundaries.get(elementIndex).second) * distanceTotal);
		// Get the vertex and its index of the Polygon that is the closest to
		// the intersection.
		Pair<Point, Integer> closestPair = this.findClosestVertexAtIntersection(vertices,
				intersectionWithMapBoundaries.get(elementIndex).second);

		// Sort the intersections based on the distance to the Unit itself. This
		// ensures the finding of the closest one with the index 0.
		this.sortOnPosition(intersectionWithMapBoundaries, unitPosition);

		// Generate the starting Points.
		Point currentPoint = intersectionWithMapBoundaries.get(elementIndex).second;
		Point nextPoint = null;

		// Determine the direction the Polygon is being traversed.
		Pair<Integer, Integer> previousAndNextIndices = this.findPreviousAndNextIndices(vertices, closestPair.second);
		Integer stepVerticesDirection = this.generateStepVerticesDirection(vector, vertices, previousAndNextIndices);

		if (stepVerticesDirection == -1) {
			nextPoint = vertices.get(previousAndNextIndices.first);
		} else {
			nextPoint = vertices.get(previousAndNextIndices.second);
		}

		// Generate a "path" for the Unit along the Polygon's boundaries.
		Pair<Vector, Region> pathAtBoundaries = this.generatePathAlongPolygon(distanceLeft, closestPair.second,
				vertices, currentPoint, nextPoint, stepVerticesDirection, matchingRegionPolygonPair.first);

		// Generate a Position at the end of the inwards rotated Vector for the
		// Unit to retreat to.
		Position generatedRetreatPosition = this.generateValidRetreatPosition(pathAtBoundaries.first,
				pathAtBoundaries.second);

		// Make sure the Position is actually inside the map's Polygon
		// boundaries.
		if (pathAtBoundaries.second.getPolygon().isInside(generatedRetreatPosition)) {
			this.generatedTempRetreatPosition = generatedRetreatPosition;
		} else {
			success = false;
		}

		// TODO: REMOVE DEBUG
		// Display intersections with the Polygon itself.
		Core.getInstance().getGame().drawCircleMap(intersectionWithMapBoundaries.get(elementIndex).second.toPosition(),
				5, new Color(0, 0, 255), true);

		return success;
	}

	/**
	 * Function for sorting a List of intersections/List of Pairs of Vectors and
	 * Points based on their distance to a given Position.
	 * 
	 * @param list
	 *            the List that is being sorted.
	 * @param position
	 *            the Position that is used for the distance calculations.
	 */
	private void sortOnPosition(List<Pair<Vector, Point>> list, final Position position) {
		list.sort(new Comparator<Pair<Vector, Point>>() {

			@Override
			public int compare(Pair<Vector, Point> p1, Pair<Vector, Point> p2) {
				return Double.compare(position.getDistance(p1.second.toPosition()),
						position.getDistance(p2.second.toPosition()));
			}

		});
	}

	// TODO: UML PARAMTERS
	/**
	 * Function for finding the closest Point (vertex) based on its distance to
	 * a given intersection in a List of vertices.
	 * 
	 * @param vertices
	 *            the List of possible vertices the Point is being chosen from.
	 * @param intersection
	 *            the Point to which the closest vertex must be found.
	 * @return a Pair of a Point which resembles the vertex and an Integer which
	 *         is the Point's index in the List of vertices.
	 */
	private Pair<Point, Integer> findClosestVertexAtIntersection(List<Point> vertices, Point intersection) {
		Point closestVertex = null;
		Integer indexClosestVertex = null;

		// Find the index and the actual reference to the vertex with the
		// smallest distance to the intersection.
		for (int i = 0; i < vertices.size(); i++) {
			if (closestVertex == null || vertices.get(i).toPosition().getDistance(intersection.toPosition()) < closestVertex
					.toPosition().getDistance(intersection.toPosition())) {
				closestVertex = vertices.get(i);
				indexClosestVertex = i;
			}
		}
		return new Pair<Point, Integer>(closestVertex, indexClosestVertex);
	}

	/**
	 * Function for finding the next and previous indices of a given List and an
	 * index. This function simply checks if the index can be increased and
	 * decreased. If not the indices are set to match the given List's size.
	 * This function refers to the List's.size() + 1 as the List's first index
	 * (0).
	 * 
	 * @param vertices
	 *            the List of Point that is being used for determining the
	 *            previous and next indices.
	 * @param indexClosestVertex
	 *            the index that is used to determine the general
	 *            index-position.
	 * @return a Pair of Integers that are the next and previous indices of the
	 *         List. Providing the last or first index results in jumping to the
	 *         end or beginning of the List's possible indices.
	 */
	private Pair<Integer, Integer> findPreviousAndNextIndices(List<Point> vertices, Integer indexClosestVertex) {
		Integer prevIndex = null;
		Integer nextIndex = null;

		// Last index:
		if (indexClosestVertex == vertices.size() - 1) {
			prevIndex = indexClosestVertex - 1;
			nextIndex = 0;
		}
		// First index
		else if (indexClosestVertex == 0) {
			prevIndex = vertices.size() - 1;
			nextIndex = indexClosestVertex + 1;
		}
		// Middle index
		else {
			prevIndex = indexClosestVertex - 1;
			nextIndex = indexClosestVertex + 1;
		}
		return new Pair<Integer, Integer>(prevIndex, nextIndex);
	}

	/**
	 * Function for determining the direction (+ or -) that a List of Points
	 * (vertices of a Polygon) is being traversed. This is being determined by
	 * calculating the distances to the next and previous Positions and
	 * comparing them. The Position that is closer "wins".
	 * 
	 * @param vector
	 *            the Vector whose end-Position determines the Position to which
	 *            the distances are being calculated to.
	 * @param vertices
	 *            the List of Points that the indices are referring to.
	 * @param previousAndNextIndices
	 *            the indices of the List of Points that are being used in the
	 *            distance calculations.
	 * @return either + or -1 depending which of the provided Positions of the
	 *         previous and next indices is closest.
	 */
	private Integer generateStepVerticesDirection(Vector vector, List<Point> vertices,
			Pair<Integer, Integer> previousAndNextIndices) {
		Position vecUTPEndPosition = new Position((int) (vector.getX() + vector.dirX),
				(int) (vector.getY() + vector.dirY));
		Integer stepVerticesDirection = null;

		// Determine the direction in which the Unit has to move along the
		// boundaries of the Polygon. This is either the found index of the
		// closest vertex + or -1. Therefore check the next and the previous
		// vertex and decide on the fact which one of them is closer to the
		// vecUTP end-Position.
		if (vertices.get(previousAndNextIndices.first).toPosition().getDistance(vecUTPEndPosition) < vertices
				.get(previousAndNextIndices.second).toPosition().getDistance(vecUTPEndPosition)) {
			stepVerticesDirection = -1;
		} else {
			stepVerticesDirection = 1;
		}
		return stepVerticesDirection;
	}

	/**
	 * Function for generating a path along the map's boundaries. This function
	 * returns the Vector to the last Point which is being calculated as well as
	 * the Region this Point is in. <br>
	 * While calculating the path the function moves along side the
	 * map-Polygon's boundaries. If a ChokePoint is met the Region, the path
	 * currently is in, is being changed and the Unit's path continues in the
	 * newly found Region. This continues until the total distance (the
	 * initially provided Vector's length) is met. <br>
	 * After that the last Vector is turned inside the Polygon until a valid
	 * retreat-Position is found. <br>
	 * In conclusion these steps are being taken:
	 * <ul>
	 * <li>Move along the Polygon's boundaries until max distance reached</li>
	 * <li>Move through a ChokePoint into a new Polygon if one is found</li>
	 * <li>Turn the last Vector inside until a valid Position is found</li>
	 * </ul>
	 * 
	 * @param distanceLeft
	 *            the total distance that the Unit has to retreat.
	 * @param currentIndex
	 *            the index of the current Point in the List of vertices of the
	 *            Polygon that the Unit is in.
	 * @param vertices
	 *            the List of all Points the Polygon the Unit is in consists of.
	 * @param currentPoint
	 *            the Point of the Polygon that is current the retreat Position.
	 * @param nextPoint
	 *            the next Point in the calculated direction of the List of
	 *            vertices of the current Polygon.
	 * @param stepVerticesDirection
	 *            the direction the Polygon is being traversed.
	 * @param currentRegion
	 *            the Region the Polygon is currently resembling.
	 * @return a Pair of a Vector which resembles the retreat-Position and the
	 *         Region that the Point is in.
	 */
	private Pair<Vector, Region> generatePathAlongPolygon(double distanceLeft, int currentIndex, List<Point> vertices,
			Point currentPoint, Point nextPoint, int stepVerticesDirection, Region currentRegion) {
		Vector vecToNextPoint = null;
		List<Point> currentVertices = vertices;
		Pair<Vector, Region> returnValues = new Pair<>(null, currentRegion);

		// Counters for traversing a ChokePoint. This is necessary since
		// sometimes the second Position of a ChokePoint is chosen as the next
		// Point and therefore causes the while loop to go on indefinitely.
		int chokePointCounterMax = 3;
		int chokePointCounter = 0;

		while (distanceLeft > 0) {
			if (chokePointCounter != 0) {
				chokePointCounter--;
			}

			// Find the nearest ChokePoint.
			bwta.Chokepoint nearestChoke = BWTA.getNearestChokepoint(currentPoint.toPosition());
			Point chokepointFirst = new Point(nearestChoke.getSides().first);
			Point chokepointSecond = new Point(nearestChoke.getSides().second);

			// Switch to another Region's Polygon if a ChokePoint is reached.
			// -> Swap the next Point in the current Polygon for another Point
			// in the Polygon that is separated from the current one by the
			// ChokePoint.
			if (chokePointCounter == 0
					&& (chokepointFirst.equals(currentPoint) || chokepointSecond.equals(currentPoint))) {
				// Get the other region.
				returnValues.second = this.findAdjacentRegion(nearestChoke, currentRegion);

				// Swap the Lists of vertices.
				currentVertices = new Polygon(returnValues.second.getPolygon()).getVertices();

				// Get the index of the current Position in the new vertices
				// List.
				currentIndex = this.findIndexOfPointInVertexList(currentVertices, currentPoint);

				// Get the new direction the next Polygon must be traversed.
				stepVerticesDirection = generateStepVerticesDirection(
						new Vector(currentPoint.getX(), currentPoint.getY()), currentVertices,
						findPreviousAndNextIndices(currentVertices, currentIndex));

				// Apply the direction to the current index to find the next
				// Point in the vertices List.
				currentIndex += stepVerticesDirection;
				currentIndex = this.verifyIndexSize(currentIndex, currentVertices);

				nextPoint = currentVertices.get(currentIndex);

				// Set the ChokePoint counter to prevent loops.
				chokePointCounter = chokePointCounterMax;
			}

			// Generate a Vector to the next Point.
			vecToNextPoint = new Vector(currentPoint.getX(), currentPoint.getY(),
					nextPoint.getX() - currentPoint.getX(), nextPoint.getY() - currentPoint.getY());
			distanceLeft -= vecToNextPoint.length();

			currentIndex += stepVerticesDirection;
			currentIndex = this.verifyIndexSize(currentIndex, currentVertices);

			currentPoint = nextPoint.clone();

			// Move the next Point in the desired direction.
			nextPoint = currentVertices.get(currentIndex);

			// TODO: REMOVE DEBUG
			// Display vectors and generated temp. retreat Positions
			Core.getInstance().getGame().drawLineMap(vecToNextPoint.getX(), vecToNextPoint.getY(),
					(int) (vecToNextPoint.getX() + vecToNextPoint.dirX),
					(int) (vecToNextPoint.getY() + vecToNextPoint.dirY), new Color(255, 255, 0));
			Core.getInstance().getGame().drawCircleMap((int) (vecToNextPoint.getX() + vecToNextPoint.dirX),
					(int) (vecToNextPoint.getY() + vecToNextPoint.dirY), 5, new Color(255, 0, 0), true);
		}

		// Do NOT forget to return the calculated Vector!
		returnValues.first = vecToNextPoint;

		return returnValues;
	}

	/**
	 * Function for finding the adjacent Region of a ChokePoint and a given
	 * Region. This is needed since the Regions the ChokePoints are splitting
	 * are not stored as HashTables / HashMaps.
	 * 
	 * @param chokePoint
	 *            the ChokePoint that is splitting two Regions.
	 * @param currentRegion
	 *            one of the Regions that is being split by the ChokePoint.
	 * @return the other Region of the ChokePoint that is not the one provided
	 *         as a parameter.
	 */
	private Region findAdjacentRegion(Chokepoint chokePoint, Region currentRegion) {
		Region otherRegion = chokePoint.getRegions().first;

		if (otherRegion.equals(currentRegion)) {
			otherRegion = chokePoint.getRegions().second;
		}
		return otherRegion;
	}

	/**
	 * Function for finding the index of a Point in a List of given vertices /
	 * Points.
	 * 
	 * @param vertices
	 *            the List of Points that is being searched through.
	 * @param point
	 *            the Point whose coordinates are being searched.
	 * @return the index of the Point inside the List of Points.
	 */
	private int findIndexOfPointInVertexList(List<Point> vertices, Point point) {
		int index = -1;

		for (int i = 0; i < vertices.size() && index == -1; i++) {
			if (vertices.get(i).equals(point)) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * Function for verifying a given index by the bounds of a List containing
	 * Points. This function returns only valid indices. If the bounds are
	 * stepped over, the returned index will be at a valid position,. which is
	 * either the beginning (0) or the end of the List (List.size() - 1).
	 * 
	 * @param index
	 *            the index that is being verified.
	 * @param vertices
	 *            the List of Points that provide the bounds of the index.
	 * @return a valid index of the provided List. Either the given one or one
	 *         that was changed according to the dimensions of the List.
	 */
	private int verifyIndexSize(int index, List<Point> vertices) {
		int currentIndex = index;
		boolean running = true;

		while(running || currentIndex >= vertices.size() || currentIndex < 0) {
			if(currentIndex < 0) {
				currentIndex = (vertices.size() - 1) - currentIndex;
			} else if (currentIndex >= vertices.size()){
				currentIndex = currentIndex - (vertices.size() - 1);
			} else {
				running = false;
			}
		}

		return currentIndex;
	}

	/**
	 * Function for generating a retreat-Position which lies inside the
	 * specified Region. This Position is turned inwards and shortened until it
	 * completely is inside the provided parameter.
	 * 
	 * @param vecToLastPoint
	 *            the Vector whose end-Point is being used as retreat-Position.
	 * @param region
	 *            the Region the Vector's end-Point is in.
	 * @return a retreat-Position to which the Unit can retreat to.
	 */
	private Position generateValidRetreatPosition(Vector vecToLastPoint, Region region) {
		int turningDegrees = 10;
		int iterationCounter = 1;
		boolean foundPossiblePoint = false;
		double stepMultiplier = 1.;
		Position rotatedPosition = null;

		// Rotate the Vector until the end is inside the Polygon with a given
		// degree value. Each time the iteration fails, the distance the Vector
		// reaches inside the Polygon is halved to prevent any errors from
		// happening. This continues until a valid Position is found. The worst
		// case is the Position being nearly on the Vector itself.
		while (!foundPossiblePoint) {
			foundPossiblePoint = true;

			// Move the target Position inside the Polygon.
			vecToLastPoint.rotateLeftDEG(iterationCounter * turningDegrees);
			rotatedPosition = new Position((int) (vecToLastPoint.getX() + vecToLastPoint.dirX * stepMultiplier),
					(int) (vecToLastPoint.getY() + vecToLastPoint.dirY * stepMultiplier));
			if (!region.getPolygon().isInside(rotatedPosition)) {
				vecToLastPoint.rotateRightDEG(2 * iterationCounter * turningDegrees);
				rotatedPosition = new Position((int) (vecToLastPoint.getX() + vecToLastPoint.dirX * stepMultiplier),
						(int) (vecToLastPoint.getY() + vecToLastPoint.dirY * stepMultiplier));

				if (!region.getPolygon().isInside(rotatedPosition)) {
					foundPossiblePoint = false;
				}
			}

			if (!foundPossiblePoint) {
				stepMultiplier /= 2.;
				iterationCounter++;
			}
		}
		return rotatedPosition;
	}

	/**
	 * Function with which the temporary retreat Position gets set to the
	 * Vectors end Position.
	 * 
	 * @param vecUTP
	 *            the Vector to the target Position.
	 * @return true or false, depending if the Position is inside the map and
	 *         accessible or not.
	 */
	private boolean retreatToVectorEndPosition(Vector vecUTP) {
		Position targetVecPosition = new Position(vecUTP.getX() + (int) (vecUTP.dirX),
				vecUTP.getY() + (int) (vecUTP.dirY));
		boolean returnValue = true;

		if (this.isInsideMap(targetVecPosition)) {
			this.generatedTempRetreatPosition = targetVecPosition;
		} else {
			returnValue = false;
		}

		return returnValue;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// Base cost has to be increased since the action should only be taken
		// in consideration, if no gathering point from another PlayerUnit is
		// found inside the cone (is another Action).
		return 100;
	}

	/**
	 * Function for retrieving all Units in an increasing range around the given
	 * PlayerUnit. The range at which the Units are searched for increases
	 * stepwise until Units are found or the preset maximum is reached.
	 * 
	 * @param goapUnit
	 *            the PlayerUnit that the search is based around.
	 * @return a HashSet containing all Units in a range around the given
	 *         PlayerUnit with at least minimum distance to it.
	 */
	private HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit goapUnit) {
		HashSet<Unit> unitsTooClose = new HashSet<Unit>();
		HashSet<Unit> unitsInRange = new HashSet<Unit>();
		int iterationCounter = 1;

		// Increase range until a Unit is found or the threshold is reached.
		while (unitsInRange.isEmpty() && iterationCounter <= EXPAND_MULTIPLIER_MAX) {
			HashSet<Unit> foundUnits = goapUnit
					.getAllPlayerUnitsInRange((int) (iterationCounter * MAX_PIXELDISTANCE_TO_UNIT));
			HashSet<Unit> unitsToBeRemoved = new HashSet<Unit>();

			// Test all found Units, a Unit has to have a minimum distance to
			// the PlayerUnit.
			for (Unit unit : foundUnits) {
				if (!unitsTooClose.contains(unit)
						&& goapUnit.getUnit().getDistance(unit.getPosition()) < MIN_PIXELDISTANCE_TO_UNIT) {
					unitsToBeRemoved.add(unit);
					unitsTooClose.add(unit);
				}
			}

			for (Unit unit : unitsToBeRemoved) {
				foundUnits.remove(unit);
			}

			unitsInRange.addAll(foundUnits);
			iterationCounter++;
		}
		return unitsInRange;
	}

	/**
	 * Function for retrieving the Unit with the greatest sum of strengths
	 * around the units TilePosition.
	 * 
	 * @param units
	 *            a HashSet containing all units which are going to be cycled
	 *            through.
	 * @param goapUnit
	 *            the currently executing IGoapUnit.
	 * @return the Unit with the greatest sum of strengths at its TilePosition.
	 */
	private Unit getUnitWithGreatestTileStrengths(HashSet<Unit> units, IGoapUnit goapUnit) {
		Unit bestUnit = null;
		int bestUnitStrengthTotal = 0;

		// Iterate over the Units and over their TilePositions in a specific
		// radius.
		for (Unit unit : units) {
			int currentStrengths = 0;

			for (int i = -TILE_RADIUS_AROUND_UNITS_SEARCH; i <= TILE_RADIUS_AROUND_UNITS_SEARCH; i++) {
				for (int j = -TILE_RADIUS_AROUND_UNITS_SEARCH; j <= TILE_RADIUS_AROUND_UNITS_SEARCH; j++) {

					// TODO: Possible Change: AirStrength Implementation
					Integer value = ((PlayerUnit) goapUnit).getInformationStorage().getTrackerInfo()
							.getPlayerGroundAttackTilePositions().get(new TilePosition(
									unit.getTilePosition().getX() + i, unit.getTilePosition().getY() + j));

					if (value != null) {
						currentStrengths += value;
					}
				}
			}

			if (bestUnit == null || currentStrengths > bestUnitStrengthTotal) {
				bestUnit = unit;
				bestUnitStrengthTotal = currentStrengths;
			}
		}
		return bestUnit;
	}
}
