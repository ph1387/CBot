package informationStorage;

import java.util.HashMap;

import bwapi.Unit;

/**
 * UnitMapper.java --- Class for mapping two Units together. This includes i.e.
 * Units following another one.
 * 
 * @author P H - 09.10.2017
 *
 */
public class UnitMapper {

	// Key: The mapped Unit (I.e. the following Unit).
	// Value: The Unit that the first one is mapped to (I.e. the Unit that is
	// being followed).
	private HashMap<Unit, Unit> mappedUnits = new HashMap<>();

	public UnitMapper() {

	}

	// -------------------- Functions

	/**
	 * Function for testing if a specific Unit is already being mapped by
	 * another Unit.
	 * 
	 * @param unit
	 *            the Unit / Value that is tested.
	 * @return true if a Unit is mapped to that specific Unit, false if not.
	 */
	public boolean isBeingMapped(Unit unit) {
		return this.mappedUnits.containsValue(unit);
	}

	/**
	 * Function for testing if a Unit is mapped to another Unit.
	 * 
	 * @param unit
	 *            the Unit / Key that is tested.
	 * @return true if the Unit is mapped to another Unit, false if not.
	 */
	public boolean isMapped(Unit unit) {
		return this.mappedUnits.containsKey(unit);
	}

	/**
	 * Function for mapping a Unit to another one.
	 * 
	 * @param key
	 *            the Unit that is going to be mapped as key.
	 * @param value
	 *            the Unit that is going to be mapped as value.
	 * @return true if the mapping of the Unit to the target was a success and
	 *         no other Unit was mapped to it.
	 */
	public boolean mapUnit(Unit key, Unit value) {
		return !this.mappedUnits.containsKey(key) && this.mappedUnits.put(key, value) == null;
	}

	/**
	 * Function for removing a mapping of a Unit.
	 * 
	 * @param key
	 *            the Unit whose mapping to a target is going to be removed.
	 */
	public void unmapUnit(Unit key) {
		this.mappedUnits.remove(key);
	}

	// ------------------------------ Getter / Setter

	/**
	 * Function for retrieving the Unit that the provided Unit is currently
	 * mapped to.
	 * 
	 * @param key
	 *            the Unit whose mapped Unit is requested.
	 * @return the Unit that the provided key Unit is currently being mapped to
	 *         or null, if the provided Unit is currently not mapped to any.
	 */
	public Unit getMappedUnit(Unit key) {
		return this.mappedUnits.get(key);
	}

	/**
	 * Function for retrieving the key Unit that is mapped to the provided value
	 * Unit.
	 * 
	 * @param value
	 *            the Unit whose Unit / Key is being requested.
	 * @return the Unit that is mapped as key to the provided Unit or null, if
	 *         the Unit is not being mapped by another Unit.
	 */
	public Unit getMappingUnit(Unit value) {
		Unit matchingVessel = null;

		for (Unit vessel : this.mappedUnits.keySet()) {
			if (this.mappedUnits.get(vessel) == value) {
				matchingVessel = vessel;

				break;
			}
		}

		return matchingVessel;
	}

}
