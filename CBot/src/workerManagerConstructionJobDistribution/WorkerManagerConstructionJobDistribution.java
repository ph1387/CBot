package workerManagerConstructionJobDistribution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import bwapi.Race;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
import informationStorage.InformationStorage;

/**
 * WorkerManagerConstructionJobDistribution.java --- The Class managing the
 * Player's construction of buildings. Workers can add themselves to an instance
 * of this Class and receive a {@link ConstructionJob} which they then can
 * pursue. </br>
 * Possible buildings that are going to be constructed are added towards the
 * building Queue which acts as a Collection for them. Units / buildings of
 * constructions that have already begun must be added towards the HashSet of
 * currently constructed buildings
 * ({@link #addToCurrentlyConstructedBuildings(Unit)}) since the
 * {@link ConstructionJob}s are changing their state upon these information.
 * 
 * @author P H - 05.09.2017
 *
 */
public class WorkerManagerConstructionJobDistribution {

	private InformationStorage informationStorage;
	private BuildLocationFactory buildLocationFactory;

	// All necessary Collections regarding the construction of buildings:
	private HashSet<Unit> currentlyConstructedBuildings = new HashSet<>();
	private Queue<UnitType> buildingQueue = new LinkedList<>();
	private HashMap<ConstructionWorker, ConstructionJob> assignedWorkers = new HashMap<>();

	public WorkerManagerConstructionJobDistribution(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
		this.buildLocationFactory = new BuildLocationFactory(informationStorage);
	}

	// -------------------- Functions

	/**
	 * Function for adding a new UnitType to the building Queue. This UnitType
	 * is going to be wrapped into a {@link ConstructionJob} and later build by
	 * a worker.
	 * 
	 * @param unitType
	 *            the type of building that is going to be constructed.
	 */
	public void addToBuildingQueue(UnitType unitType) {
		if (unitType.isBuilding()) {
			this.buildingQueue.add(unitType);
		}
	}

	/**
	 * Function for testing if the next element in the building Queue can be
	 * afforded and constructed. If it does not, the element is moved to the
	 * back of the Queue for later testing.
	 * 
	 * @return true if the next element in the building Queue can be afforded
	 *         and constructed.
	 */
	public boolean canConstruct() {
		UnitType nextBuildingQueueElement = this.buildingQueue.peek();
		boolean canConstruct = false;

		if (nextBuildingQueueElement != null) {
			canConstruct = this.informationStorage.getResourceReserver()
					.canAffordConstruction(nextBuildingQueueElement);

			// Cycle thorugh the elements in the building Queue.
			if (!canConstruct) {
				this.buildingQueue.add(this.buildingQueue.poll());
			}
		}

		return canConstruct;
	}

	/**
	 * Function for adding a new worker to the manager. Only works, if the next
	 * element in the building Queue can actually be constructed and wrapped
	 * into a {@link ConstructionJob}.
	 * 
	 * @param worker
	 *            the {@link ConstructionWorker} that is going to be
	 *            constructing the next building in the building Queue and
	 *            assigned a {@link ConstructionJob}.
	 * @return true if the next element in the building Queue can be constructed
	 *         as well as afforded and the provided worker was assigned to a
	 *         {@link ConstructionJob} containing this building. False if either
	 *         the next element can not be build or the worker was not
	 *         successfully assigned.
	 */
	public boolean addWorker(ConstructionWorker worker) {
		boolean success = false;

		if (this.canConstruct()) {
			ConstructionJob newConstructionJob = new ConstructionJob(this, this.buildingQueue.poll(), worker);

			// Reserve the resources and the needed TilePositions.
			this.assignedWorkers.put(worker, newConstructionJob);
			this.informationStorage.getMapInfo().getTilePositionContenders()
					.addAll(newConstructionJob.getContendedTilePositions());

			success = true;
		}
		return success;
	}

	/**
	 * Function for testing if a {@link ConstructionWorker} is assigned to a
	 * {@link ConstructionJob} or not.
	 * 
	 * @param worker
	 *            the worker that is going to be tested for.
	 * @return true if the provided worker is assigned to a
	 *         {@link ConstructionJob}, false if not.
	 */
	public boolean isAssignedConstructing(ConstructionWorker worker) {
		return this.assignedWorkers.containsKey(worker);
	}

	/**
	 * Function for retrieving the {@link IConstrucionInformation} of a provided
	 * {@link ConstructionWorker}. This represents the {@link ConstructionJob}
	 * that the specific worker is assigned to.
	 * 
	 * @param worker
	 *            the worker whose {@link IConstrucionInformation} are
	 *            retrieved.
	 * @return the {@link IConstrucionInformation} / {@link ConstructionJob} of
	 *         the provided {@link ConstructionWorker} or null if the worker is
	 *         not assigned to one.
	 */
	public IConstrucionInformation getConstructionInformation(ConstructionWorker worker) {
		return this.assignedWorkers.get(worker);
	}

