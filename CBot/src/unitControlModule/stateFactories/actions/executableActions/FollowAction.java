package unitControlModule.stateFactories.actions.executableActions;

import bwapi.Unit;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * FollowAction.java --- A follow action for {@link PlayerUnit}s to follow other
 * Units around the map. This Action is mostly used for supporting Units since
 * these require special treatment regarding the movement on the map.
 * 
 * @author P H - 23.09.2017
 *
 */
public abstract class FollowAction extends BaseAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public FollowAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "canMove", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().follow((Unit) this.target);
	}

	@Override
	protected void resetSpecific() {

	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null && ((PlayerUnit) goapUnit).getUnit().canFollow((Unit) this.target);
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1.f;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return this.target == null || (this.target != null && ((PlayerUnit) goapUnit)
				.isNearPosition(((Unit) this.target).getPosition(), this.defineDistanceToTarget()));
	}

	/**
	 * Function for defining the distance at which the
	 * {@link #isDone(IGoapUnit)} function returns true, which indicates that
	 * the Unit is close enough to the target.
	 * 
	 * @return the distance at which the executing Unit "stops" following (The
	 *         action is finished).
	 */
	protected abstract int defineDistanceToTarget();

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	// -------------------- Group

	@Override
	public boolean canPerformGrouped() {
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}

	@Override
	public int defineMaxGroupSize() {
		return 0;
	}

	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}
}
