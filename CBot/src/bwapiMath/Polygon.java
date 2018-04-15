package bwapiMath;

import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bwapi.Color;
import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapiMath.Point.Type;
import core.Core;

/**
 * Polygon.java --- Class used for displaying and calculating an area. Mostly
 * used as a wrapper for BWAPI polygons and other custom areas.
 * 
 * @author P H - 22.04.2017
 *
 */
public class Polygon {

	private static final Color DEFAULT_COLOR = new Color(255, 255, 255);
	private static final boolean DEFAULT_VERTICES_SHOWN = true;
	private static final int DEFAULT_VERTICES_RADIUS = 5;
	private static final boolean DEFAULT_VERTICES_FILLED = false;

	private java.awt.Polygon polygon = new java.awt.Polygon();
	private List<Point> vertices = new ArrayList<Point>();

	public Polygon() {

	}

	public Polygon(List<Point> vertices) {
		this.vertices = vertices;

		for (Point point : vertices) {
			this.polygon.addPoint(point.x, point.y);
		}
	}

	public Polygon(bwta.Polygon bwtaPolygon) {
		this(generatePointList(bwtaPolygon));
	}

	/**
	 * Function for generating a Polygon object using a List of BWAPI Positions.
	 * 
	 * @param positions
	 *            the vertices of the Polygon being created.
	 * @return a Polygon object with the provided Positions as vertices.
	 */
	public static Polygon generateFromPositions(List<Position> positions) {
		Polygon p = new Polygon();

		for (Position position : positions) {
			p.addVertex(new Point(position));
		}
		return p;
	}

	// -------------------- Functions

	/**
	 * Function for converting the Positions inside a BWTA Polygon to Points.
	 * 
	 * @param bwtaPolygon
	 *            the Polygon that is going to be converted.
	 * @return a List of Points which are the vertices of the provided Polygon.
	 */
	private static List<Point> generatePointList(bwta.Polygon bwtaPolygon) {
		List<Point> points = new ArrayList<>();

		for (Position position : bwtaPolygon.getPoints()) {
			points.add(new Point(position.getPoint()));
		}
		return points;
	}

	/**
	 * Function for calculating all TilePositions that are currently being
	 * covered by the Polygon itself. This <b>requires</b> the Polygon to use
	 * Positions as its Points. TilePositions yield no usable result!
	 * 
	 * @return a HashSet containing all TilePositions located inside the
	 *         Polygon.
	 */
	public HashSet<TilePosition> getCoveredTilePositions() throws PointTypeException {
		HashSet<TilePosition> coveredTilePositions = new HashSet<TilePosition>();
		Point startingPoint = this.findTopStartingPoint();

		if (startingPoint.type == Type.POSITION) {
			Queue<Point> pointsToCheck = new LinkedList<>();
			HashSet<TilePosition> checkedTilePositions = new HashSet<>();

			// First initialization.
			TilePosition startingTilePosition = startingPoint.transformFromPositionToTilePosition();
			pointsToCheck.add(startingPoint);
			checkedTilePositions.add(startingTilePosition);

			while (!pointsToCheck.isEmpty()) {
				Point currentPoint = pointsToCheck.poll();
				HashSet<Point> adjacentPoints = this.generatePossibleAdjacentPoints(currentPoint, checkedTilePositions);

				// Act upon the returned Points. Ensure that all (possible)
				// adjacent Points are only added once.
				pointsToCheck.addAll(adjacentPoints);
				coveredTilePositions.add(currentPoint.transformFromPositionToTilePosition());
				for (Point point : adjacentPoints) {
					checkedTilePositions.add(point.transformFromPositionToTilePosition());
				}
			}

			return coveredTilePositions;
		} else {
			throw new PointTypeException(Type.POSITION);
		}
	}

	/**
	 * Function for finding the top/left most starting Point of all given
	 * vertices.
	 * 
	 * @return the top/left most vertex.
	 */
	private Point findTopStartingPoint() {
		Point p = null;

		for (Point point : this.vertices) {
			if (p == null || (point.y <= p.y && point.x <= p.x)) {
				p = point;
			}
		}
		return p;
	}

