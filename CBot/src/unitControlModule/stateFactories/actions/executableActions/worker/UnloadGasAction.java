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
	 *            type Unit
	 */
	public UnloadGasAction(Object target) {
		super(target);

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
	
	// TODO: UML ADD
	@Override
	public int defineMaxGroupSize() {
		return 0;
	}
	
}
