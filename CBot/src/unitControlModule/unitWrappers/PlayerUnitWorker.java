package unitControlModule.unitWrappers;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.Unit;

// TODO: UML
/**
 * PlayerUnitWorker.java --- Wrapper for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class PlayerUnitWorker extends PlayerUnit {

	protected static final int MAX_NUMBER_MINING = 2;
	protected static final int MAX_NUMBER_GATHERING_GAS = 3;
	protected static final int PIXEL_GATHER_SEARCH_RADIUS = 350;
	
	public static HashMap<Unit, HashSet<Unit>> mappedAccesbileGatheringSources = new HashMap<>();
	
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
		Unit closestFreeMineralField = null;

		// Get all mineral fields
		for (Unit unit : this.getUnit().getUnitsInRadius(PIXEL_GATHER_SEARCH_RADIUS)) {
			if (unit.getType().isMineralField()) {
				// Add the mineral field to the HashMap if necessary
				if(!mappedAccesbileGatheringSources.containsKey(unit)) {
					mappedAccesbileGatheringSources.put(unit, new HashSet<Unit>());
				}
				
				HashSet<Unit> mappedUnits = mappedAccesbileGatheringSources.get(unit);
				
				// If the threshold is not reached, the Unit can gather there.
				if (mappedUnits.size() < MAX_NUMBER_MINING && (closestFreeMineralField == null
						|| this.unit.getDistance(unit) < this.unit.getDistance(closestFreeMineralField))) {
					closestFreeMineralField = unit;
				}
			}
		}
		return closestFreeMineralField;
	}

	/**
	 * Function for finding the closest free gas source.
	 * 
	 * @return the closest free gas source.
	 */
	protected Unit findClosestFreeGasSource() {
		
		// TODO: Implementation: findClosestFreeGasSource()
		
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
