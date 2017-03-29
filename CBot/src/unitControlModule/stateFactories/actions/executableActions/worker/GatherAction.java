package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

// TODO: UML
/**
 * GatherAction.java --- Gather action for a PlayerUnitWorker. Both minerals and
 * gas can be gathered.
 * 
 * @author P H - 29.03.2017
 *
 */
public class GatherAction extends BaseAction {

	protected Unit gatheringSourceTemp = null;
	protected Unit gatheringSource = null;

	/**
	 * @param target
	 *            type: Unit
	 */
	public GatherAction(Object target) {
		super(target);
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
			// see that one place at this specific mineral batch is reserved.
			PlayerUnitWorker.mappedAccesbileGatheringSources.get(this.gatheringSource)
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
		PlayerUnitWorker.mappedAccesbileGatheringSources.get(this.gatheringSource).remove(this.currentlyExecutingUnit);

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
		boolean success = true;

		if (this.gatheringSource != null) {
			success = !((PlayerUnit) goapUnit).getUnit().canGather(this.gatheringSource);
		} else if (this.gatheringSourceTemp != null) {
			success = !((PlayerUnit) goapUnit).getUnit().canGather(this.gatheringSourceTemp);
		}

		return success;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = false;

		if (this.gatheringSource != null) {
			success = ((PlayerUnit) goapUnit).getUnit().canGather(this.gatheringSource);
		} else if (this.target != null) {
			this.gatheringSourceTemp = (Unit) this.target;
			success = ((PlayerUnit) goapUnit).getUnit().canGather(this.gatheringSourceTemp);
		}

		return success;
	}
}
