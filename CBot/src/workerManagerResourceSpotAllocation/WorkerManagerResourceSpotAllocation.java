package workerManagerResourceSpotAllocation;

import java.util.HashMap;

import bwapi.Unit;
import informationStorage.InformationStorage;

// TODO: UML ADD
/**
 * WorkerManagerResourceSpotAllocation.java --- The main Class managing the
 * different {@link ResourceManager}s, mainly the one for minerals and the one
 * for gas.
 * 
 * @author P H - 03.09.2017
 *
 */
public class WorkerManagerResourceSpotAllocation {

	private InformationStorage informationStorage;

	// The mapping of the different workers to the ResourceManagers. Using a
	// HashMap ensures a faster lookup when testing for a worker assignment.
	private HashMap<ResourceManagerEntry, IResourceManager> mappedResourceManagerEntries = new HashMap<>();
	private MineralPatchManager<MineralPatch> mineralPatchManager = new MineralPatchManager<>();
	private RefineryManager<RefineryWrapper> refineryManager = new RefineryManager<>();

	public WorkerManagerResourceSpotAllocation(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	/**
	 * Function for testing if a worker can be added towards the
	 * {@link MineralPatchManager}.
	 * 
	 * @return true if a worker can be added towards the
	 *         {@link MineralPatchManager}, false if not / no space is
	 *         available.
	 */
	public boolean canAddMineralGatherer() {
		return this.mineralPatchManager.isSpaceAvailable();
	}

	/**
	 * Function for adding a worker to the {@link MineralPatchManager}.
	 * 
	 * @param entry
	 *            the worker that is going to be added towards the
	 *            {@link MineralPatchManager}.
	 * @return true if the worker was successfully added towards the manager,
	 *         false if not or no space was available.
	 */
	public boolean addMineralGatherer(ResourceManagerEntry entry) {
		boolean success = false;

		if (this.canAddMineralGatherer()) {
			success = this.mineralPatchManager.addToManager(entry);

			if (success) {
				this.mappedResourceManagerEntries.put(entry, this.mineralPatchManager);
			}
		}
		return success;
	}

	/**
	 * Function for testing if a worker can be added towards the
	 * {@link RefineryManager}
	 * 
	 * @return true if a worker can be added towards the
	 *         {@link RefineryManager}, false if not / no space is available.
	 */
	public boolean canAddGasGatherer() {
		return this.refineryManager.isSpaceAvailable();
	}

	/**
	 * Function for adding a worker to the {@link RefineryManager}.
	 * 
	 * @param entry
	 *            the worker that is going to be added towards the
	 *            {@link RefineryManager}.
	 * @return true if the worker was successfully added towards the manager,
	 *         false if not or no space was available.
	 */
	public boolean addGasGatherer(ResourceManagerEntry entry) {
		boolean success = false;

		if (this.canAddGasGatherer()) {
			success = this.refineryManager.addToManager(entry);

			if (success) {
				this.mappedResourceManagerEntries.put(entry, this.refineryManager);
			}
		}
		return success;
	}

	/**
	 * Function for removing a worker from the {@link IResourceManager} that he
	 * is currently assigned to. This includes both the
	 * {@link MineralPatchManager} and the {@link RefineryManager}.
	 * 
	 * @param entry
	 *            the worker that is going to be removed from his assigned
	 *            {@link IResourceManager}.
	 * @return true if the worker was successfully removed from the
	 *         {@link IResourceManager}, false if he was either not assigned to
	 *         one or the removal of him failed.
	 */
	public boolean removeGatherer(ResourceManagerEntry entry) {
		boolean success = false;

		if (this.mappedResourceManagerEntries.containsKey(entry)) {
			success = this.mappedResourceManagerEntries.get(entry).removeFromManager(entry);
			success &= this.mappedResourceManagerEntries.remove(entry) != null;
		}
		return success;
	}

	/**
	 * Function for adding an additional refinery to the
	 * {@link RefineryManager}. Mainly used when new ones are being constructed.
	 * 
	 * @param unit
	 *            the refinery that the {@link RefineryManager} is now going to
	 *            manage.
	 */
	public void addRefinery(Unit unit) {
		this.refineryManager.addRefinery(unit);
	}

	/**
	 * Function for removing a refinery from the {@link RefineryManager}. Mainly
	 * used when existing ones are being destroyed.
	 * 
	 * @param unit
	 *            the refinery that is going to be removed from the
	 *            {@link RefineryManager}.
	 */
	public void removeRefinery(Unit unit) {
		this.refineryManager.removeRefinery(unit);
	}

	/**
	 * Function for testing if a {@link ResourceManagerEntry} is assigned to a
	 * {@link MineralPatchManager}.
	 * 
	 * @param entry
	 *            the worker that is going to be tested.
	 * @return true if the worker is assigned to a {@link MineralPatchManager},
	 *         false if not.
	 */
	public boolean isAssignedGatheringMinerals(ResourceManagerEntry entry) {
		IResourceManager resourceManager = this.mappedResourceManagerEntries.get(entry);

		return resourceManager != null && resourceManager instanceof MineralPatchManager<?>;
	}

	/**
	 * Function for testing if a {@link ResourceManagerEntry} is assigned to a
	 * {@link RefineryManager}.
	 * 
	 * @param entry
	 *            the worker that is going to be tested.
	 * @return true if the worker is assigned to a {@link RefineryManager},
	 *         false if not.
	 */
	public boolean isAssignedGatheringGas(ResourceManagerEntry entry) {
		IResourceManager resourceManager = this.mappedResourceManagerEntries.get(entry);

		return resourceManager != null && resourceManager instanceof RefineryManager<?>;
	}

	/**
	 * Function for testing if a {@link ResourceManagerEntry} is assigned to
	 * either a {@link MineralPatchManager} OR a {@link RefineryManager}.
	 * 
	 * @param entry
	 *            the worker that is going to be tested.
	 * @return true if the worker is assigned to either a
	 *         {@link MineralPatchManager} OR a {@link RefineryManager}.
	 */
	public boolean isAssignedGathering(ResourceManagerEntry entry) {
		return this.mappedResourceManagerEntries.containsKey(entry);
	}

	// ------------------------------ Getter / Setter

	/**
	 * Function for retrieving the gathering source of the
	 * {@link ResourceManagerEntry} that it was assigned to internally. This
	 * includes BOTH the mineral spots as well as existing refineries since both
	 * are Unit types.
	 * 
	 * @param entry
	 *            the worker whose gathering source is going to be retrieved.
	 * @return the gathering source the {@link ResourceManagerEntry} was
	 *         assigned to internally.
	 */
	public Unit getGatheringSource(ResourceManagerEntry entry) {
		Unit gatheringSource = null;

		if (this.isAssignedGathering(entry)) {
			gatheringSource = this.mappedResourceManagerEntries.get(entry).getGatheringSource(entry);
		}

		return gatheringSource;
	}

	public InformationStorage getInformationStorage() {
		return informationStorage;
	}
}
