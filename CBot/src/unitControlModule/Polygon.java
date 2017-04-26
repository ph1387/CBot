package unitControlModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.TilePosition;
import core.Core;
import unitControlModule.Point.Type;

/**
 * Polygon.java --- Class used for displaying and calculating an area.
 * 
 * @author P H - 22.04.2017
 *
 */
public class Polygon {

	private List<Point> vertices = new ArrayList<Point>();
	private List<Vector> edges = new ArrayList<Vector>();

	public Polygon() {

	}

	public Polygon(List<Point> vertices) {
		this.vertices = vertices;

		this.generateEdges();
	}

	// -------------------- Functions

	/**
	 * Function for generating all edges of the Polygon with the currently given
	 * vertices. The last vertex is automatically connected with the first
	 * vertex.
	 */
	private void generateEdges() {
		this.edges.clear();

		for (int i = 0; i < this.vertices.size(); i++) {
			Point p = this.vertices.get(i);

			// Connect the last vertex with the first one
			if (i == vertices.size() - 1) {
				this.edges.add(new Vector(p.getX(), p.getY(), this.vertices.get(0).getX() - p.getX(),
						this.vertices.get(0).getY() - p.getY()));
			} else {
				this.edges.add(new Vector(p.getX(), p.getY(), this.vertices.get(i + 1).getX() - p.getX(),
						this.vertices.get(i + 1).getY() - p.getY()));
			}
		}
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

			// TODO: REMOVE DEBUG
			System.out.println("Covered Tiles: " + coveredTilePositions.size());

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
		} else if (this.containsPoint(currentPoint)
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
	 * Function for finding out if a Point is inside the Polygon. The function
	 * utilizes a ray casting algorithm to generate Vectors intersecting the
	 * Polygon X-times. Based on the amount of times the ray intersects another
	 * Vector on its way it can be calculated if the Point is in- or outside the
	 * Polygon.
	 * 
	 * @param p
	 *            the Point that is going to be tested.
	 * @return true or false depending if the Point is inside the Polygon or
	 *         not.
	 */
	public boolean containsPoint(Point p) {
		Vector ray = new Vector(0, p.y, p.x, 0);
		List<Point> intersections = new ArrayList<Point>();
		List<Vector> intersectionEdges = new ArrayList<Vector>();

		// Get intersections from all edges to a Vector from its starting Point
		// to the target Point.
		// -> Do NOT count vertices (twice)!
		for (Vector vec : this.edges) {
			Point intersectionPoint = ray.getIntersection(vec);

			// Intersection Point is null if the lines are parallel!
			if (intersectionPoint != null) {
				Double neededMultiplierRay = ray.getNeededMultiplier(intersectionPoint);
				Double neededMultiplierVector = vec.getNeededMultiplier(intersectionPoint);

				// Needed multiplier of a Vector is null if it is the origin,
				// which are ignored since another Vector will end here!
				if ((neededMultiplierRay > 0. && neededMultiplierRay <= 1.) && neededMultiplierVector != null
						&& (neededMultiplierVector > 0. && neededMultiplierVector <= 1.)
						&& !intersections.contains(intersectionPoint)) {
					intersections.add(intersectionPoint);
					intersectionEdges.add(vec);
				}
			}
		}

		// Count the remaining elements. Even = outside, odd = inside!
		return intersections.size() % 2 != 0;
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
		this.generateEdges();
	}

	/**
	 * Adds a Point to the Polygon at the given index that resembles a vertex.
	 * 
	 * @param p
	 *            the Point that is going to be added.
	 * @param index
	 *            the index at which the Point is inserted.
	 */
	public void addVertexAt(Point p, int index) {
		this.vertices.add(index, p);
		// TODO: Possible Change: Optimize!
		this.generateEdges();
	}

}
