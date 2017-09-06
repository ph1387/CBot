package unitControlModule.unitWrappers;

import bwapi.Position;
import bwapi.Unit;
import core.CBot;
import informationStorage.InformationStorage;
import workerManagerConstructionJobDistribution.ConstructionWorker;
import workerManagerConstructionJobDistribution.WorkerManagerConstructionJobDistribution;
import workerManagerResourceSpotAllocation.ResourceManagerEntry;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * PlayerUnitWorker.java --- Wrapper for a general worker Unit.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class PlayerUnitWorker extends PlayerUnitTypeMelee implements ResourceManagerEntry, ConstructionWorker {

	private WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation;
	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution;
	
	protected boolean assignedToSout = false;

	public PlayerUnitWorker(Unit unit, InformationStorage informationStorage, WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation, WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution) {
		super(unit, informationStorage);

		this.informationStorage.getWorkerConfig().incrementTotalWorkerCount();
		this.workerManagerResourceSpotAllocation = workerManagerResourceSpotAllocation;
		this.workerManagerConstructionJobDistribution = workerManagerConstructionJobDistribution;
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

	// ------------------------------ ResourceManagerEntry

	@Override
	public Position getPosition() {
		return this.unit.getPosition();
	}

	// ------------------------------ Getter / Setter

	public WorkerManagerResourceSpotAllocation getWorkerManagerResourceSpotAllocation() {
		return workerManagerResourceSpotAllocation;
	}

	public WorkerManagerConstructionJobDistribution getWorkerManagerConstructionJobDistribution() {
		return workerManagerConstructionJobDistribution;
	}
	
	public boolean isAssignedToSout() {
		return assignedToSout;
	}
}
