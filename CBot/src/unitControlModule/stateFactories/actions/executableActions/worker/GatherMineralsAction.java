package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * GatherMineralsAction.java --- Action for gathering minerals.
 * 
 * @author P H - 29.03.2017
 *
 */
public class GatherMineralsAction extends GatherAction {

	/**
	 * @param target
	 *            type: Null
	 */
	public GatherMineralsAction(Object target) {
		super(new Object());

		this.addEffect(new GoapState(0, "gatheringMinerals", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation = ((PlayerUnitWorker) goapUnit)
				.getWorkerManagerResourceSpotAllocation();
		boolean success = true;

		if (!workerManagerResourceSpotAllocation.isAssignedGathering((PlayerUnitWorker) goapUnit)) {
			success &= workerManagerResourceSpotAllocation.addMineralGatherer((PlayerUnitWorker) goapUnit);
		}

		Unit currentGatheringSource = workerManagerResourceSpotAllocation
				.getGatheringSource((PlayerUnitWorker) goapUnit);

		// If the gathering source changed, execute a new gather command.
		if (currentGatheringSource != this.prevGatheringSource) {
			((PlayerUnitWorker) goapUnit).getUnit().gather(currentGatheringSource);

			this.prevGatheringSource = currentGatheringSource;
		}

		return success;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation = ((PlayerUnitWorker) goapUnit)
				.getWorkerManagerResourceSpotAllocation();
		boolean success = true;

		// The first time only check if the Unit can be assigned. The second
		// time check if the Unit is still assigned.
		if (!workerManagerResourceSpotAllocation.isAssignedGatheringMinerals((PlayerUnitWorker) goapUnit)) {
			success &= workerManagerResourceSpotAllocation.canAddMineralGatherer();
		} else {
			success &= workerManagerResourceSpotAllocation.isAssignedGatheringMinerals((PlayerUnitWorker) goapUnit);
		}
		return success;
	}

}
