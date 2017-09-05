package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import core.CBot;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * GatherAction.java --- Gather action for a PlayerUnitWorker. Both minerals and
 * gas can be gathered.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class GatherAction extends WorkerAction {

	// Different flags and storage references that are used across the different
	// actions:
	protected boolean assigningMissing = true;
	protected Unit prevGatheringSource = null;
	protected WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation = CBot.getInstance()
			.getWorkerManagerResourceSpotAllocation();

	/**
	 * @param target
	 *            type: Null
	 */
	public GatherAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "allowGathering", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected void resetSpecific() {
		// Remove any assigned instances of the Unit.
		if (this.currentlyExecutingUnit != null && this.workerManagerResourceSpotAllocation
				.isAssignedGathering((PlayerUnitWorker) this.currentlyExecutingUnit)) {
			this.workerManagerResourceSpotAllocation.removeGatherer((PlayerUnitWorker) this.currentlyExecutingUnit);
		}

		this.assigningMissing = true;

		this.target = new Object();
		this.prevGatheringSource = null;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 2;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0.f;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return !this.assigningMissing
				&& this.workerManagerResourceSpotAllocation.getGatheringSource((PlayerUnitWorker) goapUnit) == null;
	}

}
