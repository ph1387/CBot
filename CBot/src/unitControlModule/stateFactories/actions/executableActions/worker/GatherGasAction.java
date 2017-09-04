package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

/**
 * GatherGasAction.java --- Action for gathering gas.
 * 
 * @author P H - 29.03.2017
 *
 */
public class GatherGasAction extends GatherAction {

	// TODO: UML TARGET CHANGE
	/**
	 * @param target
	 *            type: Null
	 */
	public GatherGasAction(Object target) {
		super(new Object());

		this.addEffect(new GoapState(0, "gatheringGas", true));
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (!this.workerManagerResourceSpotAllocation.isAssignedGathering((PlayerUnitWorker) goapUnit)) {
			success &= this.workerManagerResourceSpotAllocation.addGasGatherer((PlayerUnitWorker) goapUnit);
		}

		Unit currentGatheringSource = this.workerManagerResourceSpotAllocation.getGatheringSource((PlayerUnitWorker) goapUnit);

		// If the gathering source changed, execute a new gather command.
		if (currentGatheringSource != this.prevGatheringSource) {
			((PlayerUnitWorker) goapUnit).getUnit().gather(currentGatheringSource);

			this.prevGatheringSource = currentGatheringSource;
		}

		return success;
	}

	// TODO: UML ADD
	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = true;

		// The first time only check if the Unit can be assigned. The second
		// time check if the Unit is still assigned.
		if (!this.workerManagerResourceSpotAllocation.isAssignedGatheringGas((PlayerUnitWorker) goapUnit)) {
			success &= this.workerManagerResourceSpotAllocation.canAddGasGatherer();
		} else {
			success &= this.workerManagerResourceSpotAllocation.isAssignedGatheringGas((PlayerUnitWorker) goapUnit);
		}
		return success;
	}

}
