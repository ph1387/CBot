package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.worker.GatherGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.GatherMineralsAction;

/**
 * AvailableActionsWorker.java --- HashSet containing all worker Unit Actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class AvailableActionsWorker extends AvailableActionsDefault {

	public AvailableActionsWorker() {
		this.add(new GatherMineralsAction(null));
		this.add(new GatherGasAction(null));

		// TODO: Add constructing action
	}
}
