package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsWorker;
import unitControlModule.stateFactories.actions.executableActions.worker.ConstructBuildingAction;
import unitControlModule.stateFactories.actions.executableActions.worker.ConstructionJob;
import unitControlModule.stateFactories.actions.executableActions.worker.GatherGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.GatherMineralsAction;
import unitControlModule.stateFactories.actions.executableActions.worker.ScoutBaseLocationWorkerAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadMineralsAction;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

/**
 * ActionUpdaterWorker.java --- Updater for updating a
 * {@link AvailableActionsWorker} instance.
 * 
 * @author P H - 25.03.2017
 *
 */
public class ActionUpdaterWorker extends ActionUpdaterDefault {

	public ActionUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);
		
		// TODO: Possible Change: Only perform once.
		((UnloadMineralsAction) this.getActionFromInstance(UnloadMineralsAction.class)).setTarget(this.playerUnit);
		((UnloadGasAction) this.getActionFromInstance(UnloadGasAction.class)).setTarget(this.playerUnit);
		
		((GatherMineralsAction) this.getActionFromInstance(GatherMineralsAction.class)).setTarget(((PlayerUnitWorker) playerUnit).getClosestFreeMineralField());
		((GatherGasAction) this.getActionFromInstance(GatherGasAction.class)).setTarget(((PlayerUnitWorker) playerUnit).getClosestFreeGasSource());
		
		// Set the target once and only change its UnitType afterwards
		if(((PlayerUnitWorker) playerUnit).getCurrentConstructionState() == PlayerUnitWorker.ConstructionState.AWAIT_CONFIRMATION) {
			if(((ConstructBuildingAction) this.getActionFromInstance(ConstructBuildingAction.class)).getTarget() == null) {
				((ConstructBuildingAction) this.getActionFromInstance(ConstructBuildingAction.class)).setTarget(new ConstructionJob(((PlayerUnitWorker) playerUnit).getAssignedBuildingType(), ((PlayerUnitWorker) playerUnit).getUnit().getTilePosition()));
			} else {
				((ConstructionJob) (((ConstructBuildingAction) this.getActionFromInstance(ConstructBuildingAction.class)).getTarget())).setBuilding(((PlayerUnitWorker) playerUnit).getAssignedBuildingType());
			}
		}
	}
	
	@Override
	protected void baselocationScoutingConfiguration() {
		((ScoutBaseLocationWorkerAction) this.getActionFromInstance(ScoutBaseLocationWorkerAction.class)).setTarget(findClosestReachableBasePosition());
	}
}
