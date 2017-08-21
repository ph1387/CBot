package unitControlModule.stateFactories.actions.executableActions;

import bwapiMath.Point;

/**
 * RetreatPositionClusterElement.java --- A part of a
 * {@link RetreatPositionCluster}. This Class stores the Point it was assigned
 * as well as the Unit that is assigned to it. Also it is possible to deny
 * access to a instance of this Class by setting the initial validation flag to
 * false. This completely locks the instance which is used for displaying
 * inaccessible Positions on the map.
 * 
 * @author P H - 19.08.2017
 *
 */
public class RetreatPositionClusterElement {

	// The Point the element is referring to.
	private Point position;
	// The Unit that is assigned to the stored Point.
	private RetreatUnit unit = null;
	// The flag that determines if the Point can be accessed or not.
	private boolean isValid;

	// -------------------- Functions

	/**
	 * @param position
	 *            the Point the cluster element is referring to.
	 * @param isValid
	 *            the flag if the Point the cluster element is referring to is
	 *            valid or not.
	 */
	public RetreatPositionClusterElement(Point position, boolean isValid) {
		this.position = position;
		this.isValid = isValid;
	}

	/**
	 * Function for testing if the cluster element can be accessed by and filled
	 * with a Unit.
	 * 
	 * @return true if the element has a valid Point and is not currently
	 *         storing a Unit.
	 */
	public boolean isFree() {
		return this.isValid && this.unit == null;
	}

	/**
	 * Function for checking if the cluster element stores a valid Point.
	 * 
	 * @return true if the cluster element stores a valid Point, false if it
	 *         does not.
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Function for removing the assigned Unit from the cluster element.
	 */
	public void reset() {
		this.unit = null;
	}

	// ------------------------------ Getter / Setter

	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}

	public Point getPosition() {
		return position;
	}

	public RetreatUnit getUnit() {
		return unit;
	}

	public void setUnit(RetreatUnit unit) {
		if (this.isFree()) {
			this.unit = unit;
		}
	}

}
