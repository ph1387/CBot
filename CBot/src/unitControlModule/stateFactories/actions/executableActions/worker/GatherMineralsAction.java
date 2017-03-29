package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.GoapState;

//TODO: UML
/**
 * GatherMineralsAction.java --- Action for gathering minerals.
 * 
 * @author P H - 29.03.2017
 *
 */
public class GatherMineralsAction extends GatherAction {

	/**
	 * @param target
	 *            type: Unit
	 */
	public GatherMineralsAction(Object target) {
		super(target);
		
		this.addEffect(new GoapState(0, "gatheringMinerals", true));
	}

	// -------------------- Functions

}
