package unitControlModule.stateFactories.actions.executableActions;

import bwapiMath.Point;

// TODO: UML ADD JAVADOC
/**
 * RetreatPositionClusterElement.java --- 
 * @author P H - 19.08.2017
 *
 */
public class RetreatPositionClusterElement {

	private Point position;
	private RetreatUnit unit = null;
	private boolean isValid;

	// -------------------- Functions

	public RetreatPositionClusterElement(Point position, boolean isValid) {
		this.position = position;
		this.isValid = isValid;
	}
	
	public boolean isFree() {
		return this.isValid && this.unit == null;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public void reset() {
		this.unit = null;
	}

	// ------------------------------ Getter / Setter

	public Point getPosition() {
		return position;
	}

	public RetreatUnit getUnit() {
		return unit;
	}

	public void setUnit(RetreatUnit unit) {
		this.unit = unit;
	}
	
}
