package informationStorage;

import java.util.HashSet;

import bwapi.Unit;

// TODO: UML ADD
/**
 * SiegeTankStorage.java --- Shared storage Class for
 * {@link PlayerUnitTerran_SiegeTank} instances, mainly due to them needing a
 * storage for already grouped Units.
 * 
 * @author P H - 18.12.2017
 *
 */
public class SiegeTankStorage {

	// Units that were already grouped once. Needed due to the Siege_Tanks
	// morphing into other forms and therefore reseting the PlayerUnit-instance
	// they are based on.
	// -> Any already stored values are lost!
	private HashSet<Unit> alreadyGroupedUnits = new HashSet<>();

	public SiegeTankStorage() {

	}

	// -------------------- Functions

	/**
	 * Function for marking a Unit as already grouped.
	 * 
	 * @param unit
	 *            the Unit that is going to be marked.
	 */
	public void markAsGrouped(Unit unit) {
		this.alreadyGroupedUnits.add(unit);
	}

	/**
	 * Function for unmarking a Unit from the Set of grouped ones.
	 * 
	 * @param unit
	 *            the Unit that is going to be unmarked.
	 */
	public void unmarkAsGrouped(Unit unit) {
		this.alreadyGroupedUnits.remove(unit);
	}

	/**
	 * Function for checking if a Unit was already marked as grouped once.
	 * 
	 * @param unit
	 *            the Unit that is going to be checked.
	 * @return true if the Unit was already marked once as grouped, otherwise
	 *         false.
	 */
	public boolean wasAlreadyMarkedOnceAsGrouped(Unit unit) {
		return this.alreadyGroupedUnits.contains(unit);
	}

	// ------------------------------ Getter / Setter

}
