package unitControlModule.unitWrappers;

import bwapi.Position;
import bwapi.Unit;
import core.CBot;
import core.Core;
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

	// Enum needed since the worker would otherwise constantly reset his actions
	// ...
	private enum ConstructionState {
		IDLE, APPLIED
	};

	// Information regarding the construction of different buildings:
	private ConstructionState currentConstrcutionState = ConstructionState.IDLE;
	private int constructionStateApplianceTimeStamp = 0;
	private int constructionStateApplianceMaxFrameDiff = 200;

	private WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation;
	private WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution;

	protected boolean assignedToSout = false;

	public PlayerUnitWorker(Unit unit, InformationStorage informationStorage,
			WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation,
			WorkerManagerConstructionJobDistribution workerManagerConstructionJobDistribution) {
		super(unit, informationStorage);

		this.informationStorage.getWorkerConfig().incrementTotalWorkerCount();
		this.workerManagerResourceSpotAllocation = workerManagerResourceSpotAllocation;
		this.workerManagerConstructionJobDistribution = workerManagerConstructionJobDistribution;
	}

	// -------------------- Functions

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
		// Reset the construction state of the worker if possible / when a
		// specific amount of time passed.
		if (!CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
				&& this.currentConstrcutionState == ConstructionState.APPLIED
				&& Core.getInstance().getGame().getFrameCount()
						- this.constructionStateApplianceTimeStamp >= this.constructionStateApplianceMaxFrameDiff) {
			this.currentConstrcutionState = ConstructionState.IDLE;
		}

		// Act differently if the worker is / must be assigned scouting.
		if (!this.informationStorage.getWorkerConfig().isWorkerOnceAssignedScouting()
				&& this.informationStorage.getWorkerConfig().getTotalWorkerCount() >= this.informationStorage
						.getWorkerConfig().getWorkerScoutingTrigger()
				&& !CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
				&& !this.unit.isGatheringGas() && !this.unit.isConstructing()
				&& this.currentConstrcutionState == ConstructionState.IDLE) {
			this.informationStorage.getWorkerConfig().setWorkerOnceAssignedScouting(true);
			this.assignedToSout = true;

			this.resetActions();
		} else if (!this.assignedToSout) {
			if (!CBot.getInstance().getWorkerManagerConstructionJobDistribution().isAssignedConstructing(this)
					&& CBot.getInstance().getWorkerManagerConstructionJobDistribution().canConstruct()
					&& this.currentConstrcutionState == ConstructionState.IDLE) {
				this.currentConstrcutionState = ConstructionState.APPLIED;
				this.constructionStateApplianceTimeStamp = Core.getInstance().getGame().getFrameCount();

				this.resetActions();
			}
		}
	}

	// TODO: UML ADD
	@Override
	public void destroy() {
		super.destroy();

		this.informationStorage.getWorkerConfig().decrementTotalWorkerCount();
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
