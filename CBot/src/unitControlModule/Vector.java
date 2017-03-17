package unitControlModule;

/**
 * Vector.java --- Class used for Vector operations.
 * 
 * @author P H - 25.02.2017
 *
 */
public class Vector {

	private final double intersecMaxDiff = Math.pow(10, -6);
	private final double neededMPMaxDiff = Math.pow(10, -1);
	public int x, y, dirX = 0, dirY = 0;

	public Vector(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vector(int x, int y, int dirX, int dirY) {
		this(x, y);

		this.dirX = dirX;
		this.dirY = dirY;
	}

	// -------------------- Functions

	public void rotateLeftDEG(double alpha) {
		this.rotateLeftRAD(Math.toRadians(alpha));
	}

	public void rotateLeftRAD(double alpha) {
		int newDirX = (int) (this.dirX * Math.cos(alpha) + this.dirY * Math.sin(alpha));
		int newDirY = (int) (-1. * this.dirX * Math.sin(alpha) + this.dirY * Math.cos(alpha));
		this.dirX = newDirX;
		this.dirY = newDirY;
	}

	public void rotateRightDEG(double alpha) {
		this.rotateRightRAD(Math.toRadians(alpha));
	}

	public void rotateRightRAD(double alpha) {
		int newDirX = (int) (this.dirX * Math.cos(alpha) - this.dirY * Math.sin(alpha));
		int newDirY = (int) (this.dirX * Math.sin(alpha) + this.dirY * Math.cos(alpha));
		this.dirX = newDirX;
		this.dirY = newDirY;
	}

	public double length() {
		return Math.sqrt(Math.pow(this.dirX, 2) + Math.pow(this.dirY, 2));
	}

	/**
	 * Function for determining the multiplier (r) with which the current Vector
	 * calculates the X and Y values of the given Vector (-> Point).
	 * 
	 * @param point
	 *            the Vector (-> Point) the current Vector has to end at.
	 * @return either the multiplier with which the targeted Vector (-> Point)
	 *         is being reached or null, if the Point can not be reached.
	 */
	public Double getNeededMultiplier(Vector point) {
		// r = Vector length / direction multiplier
		double rX = new Double(new Double(-1 * this.x + point.x) / new Double(this.dirX));
		double rY = new Double(new Double(-1 * this.y + point.y) / new Double(this.dirY));

		if (this.dirX == 0 && this.x == point.x) {
			rX = rY;
		}
		if (this.dirY == 0 && this.y == point.y) {
			rY = rX;
		}

		if (Math.abs(Math.abs(rX) - Math.abs(rY)) < this.neededMPMaxDiff) {
			return rX; // Could also be rY
		} else {
			return null;
		}
	}

	/**
	 * Function for getting the intersection between this Vector and another
	 * one.
	 * 
	 * @param vectorB
	 *            the Vector the check is done against.
	 * @return the Point (-> Vector with a length of 0) at which both Vectors
	 *         intersect each other or null if they do not intersect each other.
	 */
	public Vector getIntersection(Vector vectorB) {
		// Vector-length / -direction multipliers
		Double r = null;
		Double s = (new Double(this.dirX * (this.y - vectorB.y) - this.dirY * (this.x - vectorB.x)))
				/ new Double(this.dirX * vectorB.dirY - vectorB.dirX * this.dirY);

		if (this.dirX == 0) {
			r = (new Double(vectorB.y - this.y + s * vectorB.dirY)) / new Double(this.dirY);
		} else {
			r = (new Double(vectorB.x - this.x + s * vectorB.dirX)) / new Double(this.dirX);
		}

		if (Math.abs(Math.abs(this.x + r * this.dirX) - Math.abs(vectorB.x + s * vectorB.dirX)) < this.intersecMaxDiff
				&& Math.abs(Math.abs(this.y + r * this.dirY)
						- Math.abs(vectorB.y + s * vectorB.dirY)) < this.intersecMaxDiff) {
			return new Vector((int) (this.x + r * this.dirX), (int) (this.y + r * this.dirY));
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
		int newDirX = (int)((1. / this.length()) * this.dirX);
		int newDirY = (int)((1. / this.length()) * this.dirY);
		this.dirX = newDirX;
		this.dirY = newDirY;
	}
}
