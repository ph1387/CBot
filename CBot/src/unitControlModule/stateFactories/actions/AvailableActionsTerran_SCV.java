package unitControlModule.stateFactories.actions;

import unitControlModule.stateFactories.actions.executableActions.FollowActionTerran_SCV;
import unitControlModule.stateFactories.actions.executableActions.worker.RepairActionBuilding;
import unitControlModule.stateFactories.actions.executableActions.worker.RepairActionUnit;

/**
 * AvailableActionsTerran_SCV.java --- HashSet containing all Terran_SCV
 * Actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class AvailableActionsTerran_SCV extends AvailableActionsWorker {

	public AvailableActionsTerran_SCV() {
		this.add(new FollowActionTerran_SCV(null));
		this.add(new RepairActionUnit(null));
		this.add(new RepairActionBuilding(null));
	}
}