	// TODO: UML ADD
	/**
	 * Function for generating the Points that are the direct neighbours of a
	 * provided Point instance with the standard tile-size stored in the Core
	 * instance. This includes the Points matching the following orientations:
	 * <ul>
	 * <li>Top-Left</li>
	 * <li>Top</li>
	 * <li>Top-Right</li>
	 * <li>Left</li>
	 * <li>Right</li>
	 * <li>Bottom-Left</li>
	 * <li>Bottom</li>
	 * <li>Bottom-Right</li>
	 * </ul>
	 * The function then checks if the Points are inside the Polygon and that
	 * the TilePositions, which are based on the Points, are <b>not</b> inside
	 * the provided HashSet! Only Points matching both criteria are being
	 * returned in the resulting HashSet.
	 * 
	 * @param point
	 *            the Point whose neighbours are being generated.
	 * @param checkedTilePositions
	 *            a HashSet containing all TilePositions that must not be
	 *            generated by the Points.
	 * @return a HashSet containing the neighbour Points from the provided Point
	 *         instance matching the criteria mentioned above.
	 */
	private HashSet<Point> generatePossibleAdjacentPoints(Point point, HashSet<TilePosition> checkedTilePositions) {
		int tileSize = Core.getInstance().getTileSize();
		int x = point.getX();
		int y = point.getY();

		Point topLeft = new Point(x - tileSize, y - tileSize, Type.POSITION);
		Point top = new Point(x, y - tileSize, Type.POSITION);
		Point topRight = new Point(x + tileSize, y - tileSize, Type.POSITION);
		Point left = new Point(x - tileSize, y, Type.POSITION);
		Point right = new Point(x + tileSize, y, Type.POSITION);
		Point bottomLeft = new Point(x - tileSize, y + tileSize, Type.POSITION);
		Point bottom = new Point(x, y + tileSize, Type.POSITION);
		Point bottomRight = new Point(x + tileSize, y + tileSize, Type.POSITION);

		// Shortened in order to process inline.
		HashSet<TilePosition> cTp = checkedTilePositions;
		HashSet<Point> newPoints = new HashSet<>();

		if (this.isInsideAndUnchecked(topLeft, cTp))
			newPoints.add(topLeft);
		if (this.isInsideAndUnchecked(top, cTp))
			newPoints.add(top);
		if (this.isInsideAndUnchecked(topRight, cTp))
			newPoints.add(topRight);
		if (this.isInsideAndUnchecked(left, cTp))
			newPoints.add(left);
		if (this.isInsideAndUnchecked(right, cTp))
			newPoints.add(right);
		if (this.isInsideAndUnchecked(bottomLeft, cTp))
			newPoints.add(bottomLeft);
		if (this.isInsideAndUnchecked(bottom, cTp))
			newPoints.add(bottom);
		if (this.isInsideAndUnchecked(bottomRight, cTp))
			newPoints.add(bottomRight);

		return newPoints;
	}

	// TODO: UML ADD
	/**
	 * Function for checking if a Point is inside the Polygon itself and the
	 * TilePosition generated by it is not already in a HashSet of already
	 * checked TilePositions.
	 * 
	 * @param point
	 *            the Point instance that is going to be checked.
	 * @param checkedTilePositions
	 *            the HashSet of TilePositions that are forbidden / already
	 *            marked.
	 * @return true if the Polygon contains the provided Point while the checked
	 *         TilePosition HashSet does not contain the resulting TilePosition.
	 */
	private boolean isInsideAndUnchecked(Point point, HashSet<TilePosition> checkedTilePositions) {
		return this.contains(point) && !checkedTilePositions.contains(point.transformFromPositionToTilePosition());
	}

	// TODO: UML REMOVE
	// private void tilePositionRecursion(HashSet<TilePosition>
	// coveredTilePositions, Point currentPoint,
	// boolean isStartingPoint) throws PointTypeException {

	/**
	 * Function for test if a Point lies inside the Polygon.
	 * 
	 * @param p
	 *            the Point being tested.
	 * @return true or false depending if the point lies inside the Polygon.
	 */
	public boolean contains(Point p) {
		return this.polygon.contains(p.x, p.y);
	}

	// TODO: UML ADD
	/**
	 * Function for finding the closest intersection between this Polygon and a
	 * given Line. This function returns a single Point or null, if no
	 * intersection is found.
	 * 
	 * @param line
	 *            the Line whose closest intersection to its starting Point with
	 *            the Polygon is being calculated.
	 * @return the closest intersection between the given Line and this Polygon
	 *         relative to the line's starting Point.
	 */
	public Point getClosestIntersection(final Line line) {
		List<Pair<Line, Point>> intersections = new ArrayList<>(this.findIntersections(line));
		Point closestIntersection = null;

		// Sort based on the closest intersection.
		intersections.sort(new Comparator<Pair<Line, Point>>() {
			@Override
			public int compare(Pair<Line, Point> p1, Pair<Line, Point> p2) {
				Line lineToFirstPoint = new Line(line.getStartPoint(), p1.second);
				Line lineToSecondPoint = new Line(line.getStartPoint(), p2.second);

				return Double.compare(lineToFirstPoint.length(), lineToSecondPoint.length());
			}
		});

		if (!intersections.isEmpty()) {
			closestIntersection = intersections.get(0).second;
		}

		return closestIntersection;
	}

