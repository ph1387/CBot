package unitControlModule.stateFactories.updater;

import unitControlModule.stateFactories.actions.AvailableActionsWorker;
import unitControlModule.stateFactories.actions.executableActions.worker.ConstructBuildingAction;
import unitControlModule.stateFactories.actions.executableActions.worker.ConstructionJob;
import unitControlModule.stateFactories.actions.executableActions.worker.GatherGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.GatherMineralsAction;
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
		
		((GatherMineralsAction) this.getActionFromInstance(GatherMineralsAction.class)).setTarget(((PlayerUnitWorker) playerUnit).getClosestFreeMineralField());
		((GatherGasAction) this.getActionFromInstance(GatherGasAction.class)).setTarget(((PlayerUnitWorker) playerUnit).getClosestFreeGasSource());
		
		// Only map the building type to the Unit if it is not null and the Unit is not already mapped to one.
		// Do NOT add a counter to the PlayerUnitWorker, since putting it here in the HashMap will override the added counter functionality!
		if(((PlayerUnitWorker) playerUnit).getAssignedBuildingType() != null && PlayerUnitWorker.mappedBuildActions.getOrDefault(((PlayerUnitWorker) playerUnit).getUnit(), null) == null) {
			((ConstructBuildingAction) this.getActionFromInstance(ConstructBuildingAction.class)).setTarget(new ConstructionJob(((PlayerUnitWorker) playerUnit).getAssignedBuildingType(), ((PlayerUnitWorker) playerUnit).getUnit().getTilePosition()));
			PlayerUnitWorker.mappedBuildActions.put(((PlayerUnitWorker) playerUnit).getUnit(), ((PlayerUnitWorker) playerUnit).getAssignedBuildingType());
		}
	}
}
