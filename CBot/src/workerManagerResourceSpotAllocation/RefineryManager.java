package workerManagerResourceSpotAllocation;

import java.util.HashMap;

import bwapi.Unit;

/**
 * RefineryManager.java --- {@link ResourceManager} for refineries. The
 * {@link GatheringSource}s that this Class holds are all the currently existing
 * / constructed refineries on the map of the Player.
 * 
 * @author P H - 03.09.2017
 *
 */
class RefineryManager<T extends GatheringSource> extends ResourceManager<GatheringSource> {

	private HashMap<Unit, RefineryWrapper> mappedRefineries = new HashMap<>();

	public RefineryManager() {

	}

	// -------------------- Functions

	/**
	 * Function for adding a refinery to the ones that are being managed by this
	 * instance and considered in the accessible gathering sources.
	 * 
	 * @param unit
	 *            the refinery that is going to be managed by this Class.
	 */
	public void addRefinery(Unit unit) {
		RefineryWrapper refineryWrapper = new RefineryWrapper(unit, this);

		this.gatheringSources.add(refineryWrapper);
		this.mappedRefineries.put(unit, refineryWrapper);
	}

	/**
	 * Function for removing a refinery from the accessible gathering sources.
	 * 
	 * @param unit
	 *            the refinery that is going to be removed from the List of
	 *            managed gathering sources.
	 */
	public void removeRefinery(Unit unit) {
		GatheringSource refineryWrapper = this.mappedRefineries.get(unit);

		// Remove the mapped entries (List before HashMap!).
		this.gatheringSources.remove(refineryWrapper);
		this.mappedRefineries.remove(unit);
	}

}