	// TODO: UML CHANGE PARAMS
	/**
	 * Function for finding an intersection between this Polygon and a given
	 * Line. This function returns a List of Line, Point Pairs. The Points
	 * represent the Intersection itself, while the Line is the segment of the
	 * Polygon that Point is on.
	 * 
	 * @param testVector
	 *            the Vector whose intersections with the Polygon are being
	 *            tested.
	 * @return a List of Pairs containing Vectors and Points.
	 *         <ul>
	 *         <li>Line: the segment of the Polygon that is being
	 *         intersected.</li>
	 *         <li>Point: the intersection with the Polygon.</li>
	 *         </ul>
	 */
	public List<Pair<Line, Point>> findIntersections(Line line) {
		List<Pair<Line, Point>> intersections = new ArrayList<>();
		PathIterator pathIterator = this.polygon.getPathIterator(null);
		Line2D.Double testLine = Converter.convert(line);
		boolean atEnd = false;

		// Define the storage of the received coordinates
		double[] startingCoords = new double[6];
		double[] prevCoords = new double[6];
		double[] currentCoords = new double[6];

		// Get the starting coordinates
		pathIterator.currentSegment(startingCoords);
		pathIterator.currentSegment(currentCoords);
		pathIterator.currentSegment(prevCoords);

		pathIterator.next();

		// Iterate through the PathIterator
		while (!atEnd) {
			// Stop the loop when the end Polygon is closed
			atEnd = (pathIterator.currentSegment(currentCoords) == PathIterator.SEG_CLOSE);
			Line2D.Double polyLine;

			// Differentiate between the last and the other vertices of the
			// Polygon.
			if (atEnd) {
				Point2D.Double start = new Point2D.Double((int) currentCoords[0], (int) currentCoords[1]);
				Point2D.Double end = new Point2D.Double((int) startingCoords[0], (int) startingCoords[1]);
				polyLine = new Line2D.Double(start, end);
			} else {
				Point2D.Double start = new Point2D.Double((int) prevCoords[0], (int) prevCoords[1]);
				Point2D.Double end = new Point2D.Double((int) currentCoords[0], (int) currentCoords[1]);
				polyLine = new Line2D.Double(start, end);
			}

			// Add an occurring intersection to the list of intersections found.
			if (polyLine.intersectsLine(testLine)) {
				Point2D.Double intersection = Line.getIntersection(polyLine, testLine);

				intersections.add(new Pair<Line, Point>(Converter.convert(polyLine), Converter.convert(intersection)));
			}

			// Swap the current coordinates with the previous ones.
			prevCoords[0] = currentCoords[0];
			prevCoords[1] = currentCoords[1];
			pathIterator.next();
		}

		return intersections;
	}

	/**
	 * Function for splitting all edges of the Polygon into pieces which are
	 * smaller or equal to a provided length.
	 * 
	 * @param maxEdgeLength
	 *            the maximum length an edge is allowed to have.
	 */
	public void splitLongEdges(int maxEdgeLength) {
		for (int i = 0; i < this.vertices.size(); i++) {
			int indexNextVertex = i + 1;

			if (indexNextVertex >= this.vertices.size()) {
				indexNextVertex = 0;
			}

			// Create a Vector to the next vertex.
			Point currentPoint = this.vertices.get(i);
			Point nextPoint = this.vertices.get(indexNextVertex);
			Vector vecToNextPoint = new Vector(currentPoint.x, currentPoint.y, nextPoint.x - currentPoint.x,
					nextPoint.y - currentPoint.y);

			// Split the edge in half if necessary.
			if (vecToNextPoint.length() > maxEdgeLength) {
				this.splitEdgesInHalfRecursion(i, indexNextVertex, vecToNextPoint, maxEdgeLength);
			}
		}
	}

