package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * UnloadMineralsAction.java --- An UnloadAction for gathered minerals.
 * 
 * @author P H - 26.06.2017
 *
 */
public class UnloadMineralsAction extends UnloadAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public UnloadMineralsAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "isCarryingMinerals", false));
		this.addPrecondition(new GoapState(0, "isCarryingMinerals", true));
	}

	// -------------------- Functions

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return ((PlayerUnit) goapUnit).getUnit().isCarryingMinerals();
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		return !((PlayerUnit) goapUnit).getUnit().isCarryingMinerals();
	}
	
	// TODO: UML ADD
	@Override
	public int defineMaxGroupSize() {
		return 0;
	}
	
}
