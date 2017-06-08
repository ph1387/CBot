package bwapiMath;

/**
 * Vector.java --- Class used for Vector operations.
 * 
 * @author P H - 25.02.2017
 *
 */
public class Vector extends Point {

	private final double INTERSEC_MAX_DIFF = Math.pow(10, -6);
	private final double NEEDED_MP_MAX_DIFF = Math.pow(10, -1);
	public double dirX = 0., dirY = 0.;

	public Vector(int x, int y) {
		super(x, y, Type.NONE);
	}

	public Vector(int x, int y, double dirX, double dirY) {
		this(x, y);

		this.dirX = dirX;
		this.dirY = dirY;
	}

	// -------------------- Functions

	@Override
	public Vector clone() {
		return new Vector(this.x, this.y, this.dirX, this.dirY);
	}

	@Override
	public String toString() {
		return "[" + this.x + " + " + this.dirX + ", " + this.y + " + " + this.dirY + "]";
	}

	/**
	 * @see #rotateLeftRAD(double)
	 * @param alpha
	 *            the degree at which the Vector is rotated.
	 */
	public void rotateLeftDEG(double alpha) {
		this.rotateLeftRAD(Math.toRadians(alpha));
	}

	/**
	 * Function for rotating a Vector left.
	 * 
	 * @param alpha
	 *            the radiant at which the Vector is rotated.
	 */
	public void rotateLeftRAD(double alpha) {
		double newDirX = this.dirX * Math.cos(alpha) + this.dirY * Math.sin(alpha);
		double newDirY = -1. * this.dirX * Math.sin(alpha) + this.dirY * Math.cos(alpha);
		this.dirX = newDirX;
		this.dirY = newDirY;
	}

	/**
	 * @see #rotateRightRAD(double)
	 * @param alpha
	 *            the degree at which the Vector is rotated.
	 */
	public void rotateRightDEG(double alpha) {
		this.rotateRightRAD(Math.toRadians(alpha));
	}

	/**
	 * Function for rotating a Vector right.
	 * 
	 * @param alpha
	 *            the radiant at which the Vector is rotated.
	 */
	public void rotateRightRAD(double alpha) {
		double newDirX = this.dirX * Math.cos(alpha) - this.dirY * Math.sin(alpha);
		double newDirY = this.dirX * Math.sin(alpha) + this.dirY * Math.cos(alpha);
		this.dirX = newDirX;
		this.dirY = newDirY;
	}

	public double length() {
		return Math.sqrt(Math.pow(this.dirX, 2) + Math.pow(this.dirY, 2));
	}

	/**
	 * Function for determining the multiplier (r) with which the current Vector
	 * calculates the X and Y values of the given Point.
	 * 
	 * @param point
	 *            the Point the current Vector has to end at.
	 * @return either the multiplier with which the targeted Vector (-> Point)
	 *         is being reached or null, if the Point can not be reached.
	 */
	public Double getNeededMultiplier(Point point) {
		// r = Vector length or direction multiplier
		Double rX = null;
		Double rY = null;
		Double returnValue = null;

		// If the value of the direction vector in one side is zero, there only
		// needs to be one check for the end point.
		if (this.dirX == 0.) {
			rY = new Double(new Double(-1 * this.y + point.y) / new Double(this.dirY));

			if (Math.abs(Math.abs(this.x) - Math.abs(point.x)) < this.NEEDED_MP_MAX_DIFF) {
				returnValue = rY;
			}
		} else if (this.dirY == 0.) {
			rX = new Double(new Double(-1 * this.x + point.x) / new Double(this.dirX));

			if (Math.abs(Math.abs(this.y) - Math.abs(point.y)) < this.NEEDED_MP_MAX_DIFF) {
				returnValue = rX;
			}
		}

		// Is only true if the check above was not successful
		if (returnValue == null && (rX == null || rY == null)) {
			// Could be split
			rX = new Double(new Double(-1 * this.x + point.x) / new Double(this.dirX));
			rY = new Double(new Double(-1 * this.y + point.y) / new Double(this.dirY));

			if (this.dirX == 0 && this.x == point.x) {
				rX = rY;
			}
			if (this.dirY == 0 && this.y == point.y) {
				rY = rX;
			}

			if (Math.abs(Math.abs(rX) - Math.abs(rY)) < this.NEEDED_MP_MAX_DIFF) {
				returnValue = rX; // Could also be rY
			}
		}
		return returnValue;
	}

	/**
	 * Function for getting the intersection between this Vector and another
	 * one.
	 * 
	 * @param vectorB
	 *            the Vector the check is done against.
	 * @return the Point at which both Vectors intersect each other or null if
	 *         they do not intersect each other.
	 */
	public Point getIntersection(Vector vectorB) {
		// Vector-length / -direction multipliers
		Double r = null;
		Double s = (new Double(this.dirX * (this.y - vectorB.y) - this.dirY * (this.x - vectorB.x)))
				/ new Double(this.dirX * vectorB.dirY - vectorB.dirX * this.dirY);

		if (this.dirX == 0) {
			r = (new Double(vectorB.y - this.y + s * vectorB.dirY)) / new Double(this.dirY);
		} else {
			r = (new Double(vectorB.x - this.x + s * vectorB.dirX)) / new Double(this.dirX);
		}

		if (Math.abs(Math.abs(this.x + r * this.dirX) - Math.abs(vectorB.x + s * vectorB.dirX)) < this.INTERSEC_MAX_DIFF
				&& Math.abs(Math.abs(this.y + r * this.dirY)
						- Math.abs(vectorB.y + s * vectorB.dirY)) < this.INTERSEC_MAX_DIFF) {
			return new Point((int) (this.x + r * this.dirX), (int) (this.y + r * this.dirY), this.type);
		} else {
			return null;
		}
	}

	/**
	 * Function for generating the cross product between this vector and another
	 * given one.
	 * 
	 * @param vectorB
	 *            another Vector with which the cross product is calculated.
	 * @return the cross product between this and the given Vector.
	 */
	public double getCrossProduct(Vector vectorB) {
		return this.dirX * vectorB.dirY - vectorB.dirX * this.dirY;
	}

	/**
	 * Normalizes the Vector.
	 */
	public void normalize() {
		double newDirX = (1. / this.length()) * this.dirX;
		double newDirY = (1. / this.length()) * this.dirY;
		this.dirX = newDirX;
		this.dirY = newDirY;
	}

	/**
	 * Sets the Vector to a specific length.
	 * 
	 * @param length
	 *            the length that the Vector is supposed to have.
	 */
	public void setToLength(double length) {
		this.normalize();
		
		this.dirX *= length;
		this.dirY *= length;
	}

	/**
	 * Function for generating the scalar / dot product of the current and a
	 * given Vector.
	 * 
	 * @param vectorB
	 *            the Vector the scalar product is calculated with.
	 * @return the scalar / dot product of the current and the given Vector.
	 */
	public double getScalarProduct(Vector vectorB) {
		double xProduct = this.dirX * vectorB.dirX;
		double yProduct = this.dirY * vectorB.dirY;

		return (xProduct + yProduct);
	}

	/**
	 * Function for calculating the angle of this Vector to another given one in
	 * <b>degrees</b>.
	 * 
	 * @param vectorB
	 *            the Vector the angle is calculated to.
	 * @return the angle of this Vector to another given one in <b>degrees</b>.
	 */
	public double getAngleToVector(Vector vectorB) {
		return Math.toDegrees(Math.acos(this.getScalarProduct(vectorB) / (this.length() * vectorB.length())));
	}
}