	/**
	 * Recursive function for splitting a edge inside the Polygon into two
	 * halves. This continues until all sub-halves have a shorter length then
	 * the provided parameter specifies.
	 * 
	 * @param currentIndex
	 *            the index of the starting vertex inside the Polygon.
	 * @param nextIndex
	 *            the index of the following vertex inside the Polygon.
	 * @param vecToNextPoint
	 *            the Vector which starts at the starting vertex and leads
	 *            towards the following vertex.
	 * @param maxEdgeLength
	 *            the maximum length any edge is allowed to have. Any edge which
	 *            is longer than this provided value will be split in half.
	 */
	private void splitEdgesInHalfRecursion(int currentIndex, int nextIndex, Vector vecToNextPoint, int maxEdgeLength) {
		Point endPoint = this.vertices.get(nextIndex);
		double vecLengthHalf = vecToNextPoint.length() / 2.;

		// Find the middle of the edge.
		vecToNextPoint.normalize();
		Vector vecToMiddlePoint = new Vector(vecToNextPoint.x, vecToNextPoint.y,
				vecToNextPoint.getDirX() * vecLengthHalf, vecToNextPoint.getDirY() * vecLengthHalf);
		Point middlePoint = new Point((int) (vecToMiddlePoint.x + vecToMiddlePoint.getDirX()),
				(int) (vecToMiddlePoint.y + vecToMiddlePoint.getDirY()), Point.Type.POSITION);
		Vector vecToEndPoint = new Vector(middlePoint.x, middlePoint.y, endPoint.x - middlePoint.x,
				endPoint.y - middlePoint.y);

		// Add the generated Point in between the two known Points.
		this.vertices.add(nextIndex, middlePoint);

		// Adjust the index of the middle Point and the end Point.
		int middleIndex = nextIndex;
		nextIndex++;
		if (nextIndex >= this.vertices.size()) {
			nextIndex = 0;
		}

		// Start a recursion if the length of the two new Vectors exceeds the
		// given max length. This has to be done in this order since inserting
		// Points causes the calculated indices to offset.
		// -> Middle Point to end.
		if (vecToEndPoint.length() > maxEdgeLength) {
			this.splitEdgesInHalfRecursion(middleIndex, nextIndex, vecToEndPoint, maxEdgeLength);
		}
		// -> Start Point to middle.
		if (vecToMiddlePoint.length() > maxEdgeLength) {
			this.splitEdgesInHalfRecursion(currentIndex, middleIndex, vecToMiddlePoint, maxEdgeLength);
		}
	}

	/**
	 * Convenience function. The values being used are the default ones.
	 * 
	 * @see Vector#display(Color, boolean, int, int)
	 */
	public void display() {
		this.display(DEFAULT_COLOR);
	}

	/**
	 * Convenience function.
	 * 
	 * @param color
	 *            the color of the displayed Polygon.
	 */
	public void display(Color color) {
		this.display(color, DEFAULT_VERTICES_SHOWN);
	}

	/**
	 * Convenience function.
	 * 
	 * @param color
	 *            the color of the displayed Polygon.
	 * @param verticesShown
	 *            flag set true if the vertices should be shown, false if not.
	 */
	public void display(Color color, boolean verticesShown) {
		this.display(color, verticesShown, DEFAULT_VERTICES_RADIUS);
	}

	/**
	 * Convenience function.
	 * 
	 * @param color
	 *            the color of the displayed Polygon.
	 * @param verticesShown
	 *            flag set true if the vertices should be shown, false if not.
	 * @param verticesRadius
	 *            the radius of the vertices being shown.
	 */
	public void display(Color color, boolean verticesShown, int verticesRadius) {
		this.display(color, verticesShown, verticesRadius, DEFAULT_VERTICES_FILLED);
	}

	/**
	 * Function for displaying the Polygon on the ingame map.
	 * 
	 * @param color
	 *            the color of the displayed Polygon.
	 * @param verticesShown
	 *            flag set true if the vertices should be shown, false if not.
	 * @param verticesRadius
	 *            the radius of the vertices being shown.
	 * @param verticesFilled
	 *            flag set true if the vertices should be filled, false if not.
	 */
	public void display(Color color, boolean verticesShown, int verticesRadius, boolean verticesFilled) {
		// Vertices:
		if (verticesShown) {
			for (Point point : this.vertices) {
				point.display(verticesRadius, color, verticesFilled);
			}
		}

		// Edges:
		for (int i = 0; i < this.vertices.size(); i++) {
			// Connect the last vertex with the first one
			if (i == this.vertices.size() - 1) {
				Core.getInstance().getGame().drawLineMap(this.vertices.get(i).toPosition(),
						this.vertices.get(0).toPosition(), color);
			} else {
				Core.getInstance().getGame().drawLineMap(this.vertices.get(i).toPosition(),
						this.vertices.get(i + 1).toPosition(), color);
			}
		}
	}

	// ------------------------------ Getter / Setter

	public List<Point> getVertices() {
		return vertices;
	}

	/**
	 * Adds a Point to the Polygon's end that resembles a vertex.
	 * 
	 * @param p
	 *            the Point that is going to be added.
	 */
	public void addVertex(Point p) {
		this.vertices.add(p);
		this.polygon.addPoint(p.x, p.y);
	}

}
