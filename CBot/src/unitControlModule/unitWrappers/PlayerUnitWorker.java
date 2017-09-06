package unitControlModule.unitWrappers;

import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import core.CBot;
import informationStorage.InformationStorage;
import workerManagerConstructionJobDistribution.ConstructionWorker;
import workerManagerResourceSpotAllocation.ResourceManagerEntry;

/**
 * PlayerUnitWorker.java --- Wrapper for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class PlayerUnitWorker extends PlayerUnitTypeMelee implements ResourceManagerEntry, ConstructionWorker {

	protected boolean assignedToSout = false;

	// Building related stuff
	protected boolean constructingFlag = false;
	protected int personalReservedMinerals = 0;
	protected int personalReservedGas = 0;

	public enum ConstructionState {
		IDLE, AWAIT_CONFIRMATION, CONFIRMED
	}

	protected ConstructionState currentConstructionState = ConstructionState.IDLE;
	protected int constructionCounter = 0;
	protected UnitType assignedBuildingType;
	protected Unit assignedBuilding;

	// Resources:
	protected boolean resourcesResettable = false;

	public PlayerUnitWorker(Unit unit, InformationStorage informationStorage) {
		super(unit, informationStorage);

		this.informationStorage.getWorkerConfig().incrementTotalWorkerCount();
	}

	// -------------------- Functions

	/**
	 * Function needs to be overwritten due to the workers actions being very
	 * delicate processes. A simple reset is not possible since multiple actions
	 * require information that is going to be reseted normally. Therefore the
	 * order needs to be as follows:
	 * <ul>
	 * <li>Reset gets called -> information regarding various worker tasks are
	 * being reseted
	 * <li>Information must be restored
	 * <li>Information can be transferred to the actions at the end of the
	 * super.update function
	 * </ul>
	 * 
	 * @see javaGOAP.GoapUnit#resetActions()
	 */
	@Override
	public void resetActions() {
		if (!this.unit.isConstructing()) {
			super.resetActions();
		}
	}

	@Override
	public void update() {
		super.update();

		// Only perform the custom update when there are no enemies in range.
		// This is due to the fact that in this case either a retreat or attack
		// action is necessary and the gathering and construction actions can be
		// ignored.
		if (this.currentRangeState == PlayerUnit.ConfidenceRangeStates.NO_UNIT_IN_RANGE) {
			this.customUpdate();
		}
	}

	/**
	 * Should be called at least one time from the sub class if overwritten. It
	 * is updating all necessary information regarding various tasks the worker
	 * can execute as well as shared information between all workers.
	 */
	protected void customUpdate() {
		if (!this.informationStorage.getWorkerConfig().isWorkerOnceAssignedScouting()
				&& this.informationStorage.getWorkerConfig().getTotalWorkerCount() >= this.informationStorage
						.getWorkerConfig().getWorkerScoutingTrigger()
				&& !CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
				&& !this.unit.isGatheringGas()) {
			this.informationStorage.getWorkerConfig().setWorkerOnceAssignedScouting(true);
			this.assignedToSout = true;
			this.resetActions();
		} else if (!this.assignedToSout) {
			if (CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
					&& CBot.getInstance().getWorkerManagerConstructionJobDistribution().canConstruct()) {
				this.resetActions();
			}
		}
	}

	/**
	 * Function for freeing any reserved resources. This depends on a flag, that
	 * is going to be set as soon as the building Unit, that this worker is
	 * constructing, and the resulting building flag is being set. This ensures,
	 * that any resources are only going to be freed a single and not multiple
	 * times.
	 */
	protected void tryFreeingResources() {
		if (this.resourcesResettable) {
			this.resourcesResettable = false;

			// Reset any reserved resources
			this.freeResources();
		}
	}

	/**
	 * Function for actually freeing the reserved resources of the Unit.
	 */
	protected void freeResources() {
		this.informationStorage.getResourceReserver().freeMinerals(this.personalReservedMinerals);
		this.informationStorage.getResourceReserver().freeGas(this.personalReservedGas);
		this.personalReservedMinerals = 0;
		this.personalReservedGas = 0;
	}

	/**
	 * Function for updating the construction state of a worker Unit. This is
	 * needed for every worker that is / was currently constructing a building
	 * and therefore was assigned a building type and a specific amount of
	 * resources. These information need to be removed from the worker to
	 * prevent a clogging of resources and buildings. Also this function acts as
	 * a safety feature since it queues all construction jobs the Unit is / was
	 * not able to fulfill in a certain amount of time in the general
	 * construction queue again. This way all queued buildings actually get
	 * constructed.
	 */
	protected void updateConstructionState() {
		// Wait for the confirmation until either a limit is reached or the
		// confirmation was given.
		if (this.currentConstructionState == ConstructionState.AWAIT_CONFIRMATION) {
			if (this.constructionCounter < this.informationStorage.getWorkerConfig().getConstructionCounterMax()) {
				this.constructionCounter++;
			} else {
				this.constructionCounter = 0;
				this.currentConstructionState = ConstructionState.IDLE;

				this.resetAwaitedConstruction();
			}

			if (this.assignedBuildingType != null && this.informationStorage.getWorkerConfig().getMappedBuildActions()
					.getOrDefault(this.unit, null) == this.assignedBuildingType) {
				this.constructionCounter = 0;
				this.currentConstructionState = ConstructionState.CONFIRMED;
			}
		}
		// No "else if" since it will be executed in one cycle this way.
		if (this.currentConstructionState == ConstructionState.CONFIRMED) {
			// Remove failed / finished construction jobs. No iteration counter
			// here, since this functionality would be overridden by the
			// ActionUpdaterWorker.
			// -> Safety feature, so that no Unit holds a order and does not
			// execute it because as soon as a building location is occupied,
			// the building gets added back into the building queue.
			if (this.assignedBuildingType != null && this.informationStorage.getWorkerConfig().getMappedBuildActions()
					.getOrDefault(this.unit, null) == null) {
				this.currentConstructionState = ConstructionState.IDLE;

				this.resetAwaitedConstruction();
			}
		}
	}

	/**
	 * Function for updating all information regarding possible work the worker
	 * can perform.
	 */
	protected void updateCurrentActionInformation() {
		// Get a building from the building Queue and reset actions if possible.
		if (!this.unit.isGatheringGas() && !this.informationStorage.getWorkerConfig().getBuildingQueue().isEmpty()
				&& this.informationStorage.getResourceReserver()
						.canAffordConstruction(this.informationStorage.getWorkerConfig().getBuildingQueue().peek())
				&& this.currentConstructionState == ConstructionState.IDLE) {
			this.assignConstructionJob();
		}
	}

	/**
	 * Function for assigning a construction job to a worker Unit and changing
	 * his current state to AWAIT_CONFIRMATION.
	 */
	protected void assignConstructionJob() {
		// Reset first or the assigned building type will be removed!
		this.resetActions();
		this.assignedBuildingType = this.informationStorage.getWorkerConfig().getBuildingQueue().poll();

		// Reserve the resources for the construction.
		this.informationStorage.getResourceReserver().reserveMinerals(this.assignedBuildingType.mineralPrice());
		this.informationStorage.getResourceReserver().reserveGas(this.assignedBuildingType.gasPrice());
		this.personalReservedMinerals = this.assignedBuildingType.mineralPrice();
		this.personalReservedGas = this.assignedBuildingType.gasPrice();

		// Await the confirmation of the construction (by mapping the Unit
		// to a UnitType).
		this.currentConstructionState = ConstructionState.AWAIT_CONFIRMATION;
	}

	/**
	 * Function for resetting everything assigned for a construction of a
	 * building. If the construction flag was not set, the UnitType is queued
	 * again since the building was not constructed / did not start being
	 * constructed.
	 */
	protected void resetAwaitedConstruction() {
		// Flag is not set = construction has not started
		if (!this.constructingFlag) {
			this.informationStorage.getWorkerConfig().getBuildingQueue().add(this.assignedBuildingType);
			this.freeResources();

			// TODO: DEBUG INFO
			System.out.println("Queued again: " + this.unit + " " + this.assignedBuildingType);
		} else {
			this.constructingFlag = false;
			this.assignedBuilding = null;
		}

		this.assignedBuildingType = null;
	}

	// ------------------------------ ResourceManagerEntry

	@Override
	public Position getPosition() {
		return this.unit.getPosition();
	}

	// ------------------------------ Getter / Setter

	public UnitType getAssignedBuildingType() {
		return assignedBuildingType;
	}

	public void setConstructingFlag(Unit building) {
		this.constructingFlag = true;
		this.assignedBuilding = building;
		this.resourcesResettable = true;
	}

	public int getPersonalReservedMinerals() {
		return personalReservedMinerals;
	}

	public int getPersonalReservedGas() {
		return personalReservedGas;
	}

	public ConstructionState getCurrentConstructionState() {
		return currentConstructionState;
	}

	public Unit getAssignedBuilding() {
		return assignedBuilding;
	}

	public boolean isAssignedToSout() {
		return assignedToSout;
	}
}
