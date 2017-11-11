package unitControlModule.stateFactories.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bwapi.Position;
import bwapi.TilePosition;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import unitControlModule.stateFactories.actions.AvailableActionsDefault;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
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
	private AttackMoveAction attackMoveAction;
	private ScoutBaseLocationAction scoutBaseLocationAction;

	public ActionUpdaterDefault(PlayerUnit playerUnit) {
		super(playerUnit);
	}

	// -------------------- Functions

	@Override
	public void update(PlayerUnit playerUnit) {
		super.update(playerUnit);

		if (this.playerUnit.currentState == PlayerUnit.UnitStates.ENEMY_MISSING) {
			this.baselocationScoutingConfiguration();
		} else {
			this.attackMoveAction.setTarget(this.attackMoveToNearestKnownUnitConfiguration());

			this.attackUnitAction.setTarget(this.playerUnit.getAttackableEnemyUnitToReactTo());
		}
	}

	@Override
	protected void init() {
		super.init();

		this.attackUnitAction = ((AttackUnitAction) this.getActionFromInstance(AttackUnitAction.class));
		this.attackMoveAction = ((AttackMoveAction) this.getActionFromInstance(AttackMoveAction.class));
		this.scoutBaseLocationAction = ((ScoutBaseLocationAction) this
				.getActionFromInstance(ScoutBaseLocationAction.class));
	}

	// TODO: UML CHANGE PARAMS RETURN TYPE
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
		// Or it just moves to the nearest one it knows of.
		else {
			List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>(
					this.playerUnit.getInformationStorage().getTrackerInfo().getEnemyUnits());
			enemyUnits.addAll(this.playerUnit.getInformationStorage().getTrackerInfo().getEnemyBuildings());

			// Find the closest unit of the known ones
			for (EnemyUnit unit : enemyUnits) {
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
	 * Function for configuring the associated scouting function of the Unit.
	 * Since the type of scouting action might differ from Unit to Unit a
	 * special function is needed to handle these cases.
	 */
	protected void baselocationScoutingConfiguration() {
		this.scoutBaseLocationAction.setTarget(findClosestReachableBasePosition());
	}

	/**
	 * If no enemy buildings are are being known of the Bot has to search for
	 * them. Find the closest BaseLocation with a timeStamp:
	 * <p>
	 * currentTime - timeStamp >= timePassed
	 * 
	 * @return the closest reachable base Position with a matching timeStamp.
	 */
	protected Position findClosestReachableBasePosition() {
		Position closestReachableBasePosition = null;
		HashMap<BaseLocation, Integer> baselocationsSearched = this.playerUnit.getInformationStorage()
				.getBaselocationsSearched();

		for (BaseLocation location : BWTA.getBaseLocations()) {
			Region baseRegion = ((BaseLocation) location).getRegion();

			if (baselocationsSearched.get(location) != null
					&& this.playerUnit.getUnit().hasPath(baseRegion.getCenter())) {
				if ((closestReachableBasePosition == null && Core.getInstance().getGame().elapsedTime()
						- baselocationsSearched.get(location) >= PlayerUnit.BASELOCATIONS_TIME_PASSED)
						|| (Core.getInstance().getGame().elapsedTime()
								- baselocationsSearched.get(location) >= PlayerUnit.BASELOCATIONS_TIME_PASSED
								&& this.playerUnit.getUnit().getDistance(location) < this.playerUnit.getUnit()
										.getDistance(closestReachableBasePosition))) {
					closestReachableBasePosition = baseRegion.getCenter();
				}
			}
		}
		return closestReachableBasePosition;
	}
}
