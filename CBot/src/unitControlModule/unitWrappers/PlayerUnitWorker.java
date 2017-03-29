package unitControlModule.unitWrappers;

import bwapi.Unit;

// TODO: UML
/**
 * PlayerUnitWorker.java --- Wrapper for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class PlayerUnitWorker extends PlayerUnit {

	protected Unit closestFreeMineralField = null;
	protected Unit closestFreeGasSource = null;

	public PlayerUnitWorker(Unit unit) {
		super(unit);
	}

	// -------------------- Functions

	/**
	 * Should be called at least one time from the sub class.
	 * 
	 * @see unitControlModule.unitWrappers.PlayerUnit#customUpdate()
	 */
	@Override
	protected void customUpdate() {
		this.closestFreeMineralField = this.findClosestFreeMineralField();
		this.closestFreeGasSource = this.findClosestFreeGasSource();
	}

	/**
	 * Function for finding the closest free mineral field.
	 * 
	 * @return the closest free mineral field.
	 */
	protected Unit findClosestFreeMineralField() {

		// TODO: ACTUAL IMPLEMENTATION

		Unit closestMineralField = null;

		// Find the closest mineral field to mine from
		for (Unit mineralField : this.getUnit().getUnitsInRadius(250)) {
			if (mineralField.getType().isMineralField()) {
				if (closestMineralField == null
						|| unit.getDistance(mineralField) < unit.getDistance(closestMineralField)) {
					closestMineralField = mineralField;
				}
			}
		}
		return closestMineralField;
	}

	/**
	 * Function for finding the closest free gas source.
	 * 
	 * @return the closest free gas source.
	 */
	protected Unit findClosestFreeGasSource() {
		return null;
	}

	// ------------------------------ Getter / Setter

	public Unit getClosestFreeMineralField() {
		return closestFreeMineralField;
	}

	public Unit getClosestFreeGasSource() {
		return closestFreeGasSource;
	}
}
