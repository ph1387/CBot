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

	private UnloadMineralsAction unloadMineralsAction;
	private UnloadGasAction unloadGasAction;
	private GatherMineralsAction gatherMineralsAction;
	private GatherGasAction gatherGasAction;
	private ConstructBuildingAction constructBuildingAction;
	private ScoutBaseLocationWorkerAction scoutBaseLocationWorkerAction;

	public ActionUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// TODO: Possible Change: Only perform once.
		this.unloadMineralsAction.setTarget(this.playerUnit);
		this.unloadGasAction.setTarget(this.playerUnit);

		this.gatherMineralsAction.setTarget(((PlayerUnitWorker) playerUnit).getClosestFreeMineralField());
		this.gatherGasAction.setTarget(((PlayerUnitWorker) playerUnit).getClosestFreeGasSource());

		// Set the target once and only change its UnitType afterwards
		if (((PlayerUnitWorker) playerUnit)
				.getCurrentConstructionState() == PlayerUnitWorker.ConstructionState.AWAIT_CONFIRMATION) {
			if (this.constructBuildingAction.getTarget() == null) {
				this.constructBuildingAction
						.setTarget(new ConstructionJob(((PlayerUnitWorker) playerUnit).getAssignedBuildingType(),
								((PlayerUnitWorker) playerUnit).getUnit().getTilePosition()));
			} else {
				((ConstructionJob) this.constructBuildingAction.getTarget())
						.setBuilding(((PlayerUnitWorker) playerUnit).getAssignedBuildingType());
			}
		}
	}

	@Override
	protected void init() {
		super.init();

		this.unloadMineralsAction = ((UnloadMineralsAction) this.getActionFromInstance(UnloadMineralsAction.class));
		this.unloadGasAction = ((UnloadGasAction) this.getActionFromInstance(UnloadGasAction.class));
		this.gatherMineralsAction = ((GatherMineralsAction) this.getActionFromInstance(GatherMineralsAction.class));
		this.gatherGasAction = ((GatherGasAction) this.getActionFromInstance(GatherGasAction.class));

		this.constructBuildingAction = ((ConstructBuildingAction) this
				.getActionFromInstance(ConstructBuildingAction.class));

		this.scoutBaseLocationWorkerAction = ((ScoutBaseLocationWorkerAction) this
				.getActionFromInstance(ScoutBaseLocationWorkerAction.class));
	}

	@Override
	protected void baselocationScoutingConfiguration() {
		this.scoutBaseLocationWorkerAction.setTarget(findClosestReachableBasePosition());
	}
}
