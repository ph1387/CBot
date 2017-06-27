package unitControlModule.stateFactories.actions.executableActions.worker;

import javaGOAP.GoapState;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;

/**
 * ScoutBaseLocationWorkerAction.java --- A special ScoutingAction for workers.
 * This is mainly used for workers to drop off any resources they might have
 * gathered so far. This prevents them from losing them in the process of
 * scouting.
 * 
 * @author P H - 27.06.2017
 *
 */
public class ScoutBaseLocationWorkerAction extends ScoutBaseLocationAction {

	/**
	 * @param target
	 *            type: Position
	 */
	public ScoutBaseLocationWorkerAction(Object target) {
		super(target);

		this.addPrecondition(new GoapState(0, "isCarryingMinerals", false));
		this.addPrecondition(new GoapState(0, "isCarryingGas", false));
	}

	// -------------------- Functions

}
