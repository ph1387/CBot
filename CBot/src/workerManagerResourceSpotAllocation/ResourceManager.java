package workerManagerResourceSpotAllocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import bwapi.Unit;

/**
 * ResourceManager.java --- Manager class for a List of
 * {@link GatheringSource}s. These include mainly the {@link MineralPatch}s and
 * {@link RefineryWrapper}s.
 * 
 * @author P H - 03.09.2017
 *
 */
class ResourceManager<T extends GatheringSource> implements IResourceManager {

	// A List of all GatheringSources that are being managed by this instance.
	protected List<T> gatheringSources = new ArrayList<T>();

	public ResourceManager() {

	}

	// -------------------- Functions

	@Override
	public boolean isSpaceAvailable() {
		return this.getAvailableSpace() > 0;
	}

	@Override
	public int getAvailableSpace() {
		int availableSpace = 0;

		// Count the free spaces of each registered gathering source.
		for (T gatheringSource : this.gatheringSources) {
			availableSpace += gatheringSource.getAvailableSpace();
		}

		return availableSpace;
	}

	@Override
	public boolean addToManager(ResourceManagerEntry entry) {
		return this.isSpaceAvailable() && this.assignWorker(entry);
	}

	/**
	 * Function for assigning a new worker to a stored {@link GatheringSource}.
	 * 
	 * @param worker
	 *            the worker that is going to be added to a
	 *            {@link GatheringSource}
	 * @return true if the worker was successfully added to a
	 *         {@link GatheringSource}, false if not.
	 */
	private boolean assignWorker(final ResourceManagerEntry worker) {
		boolean assignmentMissing = true;

		// Sort the possible gathering sources based on the distance towards the
		// worker. Then iterate through them until one is found, that has
		// available space and can take the worker. Due to the previous sorting
		// the closest one is chosen.
		this.gatheringSources.sort(new Comparator<T>() {

			@Override
			public int compare(T firstGatheringSource, T secondGatheringSource) {
				return Double.compare(firstGatheringSource.getDistance(worker.getPosition()),
						secondGatheringSource.getDistance(worker.getPosition()));
			}
		});

		// Assign the entry to a gathering source based on the sorted List.
		for (int i = 0; i < this.gatheringSources.size() && assignmentMissing; i++) {
			GatheringSource currentGatheringSource = this.gatheringSources.get(i);

			if (currentGatheringSource.isSpaceAvailable()) {
				currentGatheringSource.addWorker(worker);
				assignmentMissing = false;
			}
		}
		return !assignmentMissing;
	}

	@Override
	public boolean removeFromManager(ResourceManagerEntry entry) {
		return this.removeWorker(entry);
	}

	/**
	 * Function for removing a worker from the {@link GatheringSource} that he
	 * was previously assigned to.
	 * 
	 * @param worker
	 *            the worker that is going to be removed.
	 * @return true if the worker was successfully removed, false if not.
	 */
	private boolean removeWorker(ResourceManagerEntry worker) {
		boolean success = false;

		// Remove the worker from all assigned gathering sources. Break, when
		// the one is found that contains it.
		for (T gatheringSource : this.gatheringSources) {
			if (gatheringSource.containsWorker(worker) && gatheringSource.removeWorker(worker)) {
				success = true;

				break;
			}
		}
		return success;
	}

	@Override
	public Unit getGatheringSource(ResourceManagerEntry entry) {
		Unit foundGatheringSource = null;

		for (T gatheringSource : this.gatheringSources) {
			if (gatheringSource.containsWorker(entry)) {
				foundGatheringSource = gatheringSource.getGatheringSource(entry);

				break;
			}
		}

		return foundGatheringSource;
	}

	// ------------------------------ Getter / Setter

}
