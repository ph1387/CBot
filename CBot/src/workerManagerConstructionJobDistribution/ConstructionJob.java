package workerManagerConstructionJobDistribution;

import java.util.HashSet;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * ConstructionJob.java --- A Class containing the UnitType, the location and
 * the worker that is going to construct the building as well as a few other
 * information of the state of the "action".
 * 
 * @author P H - 05.09.2017
 *
 */
class ConstructionJob implements IConstrucionInformation {

	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution;

	// Information regarding the state of construction of the ConstructionJob:
	public enum ConstructionState {
		IDLE, WOKRING, FINISHED
	};

	private ConstructionState currentConstructionState = ConstructionState.IDLE;

	// Construction information:
	private UnitType unitType;
	private TilePosition tilePosition;
	private HashSet<TilePosition> contendedTilePositions;
	private ConstructionWorker assignedWorker;
	private Unit building;

	public ConstructionJob(WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution,
			UnitType unitType, ConstructionWorker assignedWorker) {
		this.workerManagerConstructionJobDistribution = workerManagerConstructionJobDistribution;
		this.unitType = unitType;
		this.assignedWorker = assignedWorker;

		this.init();
	}

	// -------------------- Functions

	/**
	 * Function for initializing all basic functionalities of the job.
	 */
	private void init() {
		this.reserveResources(this.unitType);

		// Get a location to construct the building and the required space.
		this.tilePosition = this.workerManagerConstructionJobDistribution.getBuildLocationFactory()
				.generateBuildLocation(this.unitType, this.assignedWorker.getUnit().getTilePosition(),
						this.assignedWorker);
		this.contendedTilePositions = this.workerManagerConstructionJobDistribution.getBuildLocationFactory()
				.generateNeededTilePositions(this.unitType, this.tilePosition);
	}

	/**
	 * Function for reserving resources based on a provided UnitType. Both types
	 * (Minerals as well as gas) are reserved based on the amount the assigned
	 * building's UnitType requires.
	 * 
	 * @param unitType
	 *            the UnitType whose mineral and gas prices are going to be
	 *            reserved.
	 */
	private void reserveResources(UnitType unitType) {
		this.workerManagerConstructionJobDistribution.getInformationStorage().getResourceReserver()
				.reserveMinerals(unitType.mineralPrice());
		this.workerManagerConstructionJobDistribution.getInformationStorage().getResourceReserver()
				.reserveGas(unitType.gasPrice());
	}

	/**
	 * Function for freeing resources based on a provided UnitType. Both types
	 * (Minerals as well as gas) are reserved based on the amount the assigned
	 * building's UnitType requires.
	 * 
	 * @param unitType
	 *            the UnitType whose mineral and gas prices are going to be
	 *            freed.
	 */
	private void freeResources(UnitType unitType) {
		this.workerManagerConstructionJobDistribution.getInformationStorage().getResourceReserver()
				.freeMinerals(unitType.mineralPrice());
		this.workerManagerConstructionJobDistribution.getInformationStorage().getResourceReserver()
				.freeGas(unitType.gasPrice());
	}

	/**
	 * Function for freeing the reserved resources of this
	 * {@link ConstructionJob} if it's construction state is still in the IDLE
	 * form. This is necessary since the worker assigned to this
	 * {@link ConstructionJob} might be destroyed before the construction of the
	 * building can be at least started which would in the resources normally
	 * being freed.
	 */
	void freeResourcesIfNecessary() {
		if (this.currentConstructionState == ConstructionState.IDLE) {
			this.freeResources(this.unitType);
		}
	}

	// ------------------------------ IConstrucionInformation

	@Override
	public void update() {
		// Perform all these checks in one single cycle if possible / needed.
		if (this.currentConstructionState == ConstructionState.IDLE) {
			// Try finding the construction job's assigned TilePosition and
			// UnitType in the HashSet of buildings currently being build.
			for (Unit unit : this.workerManagerConstructionJobDistribution.getCurrentlyConstructedBuildings()) {
				if (unit.getType() == this.unitType && unit.getTilePosition().equals(this.tilePosition)) {
					this.currentConstructionState = ConstructionState.WOKRING;
					this.building = unit;

					// Free resources if the construction of the building was
					// not yet started.
					this.freeResources(this.unitType);
					break;
				}
			}

			// Remove the (Hopefully) found building from the HashSet of the
			// currently being created ones.
			if (this.building != null) {
				this.workerManagerConstructionJobDistribution.getCurrentlyConstructedBuildings().remove(this.building);
			}
		}

		if (this.currentConstructionState == ConstructionState.WOKRING) {
			if (this.building != null && !this.building.isBeingConstructed()) {
				this.currentConstructionState = ConstructionState.FINISHED;
			}
		}
	}

	@Override
	public boolean isFinished() {
		return this.currentConstructionState == ConstructionState.FINISHED;
	}

	@Override
	public boolean constructionStarted() {
		return this.currentConstructionState == ConstructionState.WOKRING
				|| this.currentConstructionState == ConstructionState.FINISHED;
	}

	@Override
	public UnitType getUnitType() {
		return this.unitType;
	}

	@Override
	public TilePosition getTilePosition() {
		return this.tilePosition;
	}

	// ------------------------------ Getter / Setter

	public HashSet<TilePosition> getContendedTilePositions() {
		return this.contendedTilePositions;
	}

	public Unit getBuilding() {
		return building;
	}

}
