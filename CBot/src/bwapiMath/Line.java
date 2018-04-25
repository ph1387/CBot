package bwapiMath;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import bwapi.Color;
import bwapi.Game;
import core.Core;

/**
 * Line.java --- Class used for line operations (I.e.: Intersections).
 * 
 * @author P H - 28.01.2018
 *
 */
public class Line {

	private static final Color DEFAULT_COLOR = new Color(255, 255, 255);
	// The default size of the box around the provided Point at a intersection
	// calculation. This is needed since a Line does not have any covered area
	// and therefore a check with the intersects function always returns false.
	private static final int DEFAULT_POINT_BOX_SIZE = 16;

	private Point startPoint;
	private Point endPoint;
	private double length;

	public Line(Point startPoint, Point endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;

		this.updateLength();
	}

	public Line(int startX, int startY, int endX, int endY) {
		this(new Point(startX, startY, Point.Type.POSITION), new Point(endX, endY, Point.Type.POSITION));
	}

	public Line(Vector vector) {
		this(vector.x, vector.y, vector.x + (int) vector.getDirX(), vector.y + (int) vector.getDirY());
	}

	// -------------------- Functions

	/**
	 * Function for updating the length of the Line.
	 */
	private void updateLength() {
		this.length = Math.sqrt(
				Math.pow(this.endPoint.x - this.startPoint.x, 2) + Math.pow(this.endPoint.y - this.startPoint.y, 2));
	}

	/**
	 * Function for retrieving the length of the Line instance.
	 * 
	 * @return the length of the Line instance.
	 */
	public double length() {
		return this.length;
	}

	/**
	 * Function for checking if the Line instance intersects another Line.
	 * 
	 * @param otherLine
	 *            the other Line with which the check will be performed.
	 * @return true if the Line instance intersects the provided one, otherwise
	 *         false.
	 */
	public boolean intersects(Line otherLine) {
		return Converter.convert(this).intersectsLine(Converter.convert(otherLine));
	}

	/**
	 * Function for calculating the actual intersection Point between this Line
	 * instance and another provided one.
	 * 
	 * @param otherLine
	 *            the other Line with which the check will be performed.
	 * @return the Point at which the Lines intersect with each other.
	 */
	public Point getIntersection(Line otherLine) {
		Point2D intersection = getIntersection(Converter.convert(this), Converter.convert(otherLine));

		return Converter.convert(intersection);
	}

	/**
	 * Function for calculating the actual intersection Point between two Lines.
	 * 
	 * @param line1
	 *            the first Line.
	 * @param line2
	 *            the second Line.
	 * @return the Point at which both provided Lines intersect each other.
	 */
	public static Point2D.Double getIntersection(Line2D.Double line1, Line2D.Double line2) {

		double x1, y1, x2, y2, x3, y3, x4, y4;
		x1 = line1.x1;
		y1 = line1.y1;
		x2 = line1.x2;
		y2 = line1.y2;
		x3 = line2.x1;
		y3 = line2.y1;
		x4 = line2.x2;
		y4 = line2.y2;
		double x = ((x2 - x1) * (x3 * y4 - x4 * y3) - (x4 - x3) * (x1 * y2 - x2 * y1))
				/ ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));
		double y = ((y3 - y4) * (x1 * y2 - x2 * y1) - (y1 - y2) * (x3 * y4 - x4 * y3))
				/ ((x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4));

		return new Point2D.Double(x, y);
	}

	/**
	 * Convenience function.<br>
	 * Function for checking if the Line instance contains a Point.
	 * 
	 * @param point
	 *            the Point that is going to be checked for.
	 * @return true if the Point is on the Line, otherwise false.
	 */
	public boolean contains(Point point) {
		return contains(point, DEFAULT_POINT_BOX_SIZE);
	}

	/**
	 * Function for checking if the Line instance contains a Point.
	 * 
	 * @param point
	 *            the Point that is going to be checked for.
	 * @param totalPointBoxSize
	 *            the total size of the box around the Point that is used in
	 *            order to check if the Line contains it. The larger the total
	 *            size, the further the Point can be away from the Line.
	 * @return true if the Point is on the Line, otherwise false.
	 */
	public boolean contains(Point point, int totalPointBoxSize) {
		// Box around the given Point is needed since a Line does not have any
		// area. Therefore it can not intersect a single Point.
		// -> Always returns false!
		int boxX = point.x - totalPointBoxSize / 2;
		int boxY = point.y - totalPointBoxSize / 2;
		int width = totalPointBoxSize;
		int height = totalPointBoxSize;

		return Converter.convert(this).intersects(boxX, boxY, width, height);
	}

	/**
	 * Convenience function.
	 */
	public void display() {
		this.display(DEFAULT_COLOR);
	}

	/**
	 * Function for displaying the Line on the ingame map.
	 * 
	 * @param color
	 *            the color of the displayed Line.
	 */
	public void display(Color color) {
		Game game = Core.getInstance().getGame();

		game.drawLineMap(this.startPoint.toPosition(), this.endPoint.toPosition(), color);
	}

	// ------------------------------ Getter / Setter

	public Point getStartPoint() {
		return startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

}
