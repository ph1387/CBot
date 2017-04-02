package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;

import bwapi.Unit;

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

	// Mapped: gathering sources (Units) -> Units (worker)
	// Each gathering source holds the Units that are currently working on it.
	public static HashMap<Unit, ArrayList<Unit>> mappedAccessibleGatheringSources = new HashMap<Unit, ArrayList<Unit>>();
	// Used to prevent double mapping of the same gathering source in one cycle.
	public static HashMap<Unit, ArrayList<Unit>> mappedSourceContenders = new HashMap<Unit, ArrayList<Unit>>();

	protected Unit closestFreeMineralField;
	protected Unit closestFreeGasSource;

	public PlayerUnitWorker(Unit unit) {
		super(unit);
	}

	// -------------------- Functions

	/**
	 * Should be called at least one time from the sub class if overwritten.
	 * 
	 * @see unitControlModule.unitWrappers.PlayerUnit#customUpdate()
	 */
	@Override
	protected void customUpdate() {
		final Unit mappedUnit = this.unit;
		final HashSet<Unit> mappedSource = new HashSet<>();

		// Get all assigned gathering source(s) for this Unit.
		mappedAccessibleGatheringSources.forEach(new BiConsumer<Unit, ArrayList<Unit>>() {
			@Override
			public void accept(Unit unit, ArrayList<Unit> set) {
				if (set.contains(mappedUnit)) {
					mappedSource.add(unit);
				}
			}
		});

		// Remove any previously contended spots.
		if (mappedSourceContenders.containsKey(this.closestFreeMineralField)) {
			mappedSourceContenders.get(this.closestFreeMineralField).remove(this.unit);
		}
		if (mappedSourceContenders.containsKey(this.closestFreeGasSource)) {
			mappedSourceContenders.get(this.closestFreeGasSource).remove(this.unit);
		}

		if (mappedSource.isEmpty()) {
			this.markContenders();
		}
	}

	/**
	 * Mark a gathering source as contender so that no other worker can set it
	 * as their closest free gathering source. This can only be done if the Unit
	 * is currently not mapped to an actual accessible gathering source.
	 */
	protected void markContenders() {
		Unit mineralField = this.findClosestFreeMineralField();
		Unit gasSource = this.findClosestFreeGasSource();

		// Create new entries if necessary.
		if (!mappedSourceContenders.containsKey(mineralField)) {
			mappedSourceContenders.put(mineralField, new ArrayList<Unit>());
		}
		if (!mappedSourceContenders.containsKey(gasSource)) {
			mappedSourceContenders.put(gasSource, new ArrayList<Unit>());
		}
		if (!mappedAccessibleGatheringSources.containsKey(mineralField)) {
			mappedAccessibleGatheringSources.put(mineralField, new ArrayList<Unit>());
		}
		if (!mappedAccessibleGatheringSources.containsKey(gasSource)) {
			mappedAccessibleGatheringSources.put(gasSource, new ArrayList<Unit>());
		}

		// If a space for gathering a resource is free, set this Unit as a
		// contender for the spot. Contended sources are assigned to a Unit
		// while the action is executed so that, if the found source has no
		// free spot, a new one will be eventually found in one of the next
		// iterations.
		if (mappedSourceContenders.get(mineralField).size()
				+ mappedAccessibleGatheringSources.get(mineralField).size() < MAX_NUMBER_MINING) {
			mappedSourceContenders.get(mineralField).add(this.unit);
			this.closestFreeMineralField = mineralField;
		}
		if (mappedSourceContenders.get(gasSource).size()
				+ mappedAccessibleGatheringSources.get(gasSource).size() < MAX_NUMBER_GATHERING_GAS) {
			mappedSourceContenders.get(gasSource).add(this.unit);
			this.closestFreeGasSource = gasSource;
		}
	}

	/**
	 * Function for finding the closest free mineral field.
	 * 
	 * @return the closest free mineral field.
	 */
	protected Unit findClosestFreeMineralField() {
		Unit closestFreeMineralField = null;

		// Get all mineral fields
		for (Unit gatheringSource : this.getUnit().getUnitsInRadius(PIXEL_GATHER_SEARCH_RADIUS)) {
			if (gatheringSource.getType().isMineralField()) {
				closestFreeMineralField = this.checkAgainstMappedAccessibleSources(gatheringSource,
						closestFreeMineralField, MAX_NUMBER_MINING);
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
		Unit closestRefinery = null;

		// Get all mineral fields
		for (Unit gatheringSource : this.getUnit().getUnitsInRadius(PIXEL_GATHER_SEARCH_RADIUS)) {
			if (gatheringSource.getType().isRefinery()) {
				closestRefinery = this.checkAgainstMappedAccessibleSources(gatheringSource, closestRefinery,
						MAX_NUMBER_GATHERING_GAS);
			}
		}
		return closestRefinery;
	}

	/**
	 * Function for checking if a Unit can be mapped to a gathering source.
	 * Conditions are that the amount of Units already gathering there has to be
	 * less than a set threshold as well as the check against an already
	 * existing reference Unit, which is a gathering source of the same type
	 * with a set distance to the PlayerUnitWorker (Unit can be null => first
	 * other gathering source will be set). If no entry for the source is found,
	 * a new one is generated.
	 * 
	 * @param gatheringSource
	 *            the gathering source.
	 * @param referenceUnit
	 *            the currently chosen closest gathering source.
	 * @param workerThreshold
	 *            the threshold of workers, that can work at the specific
	 *            gathering source.
	 * @return the reference Unit if the distance to the new gathering source is
	 *         greater than the reference value or the threshold is reached. Or
	 *         the gathering source, if the threshold is not yet reached and the
	 *         distance is smaller than the distance towards the reference Unit.
	 */
	protected Unit checkAgainstMappedAccessibleSources(Unit gatheringSource, Unit referenceUnit, int workerThreshold) {
		// Create a new entry in the map if no other entry for the gathering
		// source is found.
		if (!mappedAccessibleGatheringSources.containsKey(gatheringSource)) {
			mappedAccessibleGatheringSources.put(gatheringSource, new ArrayList<Unit>());
		}

		ArrayList<Unit> mappedUnits = mappedAccessibleGatheringSources.get(gatheringSource);

		// If the threshold is not reached, the Unit can gather there.
		if (mappedUnits.size() < workerThreshold && (referenceUnit == null
				|| this.unit.getDistance(gatheringSource) < this.unit.getDistance(referenceUnit))) {
			return gatheringSource;
		}
		return referenceUnit;
	}

	// ------------------------------ Getter / Setter

	public Unit getClosestFreeMineralField() {
		return closestFreeMineralField;
	}

	public Unit getClosestFreeGasSource() {
		return closestFreeGasSource;
	}
}
