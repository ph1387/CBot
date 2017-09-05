package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.IGoapUnit;
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
public abstract class UnloadAction extends WorkerAction {

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
		return ((PlayerUnit) goapUnit).getUnit().returnCargo();
	}

	@Override
	protected void resetSpecific() {
		this.target = new Object();
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

}
