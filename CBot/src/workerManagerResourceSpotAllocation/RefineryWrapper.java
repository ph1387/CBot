package workerManagerResourceSpotAllocation;

import java.util.HashSet;

import bwapi.Position;
import bwapi.Unit;

/**
 * RefineryWrapper.java --- Wrapper Class for refineries that are going to be
 * managed by {@link RefineryManager} instances as {@link GatheringSource}s.
 * 
 * @author P H - 03.09.2017
 *
 */
class RefineryWrapper implements GatheringSource {

	private IResourceManager refineryManager;

	// The Unit instance that represents the refinery that any assigned Units
	// are going to gather at.
	private Unit refinery;
	private int maxWorkersPerRefinery = 3;
	// TODO: UML REMOVE
//	private int freeWorkerSpots = this.maxWorkersPerRefinery;
	private HashSet<ResourceManagerEntry> assignedWorkers = new HashSet<>();

	public RefineryWrapper(Unit refinery, IResourceManager refineryManager) {
		this.refineryManager = refineryManager;
		this.refinery = refinery;
	}

	// -------------------- Functions

	@Override
	public boolean addWorker(ResourceManagerEntry worker) {
		boolean success = false;

		if (this.isSpaceAvailable()) {
			success = this.assignedWorkers.add(worker);
		}
		return success;
	}

	@Override
	public boolean removeWorker(ResourceManagerEntry worker) {
		boolean success = false;

		if (this.containsWorker(worker)) {
			success = this.assignedWorkers.remove(worker);
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
		return this.maxWorkersPerRefinery - this.assignedWorkers.size();
	}

	@Override
	public double getDistance(Position position) {
		return this.refinery.getDistance(position);
	}

	@Override
	public Unit getGatheringSource(ResourceManagerEntry worker) {
		Unit refinery = null;

		if (this.refinery.exists()) {
			refinery = this.refinery;
		}
		return refinery;
	}

}
