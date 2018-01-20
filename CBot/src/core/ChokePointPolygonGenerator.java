package core;

import java.util.ArrayList;
import java.util.List;

import bwapiMath.Point;
import bwapiMath.Polygon;
import bwapiMath.Vector;
import bwta.Chokepoint;

/**
 * ChokePointPolygonGenerator.java --- Class for generating Polygons around
 * ChokePoints.
 * 
 * @author P H - 30.11.2017
 *
 */
public class ChokePointPolygonGenerator {

	private static final double DEFAULT_CHOKEPOINT_VECTOR_LENGTH = 64;
	private static final int DEFAULT_CHOKEPOINT_VECTOR_ROTATION = 90;

	// -------------------- Functions

	/**
	 * Convenience function.
	 * 
	 * @see #generatePolygonAtChokePoint(Chokepoint, double, int)
	 * @param chokePoint
	 *            the {@link Chokepoint} that the {@link Polygon} is being
	 *            created around.
	 * @return a {@link Polygon} that is being created around a
	 *         {@link Chokepoint}.
	 */
	public static Polygon generatePolygonAtChokePoint(Chokepoint chokePoint) {
		return generatePolygonAtChokePoint(chokePoint, DEFAULT_CHOKEPOINT_VECTOR_LENGTH,
				DEFAULT_CHOKEPOINT_VECTOR_ROTATION);
	}

	/**
	 * Function for generating a Polygon around a ChokePoint.
	 * 
	 * @param chokePoint
	 *            the {@link Chokepoint} that the {@link Polygon} is being
	 *            created around.
	 * @param vectorLength
	 *            the length of the Vectors that are being used to generate the
	 *            Polygon. A side perpendicular to the ChokePoint itself has the
	 *            length 2*vectorLength (When using 90° as rotation).
	 * @param the
	 *            rotation in <b>degrees</b> that the Vectors generating the
	 *            Polygon will be rotated.
	 * @return a {@link Polygon} that is being created around a
	 *         {@link Chokepoint}.
	 */
	public static Polygon generatePolygonAtChokePoint(Chokepoint chokePoint, double vectorLength, int vectorRotation) {
		List<Point> vertices = new ArrayList<>();

		// Generate four Vectors: Each end Position of the ChokPoint is the
		// start of two of them. Use the other Position as end Position to
		// ensure the 90° rotation later on.
		Vector firstVectorRotLeft = new Vector(chokePoint.getSides().first, chokePoint.getSides().second);
		Vector firstVectorRotRight = new Vector(chokePoint.getSides().first, chokePoint.getSides().second);
		Vector secondVectorRotLeft = new Vector(chokePoint.getSides().second, chokePoint.getSides().first);
		Vector secondVectorRotRight = new Vector(chokePoint.getSides().second, chokePoint.getSides().first);
		firstVectorRotLeft.setToLength(vectorLength);
		firstVectorRotRight.setToLength(vectorLength);
		secondVectorRotLeft.setToLength(vectorLength);
		secondVectorRotRight.setToLength(vectorLength);

		// Rotate the Vectors in the different directions (2x90° left and
		// right).
		firstVectorRotLeft.rotateLeftDEG(vectorRotation);
		firstVectorRotRight.rotateRightDEG(vectorRotation);
		secondVectorRotLeft.rotateLeftDEG(vectorRotation);
		secondVectorRotRight.rotateRightDEG(vectorRotation);

		// Combine the end Positions of the Vectors to a Polygon.
		vertices.add(new Point(firstVectorRotLeft.getX() + (int) (firstVectorRotLeft.getDirX()),
				firstVectorRotLeft.getY() + (int) (firstVectorRotLeft.getDirY()), Point.Type.POSITION));
		vertices.add(new Point(firstVectorRotRight.getX() + (int) (firstVectorRotRight.getDirX()),
				firstVectorRotRight.getY() + (int) (firstVectorRotRight.getDirY()), Point.Type.POSITION));
		vertices.add(new Point(secondVectorRotLeft.getX() + (int) (secondVectorRotLeft.getDirX()),
				secondVectorRotLeft.getY() + (int) (secondVectorRotLeft.getDirY()), Point.Type.POSITION));
		vertices.add(new Point(secondVectorRotRight.getX() + (int) (secondVectorRotRight.getDirX()),
				secondVectorRotRight.getY() + (int) (secondVectorRotRight.getDirY()), Point.Type.POSITION));

		return new Polygon(vertices);
	}

}
