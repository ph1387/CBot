package workerManagerResourceSpotAllocation;

import bwta.BWTA;
import bwta.BaseLocation;

/**
 * MineralPatchManager.java --- {@link ResourceManager} for mineral fields. The
 * {@link GatheringSource}s that this Class holds are all the accessible mineral
 * spots on the map.
 * 
 * @author P H - 03.09.2017
 *
 */
class MineralPatchManager<T extends GatheringSource> extends ResourceManager<GatheringSource> {

	public MineralPatchManager() {
		super();

		this.init();
	}

	// -------------------- Functions

	/**
	 * Function for initializing the gathering sources, namely the mineral spots
	 * on the map wrapped in {@link MineralPatch} instances.
	 */
	private void init() {
		// Instantiate all possible mineral spots on the map based on the BWTA
		// BaseLocations.
		for (BaseLocation baseLocation : BWTA.getBaseLocations()) {
			this.gatheringSources.add(new MineralPatch(baseLocation, this));
		}
	}

}
