package bwapiMath;

import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
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
	 * Convenience function.
	 * 
	 * @param color
	 *            the Color that is used to represent the Polygon.
	 * @param vertexRadius
	 *            the radius of the ellipses symbolizing the different vertices.
	 * @see #drawOnMap(Color, int, boolean)
	 */
	public void drawOnMap(Color color, int vertexRadius) {
		this.drawOnMap(color, vertexRadius, false);
	}

	/**
	 * Function for drawing the Polygon on the ingame map.
	 * 
	 * @param color
	 *            the Color that is used to represent the Polygon.
	 * @param vertexRadius
	 *            the radius of the ellipses symbolizing the different vertices.
	 * @param verticesFilled
	 *            show the ellipses either empty or filled.
	 */
	public void drawOnMap(Color color, int vertexRadius, boolean verticesFilled) {
		Game game = Core.getInstance().getGame();

		// Vertices
		for (Point point : this.vertices) {
			game.drawEllipseMap(point.toPosition(), vertexRadius, vertexRadius, color, verticesFilled);
		}

		// Edges
		for (int i = 0; i < this.vertices.size(); i++) {
			// Connect the last vertex with the first one
			if (i == vertices.size() - 1) {
				game.drawLineMap(this.vertices.get(i).toPosition(), this.vertices.get(0).toPosition(), color);
			} else {
				game.drawLineMap(this.vertices.get(i).toPosition(), this.vertices.get(i + 1).toPosition(), color);
			}
		}
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
			// First clone the Point, since the actual edge of the Polygon must
			// NOT be changed! Then add a fixed amount to the current Point's
			// values for the coordinates actually being inside the Polygon. If
			// this is not added, the second generated Point would directly be
			// on the edge of the Polygon and not being counted.
			Point currentPoint = startingPoint.clone();
			currentPoint.x += 1;
			currentPoint.y += 1;

			// Iterate through all possible TilePositions in the Polygon with a
			// recursion
			this.tilePositionRecursion(coveredTilePositions, currentPoint);

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

	/**
	 * Function for performing a recursion, which finds all covered
	 * TilePositions that are covered by this Polygon. This uses a Point, which
	 * resembles a <b>Position (!)</b> in the Game. This is necessary since
	 * adding a small margin at the left and top side is necessary for the
	 * algorithm to properly work. If this margin is not added, the values
	 * returned by the ray algorithm used to determine the intersections between
	 * the Vectors would be null as the Point would sit directly on the Vector
	 * itself. Therefore using a TilePosition is not wise since adding a margin
	 * to one is not possible.
	 * 
	 * @param coveredTilePositions
	 *            the HashSet for holding all found TilePositions that are being
	 *            covered by the Polygon itself.
	 * @param currentPoint
	 *            the Point <b>(as Position!)</b> that the ray casting algorithm
	 *            is testing and that is later converted to an actual
	 *            TilePosition.
	 */
	private void tilePositionRecursion(HashSet<TilePosition> coveredTilePositions, Point currentPoint)
			throws PointTypeException {
		if (currentPoint.type != Type.POSITION) {
			throw new PointTypeException(Type.POSITION);
		} else if (this.polygon.contains(currentPoint.x, currentPoint.y)
				&& !coveredTilePositions.contains(currentPoint.transformFromPositionToTilePosition())) {
			int tileSize = Core.getInstance().getTileSize();

			coveredTilePositions.add(currentPoint.transformFromPositionToTilePosition());

			// -> left
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x - tileSize, currentPoint.y, Type.POSITION));
			// -> right
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x + tileSize, currentPoint.y, Type.POSITION));
			// -> top
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x, currentPoint.y - tileSize, Type.POSITION));
			// -> bottom
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x, currentPoint.y + tileSize, Type.POSITION));

			// -> top-left
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x - tileSize, currentPoint.y - tileSize, Type.POSITION));
			// -> top-right
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x - tileSize, currentPoint.y + tileSize, Type.POSITION));
			// -> bottom-left
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x + tileSize, currentPoint.y - tileSize, Type.POSITION));
			// -> bottom-right
			tilePositionRecursion(coveredTilePositions,
					new Point(currentPoint.x + tileSize, currentPoint.y + tileSize, Type.POSITION));
		}
	}

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

	/**
	 * Function for finding an intersection between this Polygon and a given
	 * Vector. This function returns a List of Vector, Point Pairs. The Points
	 * represent the Intersection itself, while the Vector is the segment of the
	 * Polygon that Point is on.
	 * 
	 * @param testVector
	 *            the Vector whose intersections with the Polygon are being
	 *            tested.
	 * @return a List of Pairs containing Vectors and Points.
	 *         <ul>
	 *         <li>Vector: the segment of the Polygon that is being
	 *         intersected.</li>
	 *         <li>Point: the intersection with the Polygon.</li>
	 *         </ul>
	 */
	public List<Pair<Vector, Point>> findIntersections(Vector testVector) {
		List<Pair<Vector, Point>> intersections = new ArrayList<>();
		PathIterator pathIterator = this.polygon.getPathIterator(null);
		Line2D testLine = new Line2D.Double(testVector.x, testVector.y, testVector.x + testVector.dirX,
				testVector.y + testVector.dirY);
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

			// Add an occuring intersection to the list of intersections found.
			if (polyLine.intersectsLine(testLine)) {
				// Extract the needed Vector information.
				int posX = (int) polyLine.getX1();
				int posY = (int) polyLine.getY1();
				double dirX = polyLine.getX2() - polyLine.getX1();
				double dirY = polyLine.getY2() - polyLine.getY1();
				Vector polyVector = new Vector(posX, posY, dirX, dirY);

				// Find the intersection itself.
				Point intersection = testVector.getIntersection(polyVector);
				intersections.add(new Pair<Vector, Point>(polyVector, intersection));
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
		Vector vecToMiddlePoint = new Vector(vecToNextPoint.x, vecToNextPoint.y, vecToNextPoint.dirX * vecLengthHalf,
				vecToNextPoint.dirY * vecLengthHalf);
		Point middlePoint = new Point((int) (vecToMiddlePoint.x + vecToMiddlePoint.dirX),
				(int) (vecToMiddlePoint.y + vecToMiddlePoint.dirY), Point.Type.POSITION);
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
