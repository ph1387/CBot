package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * UnloadAction.java --- Unload Action for a PlayerUnitWorker. Both minerals and
 * gas can be unloaded. This is mainly used for emptying the current resource
 * amount the worker Unit is holding before attempting a scout mission or
 * constructing a building etc.
 * 
 * @author P H - 26.06.2017
 *
 */
public abstract class UnloadAction extends BaseAction {

	private static final int COUNTER_MAX = 25;
	private int counter = 0;

	/**
	 * @param target
	 *            type: Null
	 */
	public UnloadAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Counting backwards is more efficient.
		if (this.counter <= 0) {
			this.counter = COUNTER_MAX;

			success = ((PlayerUnit) goapUnit).getUnit().returnCargo(true);
		} else {
			this.counter--;
		}
		return success;
	}

	@Override
	protected void resetSpecific() {
		this.counter = 0;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

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
		// All unload actions are executed by a single Unit.
		return false;
	}

	@Override
	public boolean performGrouped(IGoapUnit groupLeader, IGoapUnit groupMember) {
		return false;
	}
	
	// TODO: UML ADD
	@Override
	public int defineMaxGroupSize() {
		return 0;
	}
	
	// TODO: UML ADD
	@Override
	public int defineMaxLeaderTileDistance() {
		return 0;
	}
}
