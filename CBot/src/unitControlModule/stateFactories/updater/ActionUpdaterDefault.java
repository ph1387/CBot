package unitControlModule.stateFactories.updater;

import bwapi.TilePosition;
import bwapi.Unit;
import unitControlModule.stateFactories.actions.AvailableActionsDefault;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;
import unitControlModule.unitWrappers.PlayerUnit;
import unitTrackerModule.EnemyUnit;

/**
 * SimpleActionUpdater.java --- Updater for updating a
 * {@link AvailableActionsDefault} instance.
 * 
 * @author P H - 26.02.2017
 *
 */
public class ActionUpdaterDefault extends ActionUpdaterGeneral {

	private AttackUnitAction attackUnitAction;
	private BaseAction attackMoveAction;
	private BaseAction scoutBaseLocationAction;

	public ActionUpdaterDefault(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		this.scoutBaseLocationAction.setTarget(new Object());

		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_KNOWN) {
			this.attackMoveAction.setTarget(this.attackMoveToNearestKnownUnitConfiguration());

			this.attackUnitAction.setTarget(this.attackUnitActionConfiguration());
		}
	}

	@Override
	protected void init() {
		super.init();

		this.attackUnitAction = ((AttackUnitAction) this.getActionFromInstance(AttackUnitAction.class));
		this.scoutBaseLocationAction = this.initScoutBaseLocationActionInstance();
		this.attackMoveAction = this.initAttackMoveAction();
	}

	/**
	 * Function for initializing the used scouting action. This is needed since
	 * some Units require the use of different types of actions. Therefore the
	 * types of the Actions as well as the instance of the Action used must be
	 * defined by each Unit individually.
	 * 
	 * @return the scouting Action that the Unit will be using.
	 */
	protected BaseAction initScoutBaseLocationActionInstance() {
		return ((ScoutBaseLocationAction) this.getActionFromInstance(ScoutBaseLocationAction.class));
	}

	/**
	 * Function for initializing the used attack move Action. This is needed
	 * since some Units require the use of different types of actions. Therefore
	 * the types of the Actions as well as the instance of the Action used must
	 * be defined by each Unit individually.
	 * 
	 * @return the scouting Action that the Unit will be using.
	 */
	protected BaseAction initAttackMoveAction() {
		return ((AttackMoveAction) this.getActionFromInstance(AttackMoveAction.class));
	}

	/**
	 * Function for the unit to configure its AttackMoveAction to the nearest
	 * enemy Unit (can be a building).
	 */
	protected TilePosition attackMoveToNearestKnownUnitConfiguration() {
		TilePosition closestUnitTilePosition = null;

		// Either the Unit has a possible attack target,
		if (this.playerUnit.getAttackableEnemyUnitToReactTo() != null) {
			closestUnitTilePosition = this.playerUnit.getAttackableEnemyUnitToReactTo().getTilePosition();
		}
		// Or it just moves to the nearest building it knows of.
		else {
			for (EnemyUnit unit : this.playerUnit.getInformationStorage().getTrackerInfo().getEnemyBuildings()) {
				if (closestUnitTilePosition == null || this.playerUnit.getUnit()
						.getDistance(unit.getLastSeenTilePosition().toPosition()) < this.playerUnit.getUnit()
								.getDistance(closestUnitTilePosition.toPosition())) {
					closestUnitTilePosition = unit.getLastSeenTilePosition();
				}
			}
		}
		return closestUnitTilePosition;
	}

	/**
	 * Function for the unit to configure its AttackUnitAction.
	 */
	protected Unit attackUnitActionConfiguration() {
		return this.playerUnit.getAttackableEnemyUnitToReactTo();
	}

}
