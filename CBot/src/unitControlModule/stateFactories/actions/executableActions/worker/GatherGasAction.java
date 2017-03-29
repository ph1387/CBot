package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.GoapState;

// TODO: UML
/**
 * GatherGasAction.java --- Action for gathering gas.
 * 
 * @author P H - 29.03.2017
 *
 */
public class GatherGasAction extends GatherAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public GatherGasAction(Object target) {
		super(target);
		
		this.addEffect(new GoapState(0, "gatheringGas", true));
	}

	// -------------------- Functions

}
