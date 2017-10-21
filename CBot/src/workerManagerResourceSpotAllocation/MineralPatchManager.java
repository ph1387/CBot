package workerManagerResourceSpotAllocation;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import core.Core;
import informationStorage.InformationStorage;

/**
 * MineralPatchManager.java --- {@link ResourceManager} for mineral fields. The
 * {@link GatheringSource}s that this Class holds are all the accessible mineral
 * spots on the map.
 * 
 * @author P H - 03.09.2017
 *
 */
class MineralPatchManager<T extends GatheringSource> extends ResourceManager<GatheringSource> {

	/**
	 * Sorter.java --- Class used for sorting the gathering sources.
	 * 
	 * @author P H - 21.10.2017
	 *
	 */
	private class Sorter implements SortInterface<T> {

		// The value that gets added to the distance of a mineral patch if no
		// command center was build near it. This ensures that patches with
		// command centers near them are being preferred.
		private int notCommandCenterExtraValue = 1000000;
		// TilePositions have to be used since the BWTA BaseLocations are NOT
		// consistent! The ones returned by the MineralPatches do NOT match the
		// ones returned by the BWTA function!
		private HashMap<TilePosition, Integer> centerBaseLocations = new HashMap<>();

		/**
		 * @param informationStorage
		 *            the {@link InformationStorage} instance that holds all
		 *            information regarding the current state of the game.
		 */
		public Sorter(InformationStorage informationStorage) {
			HashSet<Unit> centers = informationStorage.getCurrentGameInformation().getCurrentUnits()
					.getOrDefault(Core.getInstance().getPlayer().getRace().getCenter(), new HashSet<Unit>());

			for (Unit unit : centers) {
				this.centerBaseLocations.put(BWTA.getNearestBaseLocation(unit.getPosition()).getTilePosition(), 0);
			}
		}

		@Override
		public int compare(ResourceManagerEntry worker, T firstGatheringSource, T secondGatheringSource) {
			// If no center is near the BaseLocation, add an extra value to the
			// distance since ones with a center should be preferred.
			TilePosition firstBaselocationTilePosition = ((MineralPatch) firstGatheringSource).getBaseLocation()
					.getTilePosition();
			TilePosition secondBaselocationTilePosition = ((MineralPatch) secondGatheringSource).getBaseLocation()
					.getTilePosition();
			double firstValue = firstGatheringSource.getDistance(worker.getPosition()) + this.centerBaseLocations
					.getOrDefault(firstBaselocationTilePosition, this.notCommandCenterExtraValue);
			double secondValue = secondGatheringSource.getDistance(worker.getPosition()) + this.centerBaseLocations
					.getOrDefault(secondBaselocationTilePosition, this.notCommandCenterExtraValue);

			return Double.compare(firstValue, secondValue);
		}

	}

	// TODO: UML PARAMS
	public MineralPatchManager(WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation) {
		super(workerManagerResourceSpotAllocation);

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

	// TODO: UML ADD
	@Override
	@SuppressWarnings("unchecked")
	protected SortInterface<GatheringSource> defineSorter() {
		return (SortInterface<GatheringSource>) new Sorter(
				this.workerManagerResourceSpotAllocation.getInformationStorage());
	}

}
