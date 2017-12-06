package unitControlModule.stateFactories.updater;

import bwapi.TilePosition;
import unitControlModule.stateFactories.actions.AvailableActionsWorker;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.worker.MoveToNearestCenterAction;
import unitControlModule.stateFactories.actions.executableActions.worker.ScoutBaseLocationWorkerAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadGasAction;
import unitControlModule.stateFactories.actions.executableActions.worker.UnloadMineralsAction;
import unitControlModule.unitWrappers.PlayerUnit;

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
	// TODO: UML REMOVE
	// private ScoutBaseLocationWorkerAction scoutBaseLocationWorkerAction;
	private MoveToNearestCenterAction moveToNearestCenterAction;

	public ActionUpdaterWorker(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		// TODO: Possible Change: Only perform once.
		this.unloadMineralsAction.setTarget(new Object());
		this.unloadGasAction.setTarget(new Object());

		this.moveToNearestCenterAction.setTarget(this.playerUnit.getClosestCenter());

		// Construction / Gather actions are set separately in their own
		// instance. These require no interaction with the Updater since they
		// are managed by other Classes.
	}

	@Override
	protected void init() {
		super.init();

		this.unloadMineralsAction = ((UnloadMineralsAction) this.getActionFromInstance(UnloadMineralsAction.class));
		this.unloadGasAction = ((UnloadGasAction) this.getActionFromInstance(UnloadGasAction.class));

		this.moveToNearestCenterAction = ((MoveToNearestCenterAction) this
				.getActionFromInstance(MoveToNearestCenterAction.class));
	}

	// TODO: UML ADD
	@Override
	protected BaseAction initScoutBaseLocationActionInstance() {
		return ((ScoutBaseLocationWorkerAction) this.getActionFromInstance(ScoutBaseLocationWorkerAction.class));
	}

	// TODO: UML ADD
	@Override
	protected TilePosition attackMoveToNearestKnownUnitConfiguration() {
		TilePosition returnTilePosition = null;

		// No buildings are attacked like in the superclass.
		if (this.playerUnit.getAttackableEnemyUnitToReactTo() != null) {
			returnTilePosition = this.playerUnit.getAttackableEnemyUnitToReactTo().getTilePosition();
		}
		
		return returnTilePosition;
	}

	// TODO: UML REMOVE
	// protected void baselocationScoutingConfiguration() {

}
