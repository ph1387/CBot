package unitControlModule.stateFactories.actions.executableActions.worker;

import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.Region;
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

	// TODO: UML ADD
	/**
	 * MoveToNearestCenterActionWrapper.java --- Wrapper Class used for smartly
	 * moving between ChokePoints.
	 * 
	 * @author P H - 18.03.2018
	 *
	 */
	private class MoveToNearestCenterActionWrapper implements SmartlyMovingActionWrapper {

		private Unit centerToMoveTo;

		public MoveToNearestCenterActionWrapper(Unit centerToMoveTo) {
			this.centerToMoveTo = centerToMoveTo;
		}

		@Override
		public boolean performInternalAction(IGoapUnit goapUnit, Object target) {
			return ((PlayerUnit) goapUnit).getUnit().move(this.centerToMoveTo.getPosition());
		}

		@Override
		public Position convertTarget(Object target) {
			return this.centerToMoveTo.getPosition();
		}

	}

	// TODO: UML ADD
	private SmartlyMovingActionWrapper actionWrapper;

	// The distance at which the isDone function returns true and the Action is
	// finished.
	private int minDistanceToTargetCenter = 128;

	// The center Unit the Unit is moving to. Needed since the target of this
	// action might change (IsDone must know what to check for).
	private Unit centerToMoveTo;

	/**
	 * @param target
	 *            type: Unit (A center Unit)
	 */
	public MoveToNearestCenterAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "canGather", true));
		this.addPrecondition(new GoapState(0, "canGather", false));
		this.addPrecondition(new GoapState(0, "allowGathering", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.centerToMoveTo == null) {
			this.centerToMoveTo = (Unit) this.target;
			this.actionWrapper = new MoveToNearestCenterActionWrapper(this.centerToMoveTo);
		}

		try {
			Region targetRegion = BWTA.getRegion(this.centerToMoveTo.getPosition());

			this.performSmartlyMovingToRegion(goapUnit, targetRegion, this.actionWrapper);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return success;
	}

	@Override
	protected void resetSpecific() {
		this.centerToMoveTo = null;
		this.actionWrapper = null;
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
		boolean isDone;

		// The initial test must use the target as reference.
		if (this.centerToMoveTo == null) {
			isDone = ((PlayerUnit) goapUnit).isNearPosition(((Unit) this.target).getPosition(),
					this.minDistanceToTargetCenter);
		} else {
			isDone = ((PlayerUnit) goapUnit).isNearPosition(this.centerToMoveTo.getPosition(),
					this.minDistanceToTargetCenter);
		}

		return isDone;
	}

}
