package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Unit;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * RetreatToNearestCenterAction.java --- A Action with which the worker can
 * return to the nearest center building.
 * 
 * @author P H - 25.08.2017
 *
 */
public class MoveToNearestCenterAction extends WorkerAction {

	// The distance at which the isDone function returns true and the Action is
	// finished.
	private int minDistanceToTargetCenter = 128;

	/**
	 * @param target
	 *            type: Unit (A center Unit)
	 */
	public MoveToNearestCenterAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "allowGathering", true));
		this.addPrecondition(new GoapState(0, "allowGathering", false));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().move(((Unit) this.target).getPosition());
	}

	@Override
	protected void resetSpecific() {

	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null
				&& (((Unit) this.target).getType() == Core.getInstance().getPlayer().getRace().getCenter());
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().getDistance((Unit) this.target);
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).isNearPosition(((Unit) this.target).getPosition(),
				this.minDistanceToTargetCenter);
	}

}
