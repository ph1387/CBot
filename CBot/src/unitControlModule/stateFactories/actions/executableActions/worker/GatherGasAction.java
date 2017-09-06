package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

/**
 * GatherGasAction.java --- Action for gathering gas.
 * 
 * @author P H - 29.03.2017
 *
 */
public class GatherGasAction extends GatherAction {

	/**
	 * @param target
	 *            type: Null
	 */
	public GatherGasAction(Object target) {
		super(new Object());

		this.addEffect(new GoapState(0, "gatheringGas", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation = ((PlayerUnitWorker) goapUnit)
				.getWorkerManagerResourceSpotAllocation();
		boolean success = true;

		if (!workerManagerResourceSpotAllocation.isAssignedGathering((PlayerUnitWorker) goapUnit)) {
			success &= workerManagerResourceSpotAllocation.addGasGatherer((PlayerUnitWorker) goapUnit);
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
		if (!workerManagerResourceSpotAllocation.isAssignedGatheringGas((PlayerUnitWorker) goapUnit)) {
			success &= workerManagerResourceSpotAllocation.canAddGasGatherer();
		} else {
			success &= workerManagerResourceSpotAllocation.isAssignedGatheringGas((PlayerUnitWorker) goapUnit);
		}
		return success;
	}

}
