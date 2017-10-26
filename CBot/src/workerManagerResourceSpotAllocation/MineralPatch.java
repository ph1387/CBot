package workerManagerResourceSpotAllocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwta.BaseLocation;

/**
 * MineralPatch.java --- Wrapper Class for mineral spots that are going to be
 * managed by {@link MineralPatchManager} instances as {@link GatheringSource}s.
 * Each mineral patch is located at a specific BaseLocation and has multiple
 * gathering spots.
 * 
 * @author P H - 03.09.2017
 *
 */
class MineralPatch implements GatheringSource {

	private IResourceManager mineralPatchManager;

	// The BaseLocation the mineral spots are located at.
	private BaseLocation baseLocation;
	private int maxWorkersPerSpot = 2;
	private int freeWorkerSpots;
	private HashSet<ResourceManagerEntry> assignedWorkers = new HashSet<>();
	private HashMap<ResourceManagerEntry, Unit> mappedWorkers = new HashMap<>();
	private HashMap<Unit, HashSet<ResourceManagerEntry>> mappedSpots = new HashMap<>();

	public MineralPatch(BaseLocation baseLocation, IResourceManager mineralPatchManager) {
		this.mineralPatchManager = mineralPatchManager;
		this.baseLocation = baseLocation;

		// Instantiate the HashSets of the mapped spots.
		for (Unit unit : this.baseLocation.getMinerals()) {
			this.mappedSpots.put(unit, new HashSet<ResourceManagerEntry>());
		}
	}

	// -------------------- Functions

	@Override
	public boolean addWorker(ResourceManagerEntry worker) {
		return this.assignedWorkers.add(worker) && this.mapWorker(worker);
	}

	/**
	 * Function for mapping a worker to a specific single gathering spot since
	 * there are multiple possible ones at a single BaseLocation.
	 * 
	 * @param worker
	 *            the worker that is going to be mapped to a mineral spot.
	 * @return true if the worker was successfully mapped to a mineral spot,
	 *         false if not.
	 */
	private boolean mapWorker(ResourceManagerEntry worker) {
		final HashMap<Unit, HashSet<ResourceManagerEntry>> finalMappedSpots = new HashMap<>(this.mappedSpots);
		List<Unit> mineralSpots = new ArrayList<>(this.mappedSpots.keySet());
		boolean assignmentMissing = true;

		// Sort the spots based on the free spaces they provide!
		mineralSpots.sort(new Comparator<Unit>() {

			@Override
			public int compare(Unit firstUnit, Unit secondUnit) {
				return Integer.compare(finalMappedSpots.get(firstUnit).size(), finalMappedSpots.get(secondUnit).size());
			}
		});

		// Add the worker to the spot that provides the most free space.
		for (int i = 0; i < mineralSpots.size() && assignmentMissing; i++) {
			if (this.mappedSpots.get(mineralSpots.get(i)).size() < this.maxWorkersPerSpot) {
				assignmentMissing = !this.mappedSpots.get(mineralSpots.get(i)).add(worker);

				// The Unit was added towards the mineral spot's HashSet.
				// Therefore the Unit must be mapped towards the mineral spot.
				if (!assignmentMissing) {
					this.mappedWorkers.put(worker, mineralSpots.get(i));
				}
			}
		}

		return !assignmentMissing;
	}

	@Override
	public boolean removeWorker(ResourceManagerEntry worker) {
		Unit gatheringSpot = this.mappedWorkers.get(worker);
		boolean success = false;

		if (this.containsWorker(worker)) {
			// Remove the reference to all worker related information.
			success &= this.assignedWorkers.remove(worker);
			success &= this.mappedSpots.get(gatheringSpot).remove(worker);
			success &= this.mappedWorkers.remove(worker) != null;
		}
		return success;
	}

	@Override
	public boolean containsWorker(ResourceManagerEntry worker) {
		return this.assignedWorkers.contains(worker);
	}

	@Override
	public boolean isSpaceAvailable() {
		return this.getAvailableSpace() > 0;
	}

	@Override
	public int getAvailableSpace() {
		this.updateFreeWorkerSpots();

		return this.freeWorkerSpots;
	}

	/**
	 * Function for updating the number of free worker spots of this particular
	 * {@link MineralPatch}.
	 */
	private void updateFreeWorkerSpots() {
		int totalWorkerSpots = 0;
		int assignedWorkerSpots = 0;

		// Iterate through all mineral spots and only count the ones that
		// contain minerals!
		for (Unit unit : this.baseLocation.getMinerals()) {
			if (unit.exists() && unit.getResources() > 0) {
				totalWorkerSpots += this.maxWorkersPerSpot;
			}

			assignedWorkerSpots += this.mappedSpots.get(unit).size();
		}

		this.freeWorkerSpots = totalWorkerSpots - assignedWorkerSpots;
	}

	@Override
	public double getDistance(Position position) {
		return this.baseLocation.getDistance(position);
	}

	@Override
	public Unit getGatheringSource(ResourceManagerEntry worker) {
		Unit mappedGatheringSource = this.mappedWorkers.get(worker);
		Unit gatheringSource = null;

		// The gathering source has to be existent in the game in order for the
		// Unit to gather there.
		if (mappedGatheringSource != null && mappedGatheringSource.exists()) {
			// Return the gathering source if either resources can be mined or
			// the maximum number of Units working there is not exceeded.
			if ((mappedGatheringSource.getResources() > 0)
					|| (this.mappedSpots.get(mappedGatheringSource).size() <= this.maxWorkersPerSpot)) {
				gatheringSource = mappedGatheringSource;
			}
		}

		return gatheringSource;
	}

	// ------------------------------ Getter / Setter

	public BaseLocation getBaseLocation() {
		return baseLocation;
	}

}