	/**
	 * Function for passing an update cycle to the assigned
	 * {@link ConstructionJob} of the provided {@link ConstructionWorker}.
	 * Mainly used for updating the state of the job.
	 * 
	 * @param worker
	 *            the {@link ConstructionWorker} whose {@link ConstructionJob}
	 *            is going to be updated.
	 */
	public void updateConstructionStatus(ConstructionWorker worker) {
		if (this.isAssignedConstructing(worker)) {
			this.assignedWorkers.get(worker).update();
		}
	}

	/**
	 * Function for adding a building / Unit to the ones that are currently
	 * being constructed / build. This is necessary for the
	 * {@link ConstructionJob}s to know since the resources they reserved must
	 * eventually be freed. Therefore knowing which Units / buildings are
	 * currently being constructed is necessary.
	 * 
	 * @param building
	 *            the building that is currently being constructed.
	 */
	public void addToCurrentlyConstructedBuildings(Unit building) {
		this.currentlyConstructedBuildings.add(building);
	}

	/**
	 * Function for testing if the provided {@link ConstructionWorker} finished
	 * his assigned {@link ConstructionJob}. For this function to work the
	 * {@link #updateConstructionStatus(ConstructionWorker)} as well as the
	 * {@link #addToCurrentlyConstructedBuildings(Unit)} functions must be
	 * called since this function utilizes the internal construction state of
	 * the {@link ConstructionJob} which in return requires the assignment of a
	 * constructed building.
	 * 
	 * @param worker
	 *            the worker whose {@link ConstructionJob} is going to be
	 *            tested.
	 * @return true if the {@link ConstructionJob} of the provided
	 *         {@link ConstructionWorker} is finished, false if not.
	 */
	public boolean isFinishedConstructing(ConstructionWorker worker) {
		boolean finished = false;

		if (this.isAssignedConstructing(worker)) {
			finished = this.assignedWorkers.get(worker).isFinished();
		}
		return finished;
	}

	/**
	 * Function for removing a worker from any saved {@link ConstructionJob} as
	 * well as the {@link ConstructionJob} itself from the manager.
	 * 
	 * @param worker
	 *            the worker that is going to be removed.
	 * @return true if the worker was successfully removed, false if not.
	 */
	public boolean removeWorker(ConstructionWorker worker) {
		boolean success = false;

		// Worker must be assigned a ConstructionJob.
		if (this.isAssignedConstructing(worker)) {
			ConstructionJob constructionJob = this.assignedWorkers.get(worker);

			// Free contended TilePositions of the ConstructionJob.
			this.informationStorage.getMapInfo().getTilePositionContenders()
					.removeAll(constructionJob.getContendedTilePositions());

			// Free the reserved resources of the ConstructionJob if necessary.
			constructionJob.freeResourcesIfNecessary();

			// Terran uses SCV's to construct buildings. If one is removed from
			// the construction, cancel the building. Do NOT do this with other
			// Races due to them consuming / morphing the Unit into a building.
			if (Core.getInstance().getPlayer().getRace() == Race.Terran && !constructionJob.isFinished()
					&& constructionJob.getBuilding() != null && constructionJob.getBuilding().canCancelConstruction()) {
				constructionJob.getBuilding().cancelConstruction();
			}

			// Remove the reference to the worker as well as the construction
			// job.
			success = this.assignedWorkers.remove(worker) != null;
		}

		return success;
	}

	/**
	 * Function for removing a building from the currently saved
	 * {@link ConstructionJob}s.
	 * 
	 * @param building
	 *            the building that is going to be removed.
	 * @return true if the building was successfully removed, false if not.
	 */
	public boolean removeBuilding(Unit building) {
		ConstructionWorker matchingWorker = null;
		boolean success = false;

		// The building must be one of the ones currently being constructed.
		for (ConstructionWorker worker : this.assignedWorkers.keySet()) {
			if (this.assignedWorkers.get(worker).getBuilding() == building) {
				matchingWorker = worker;

				break;
			}
		}

		if (matchingWorker != null) {
			// Use the remove worker function due to it checking all other
			// cases.
			success = this.removeWorker(matchingWorker);

			// Cancel construction if necessary and possible.
			if (building.isBeingConstructed() && building.getHitPoints() > 0 && building.canCancelConstruction()) {
				building.cancelConstruction();
			}
		}

		return success;
	}

	BuildLocationFactory getBuildLocationFactory() {
		return this.buildLocationFactory;
	}

	// ------------------------------ Getter / Setter

	public InformationStorage getInformationStorage() {
		return informationStorage;
	}

	public Queue<UnitType> getBuildingQueue() {
		return buildingQueue;
	}

	public HashSet<Unit> getCurrentlyConstructedBuildings() {
		return currentlyConstructedBuildings;
	}

}
