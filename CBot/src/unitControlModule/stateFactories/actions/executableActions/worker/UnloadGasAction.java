package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * UnloadGasAction.java --- An UnloadAction for gathered vaspene gas.
 * 
 * @author P H - 26.06.2017
 *
 */
public class UnloadGasAction extends UnloadAction {

	/**
	 * @param target
	 *            type Null
	 */
	public UnloadGasAction(Object target) {
		super(new Object());

		this.addEffect(new GoapState(0, "isCarryingGas", false));
		this.addPrecondition(new GoapState(0, "isCarryingGas", true));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().isCarryingGas();
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return !((PlayerUnit) goapUnit).getUnit().isCarryingGas();
	}
	
}
