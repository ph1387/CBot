package unitControlModule.stateFactories.actions;

import javaGOAP.GoapAction;
import unitControlModule.stateFactories.actions.executableActions.worker.ConstructBuildingAction;
import unitControlModule.stateFactories.actions.executableActions.worker.GatherGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.GatherMineralsAction;
import unitControlModule.stateFactories.actions.executableActions.worker.MoveToNearestCenterAction;
import unitControlModule.stateFactories.actions.executableActions.worker.ScoutBaseLocationWorkerAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadMineralsAction;

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
		this.add(new ConstructBuildingAction(null));
		this.add(new UnloadMineralsAction(null));
		this.add(new UnloadGasAction(null));
		this.add(new MoveToNearestCenterAction(null));
	}
	
	@Override
	protected GoapAction defineScoutingAction() {
		return new ScoutBaseLocationWorkerAction(null);
	}
}
