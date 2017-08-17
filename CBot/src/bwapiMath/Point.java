package bwapiMath;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import core.Core;

/**
 * Point.java --- Stores an X and Y value.
 * 
 * @author P H - 22.04.2017
 *
 */
public class Point {

	// TODO: UML ADD
	private static final int DEFAULT_RADIUS = 5;
	// TODO: UML ADD
	private static final boolean DEFAULT_FILLED = true;
	// TODO: UML ADD
	private static final Color DEFAULT_COLOR = new Color(255, 255, 255);
	
	public static enum Direction {
		NULL, LEFT, TOP, RIGHT, BOTTOM
	}

	public static enum Type {
		NONE, POSITION, TILEPOSITION
	}

	protected Integer x;
	protected Integer y;
	protected Type type = Type.NONE;

	public Point() {

	}

	public Point(Integer x, Integer y, Type type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	/**
	 * @param p
	 *            the Position that is going to be converted to a Point.
	 */
	public Point(Position p) {
		this(p.getX(), p.getY(), Type.POSITION);
	}

	/**
	 * @param p
	 *            the TilePosition that is going to be converted.
	 */
	public Point(TilePosition p) {
		this(p.getX(), p.getY(), Type.TILEPOSITION);
	}

	// -------------------- Functions

	@Override
	public boolean equals(Object p) {
		boolean isEqual = false;

		if (p instanceof Point) {
			isEqual = this.x.equals(((Point) p).x) && this.y.equals(((Point) p).y);
		}
		return isEqual;
	}

	@Override
	public String toString() {
		return "[" + this.x + ", " + this.y + "]";
	}

	@Override
	public Point clone() {
		return new Point(this.x.intValue(), this.y.intValue(), this.type);
	}

	/**
	 * Transforms the Point into a Position object.
	 * 
	 * @return a Position object with the Point's x and y values.
	 */
	public Position toPosition() {
		return new Position(this.x, this.y);
	}

	/**
	 * Transforms the Point into a TilePosition object.
	 * 
	 * @return a TilePosition object with the Point's x and y values.
	 */
	public TilePosition toTilePosition() {
		return new TilePosition(this.x, this.y);
	}

	/**
	 * Transforms the Point into a Position object. Can be used if the the
	 * currently stored Point is in a TilePosition format.
	 * 
	 * @return a new Position object with the Point's x and y values multiplied
	 *         with the ingame tile size.
	 */
	public Position transformFromTilePositionToPosition() {
		if (this.type == Type.POSITION) {
			return new Position(this.x, this.y);
		} else {
			return new Position(this.x * Core.getInstance().getTileSize(), this.y * Core.getInstance().getTileSize());
		}
	}

	/**
	 * Transforms the Point into a TilePosition object. Can be used if the the
	 * currently stored Point is in a Position format.
	 * 
	 * @return a new TilePosition object with the Point's x and y values divided
	 *         by the ingame tile size.
	 */
	public TilePosition transformFromPositionToTilePosition() {
		if (this.type == Type.TILEPOSITION) {
			return new TilePosition(this.x, this.y);
		} else {
			return new TilePosition(this.x / Core.getInstance().getTileSize(),
					this.y / Core.getInstance().getTileSize());
		}
	}

	/**
	 * Convenience function.
	 * 
	 * @see #getDirectionToSecondPoint(Point, Point)
	 * @param to
	 *            the Point that the direction to which the direction of this
	 *            one is calculated to.
	 * @return direction of this Point towards the second one.
	 */
	public Direction getDirectionToSecondPoint(Point to) {
		return getDirectionToSecondPoint(this, to);
	}

	/**
	 * Function for getting the direction of a Point to another one.
	 * <p>
	 * -> In which direction must the first Point move to meet the second one?
	 * 
	 * @param from
	 *            the Point that the direction is calculated from.
	 * @param to
	 *            the Point that the direction to which the direction of the
	 *            first one is calculated to.
	 * @return direction of the first given Point towards the second one.
	 */
	public static Direction getDirectionToSecondPoint(Point from, Point to) {
		int offsetX = to.x - from.x;
		int offsetY = to.y - from.y;
		Direction dir = Direction.NULL;

		// Either left/right or top/bottom can be considered.
		if (Math.abs(offsetX) > Math.abs(offsetY)) {
			// left/right
			if (offsetX < 0) {
				dir = Direction.RIGHT;
			} else {
				dir = Direction.LEFT;
			}
		} else {
			// top/bottom
			if (offsetY < 0) {
				dir = Direction.BOTTOM;
			} else {
				dir = Direction.TOP;
			}
		}
		return dir;
	}

	// TODO: UML ADD JAVADOC
	public void display() {
		this.display(DEFAULT_COLOR);
	}
	
	// TODO: UML ADD JAVADOC
	public void display(Color color) {
		this.display(color, DEFAULT_FILLED);
	}
	
	// TODO: UML ADD JAVADOC
	public void display(Color color, boolean filled) {
		this.display(DEFAULT_RADIUS, color, filled);
	}

	// TODO: UML ADD JAVADOC
	public void display(int radius, Color color, boolean filled) {
		if(filled) {
			Core.getInstance().getGame().drawCircleMap(this.toPosition(), radius, color, true);
		} else {
			Core.getInstance().getGame().drawCircleMap(this.toPosition(), radius, color);
		}
	}
	
	// ------------------------------ Getter / Setter

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

}
