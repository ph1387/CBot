package bwapiMath;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

// TODO: UML ADD
/**
 * Converter.java --- Class used for converting java.awt objects into wrapper
 * ones and vice versa.
 * 
 * @author P H - 28.01.2018
 *
 */
public class Converter {

	/**
	 * Function for converting a Point into it's java.awt equivalent.
	 * 
	 * @param point
	 *            the Point that is going to be converted.
	 * @return the java.awt equivalent of the provided Point.
	 */
	public static Point2D convert(Point point) {
		return new Point2D.Double(point.x, point.y);
	}

	/**
	 * Function for converting a java.awt Point into it's wrapper equivalent.
	 * <br>
	 * Convenience function:<br>
	 * The Point's type is Point.Type.Position.
	 * 
	 * @param point
	 *            the Point that is going to be converted.
	 * @return the wrapper equivalent of the provided Point.
	 */
	public static Point convert(Point2D point) {
		return convert(point, Point.Type.POSITION);
	}

	/**
	 * Function for converting a java.awt Point into it's wrapper equivalent.
	 * 
	 * @param point
	 *            the Point that is going to be converted.
	 * @param type
	 *            the type of the wrapper Point.
	 * @return the wrapper equivalent of the provided Point.
	 */
	public static Point convert(Point2D point, Point.Type type) {
		return new Point((int) point.getX(), (int) point.getY(), type);
	}

	/**
	 * Function for converting a Line into it's java.awt equivalent.
	 * 
	 * @param line
	 *            the Line that is going to be converted.
	 * @return the java.awt equivalent of the provided Line.
	 */
	public static Line2D.Double convert(Line line) {
		int startX = line.getStartPoint().x;
		int startY = line.getStartPoint().y;
		int endX = line.getEndPoint().x;
		int endY = line.getEndPoint().y;

		return new Line2D.Double(startX, startY, endX, endY);
	}

	/**
	 * Function for converting a java.awt Line into it's wrapper equivalent.
	 * 
	 * @param line
	 *            the Line that is going to be converted.
	 * @return the wrapper equivalent of the provided Line.
	 */
	public static Line convert(Line2D.Double line) {
		int startX = (int) line.getX1();
		int startY = (int) line.getY1();
		int endX = (int) line.getX2();
		int endY = (int) line.getY2();

		return new Line(startX, startY, endX, endY);
	}

}
