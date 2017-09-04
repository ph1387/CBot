package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import core.CBot;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
import workerManagerResourceSpotAllocation.WorkerManagerResourceSpotAllocation;

// TODO: UML ABSTRACT
/**
 * GatherAction.java --- Gather action for a PlayerUnitWorker. Both minerals and
 * gas can be gathered.
 * 
 * @author P H - 29.03.2017
 *
 */
public abstract class GatherAction extends WorkerAction {

	// TODO: UML REMOVE
//	protected Unit gatheringSourceTemp = null;
//	protected Unit gatheringSource = null;

	// TODO: UML ADD
	protected boolean assigningMissing = true;
	// TODO: UML ADD
	protected Unit prevGatheringSource = null;
	// TODO: UML ADD
	protected WorkerManagerResourceSpotAllocation workerManagerResourceSpotAllocation = CBot.getInstance().getWorkerManagerResourceSpotAllocation();
	
	// TODO: UML TARGET CHANGE
	/**
	 * @param target type: Null
	 */
	public GatherAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "allowGathering", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	// TODO: UML REMOVE
//	@Override
//	protected boolean performSpecificAction(IGoapUnit goapUnit) {
//		boolean success = true;
//
//		if (this.actionChangeTrigger && this.gatheringSourceTemp != null) {
//			this.gatheringSource = this.gatheringSourceTemp;
//			success &= this.gatheringSource != null
//					&& ((PlayerUnit) goapUnit).getUnit().gather(this.gatheringSource, true);
//
//			// Add executing Unit to the mapped HashMap, so that other Units can
//			// see that one place at this specific gathering source is reserved.
//			((PlayerUnitWorker) goapUnit).getInformationStorage().getWorkerConfig()
//					.getMappedAccessibleGatheringSources().get(this.gatheringSource)
//					.add(((PlayerUnit) this.currentlyExecutingUnit).getUnit());
//		} else if (this.actionChangeTrigger && this.gatheringSourceTemp == null) {
//			success = false;
//		}
//
//		return this.gatheringSource != null && success;
//	}

	@Override
	protected void resetSpecific() {
		// Remove any assigned instances of the Unit.
		if(this.currentlyExecutingUnit != null && this.workerManagerResourceSpotAllocation.isAssignedGathering((PlayerUnitWorker) this.currentlyExecutingUnit)) {
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
		return !this.assigningMissing && this.workerManagerResourceSpotAllocation.getGatheringSource((PlayerUnitWorker) goapUnit) == null;
	}

	// TODO: UML REMOVE
//	@Override
//	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
//		boolean success = false;
//
//		if (this.gatheringSource != null) {
//			success = true;
//		} else if (this.target != null) {
//			// Check if the Unit is a contender for the source
//			try {
//				success = ((PlayerUnitWorker) goapUnit).getInformationStorage().getWorkerConfig()
//						.getMappedSourceContenders().get((Unit) this.target)
//						.contains(((PlayerUnit) goapUnit).getUnit());
//
//				if (success) {
//					this.gatheringSourceTemp = (Unit) this.target;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return success;
//	}

}
