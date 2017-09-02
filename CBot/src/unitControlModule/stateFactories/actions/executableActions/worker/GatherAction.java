package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

/**
 * GatherAction.java --- Gather action for a PlayerUnitWorker. Both minerals and
 * gas can be gathered.
 * 
 * @author P H - 29.03.2017
 *
 */
public class GatherAction extends WorkerAction {

	protected Unit gatheringSourceTemp = null;
	protected Unit gatheringSource = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public GatherAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "allowGathering", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.actionChangeTrigger && this.gatheringSourceTemp != null) {
			this.gatheringSource = this.gatheringSourceTemp;
			success &= this.gatheringSource != null
					&& ((PlayerUnit) goapUnit).getUnit().gather(this.gatheringSource, true);

			// Add executing Unit to the mapped HashMap, so that other Units can
			// see that one place at this specific gathering source is reserved.
			((PlayerUnitWorker) goapUnit).getInformationStorage().getWorkerConfig()
					.getMappedAccessibleGatheringSources().get(this.gatheringSource)
					.add(((PlayerUnit) this.currentlyExecutingUnit).getUnit());
		} else if (this.actionChangeTrigger && this.gatheringSourceTemp == null) {
			success = false;
		}

		return this.gatheringSource != null && success;
	}

	@Override
	protected void resetSpecific() {
		// Make the blocked space at the mapped gathering source available again
		// if the Action stops.
		try {
			((PlayerUnitWorker) this.currentlyExecutingUnit).getInformationStorage().getWorkerConfig()
					.getMappedAccessibleGatheringSources().get(this.gatheringSource)
					.remove(((PlayerUnit) this.currentlyExecutingUnit).getUnit());
		} catch (Exception e) {
		}

		this.target = null;
		this.gatheringSource = null;
		this.gatheringSourceTemp = null;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		float returnValue = 0f;

		try {
			returnValue = ((PlayerUnit) goapUnit).getUnit().getDistance(this.gatheringSourceTemp);
		} catch (Exception e) {
			returnValue = Float.MAX_VALUE;
		}

		return returnValue;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return this.target == null || !((Unit) this.target).exists();
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		if (this.gatheringSource != null) {
			success = true;
		} else if (this.target != null) {
			// Check if the Unit is a contender for the source
			try {
				success = ((PlayerUnitWorker) goapUnit).getInformationStorage().getWorkerConfig()
						.getMappedSourceContenders().get((Unit) this.target)
						.contains(((PlayerUnit) goapUnit).getUnit());

				if (success) {
					this.gatheringSourceTemp = (Unit) this.target;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}

}
