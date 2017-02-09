package unitControlModule;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import unitControlModule.actions.AttackMoveUnitAction;
import unitControlModule.actions.ScoutBaseLocationAction;
import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;
import unitTrackerModule.EnemyUnit;
import unitTrackerModule.UnitTrackerModule;

/**
 * PlayerUnit.java --- Wrapper for a player unit.
 * 
 * @author P H - 29.01.2017
 *
 */
public class PlayerUnit extends GoapUnit {

	protected static Hashtable<BaseLocation, Integer> BASELOCATIONS_SEARCHED = new Hashtable<>();
	protected static int BASELOCATIONS_TIME_PASSED = 180;
	protected static Integer DEFAULT_SEARCH_RADIUS = 5;

	protected Unit unit;

	protected enum UnitStates {
		ENEMY_MISSING, ENEMY_KNOWN
	}

	protected UnitStates currentState = UnitStates.ENEMY_MISSING;

	/**
	 * @param unit
	 *            the unit the class wraps around.
	 */
	public PlayerUnit(Unit unit) {
		this.unit = unit;

		this.addWorldState(new GoapState(1, "enemyKnown", false));
		this.addWorldState(new GoapState(1, "destroyUnit", false));

		this.addGoalState(new GoapState(1, "enemyKnown", true));
		this.addGoalState(new GoapState(2, "destroyUnit", true));

		// Set default values in the beginning.
		if (BASELOCATIONS_SEARCHED.size() == 0) {
			for (BaseLocation location : BWTA.getBaseLocations()) {
				BASELOCATIONS_SEARCHED.put(location, 0);
			}
		}
	}

	// -------------------- Functions

	@Override
	protected void goapPlanFound(Queue<GoapAction> actions) {

	}

	@Override
	protected void goapPlanFailed(Queue<GoapAction> actions) {

	}

	@Override
	protected void goapPlanFinished() {

	}

	@Override
	protected void update() {
		// Changing the worldState with a FSM
		if (this.currentState == UnitStates.ENEMY_MISSING) {
			this.changeDestroyUnit(false);
			this.changeEnemyKnown(false);

			this.actOnBuildingsMissing();

			if(UnitTrackerModule.getInstance().enemyUnits.size() != 0 || UnitTrackerModule.getInstance().enemyBuildings.size() != 0) {
				this.resetActions();
				this.currentState = UnitStates.ENEMY_KNOWN;
			}
		} else if (this.currentState == UnitStates.ENEMY_KNOWN) {
			if(UnitTrackerModule.getInstance().enemyUnits.size() == 0 && UnitTrackerModule.getInstance().enemyBuildings.size() == 0) {
				this.changeEnemyKnown(false);
				this.currentState = UnitStates.ENEMY_MISSING;
			} else {
				this.changeEnemyKnown(true);
				this.actOnUnitsKnown();
			}
		}

		this.updateBaseLocationsSearched();
	}

	@Override
	protected boolean moveTo(Object target) {
		boolean moved = false;

		if (target instanceof TilePosition) {
			moved = this.unit.move(((TilePosition) target).toPosition());
		} else if (target instanceof Unit) {
			moved = this.unit.move(((Unit) target).getPosition());
		} else if (target instanceof Position) {
			moved = this.unit.move((Position) target);
		}
		return moved;
	}

	/**
	 * Test if a unit is near another TilePosition
	 * 
	 * @param targetTilePosition
	 *            the TilePosition the units position is being checked against.
	 * @param radius
	 *            the radius around the target in which the function returns
	 *            true.
	 * @return true or false depending if the unit is in the radius around the
	 *         TilePosition.
	 */
	public boolean isNear(TilePosition targetTilePosition, Integer radius) {
		int targetX = targetTilePosition.getX();
		int targetY = targetTilePosition.getY();
		int unitX = this.unit.getTilePosition().getX();
		int unitY = this.unit.getTilePosition().getY();

		if (radius == null) {
			radius = DEFAULT_SEARCH_RADIUS;
		}

		return (unitX >= targetX - radius && unitX <= targetX + radius && unitY >= targetY - radius
				&& unitY <= targetY + radius);
	}

	/**
	 * If no enemy buildings are are being known of the Bot has to search for
	 * them. Find the closest BaseLocation with a timeStamp:
	 * <p>
	 * currentTime - timeStamp >= timePassed
	 * <p>
	 * Create a new ScoutBaseLocationAction or change the current one to target
	 * a new location.
	 */
	private void actOnBuildingsMissing() {
		GoapAction scoutBaseLocationAction = this.getActionFromInstance(ScoutBaseLocationAction.class);
		Position closestReachableBasePosition = null;

		for (BaseLocation location : BWTA.getBaseLocations()) {
			Region baseRegion = ((BaseLocation) location).getRegion();

			if (BASELOCATIONS_SEARCHED.get(location) != null && this.unit.hasPath(baseRegion.getCenter())) {
				if ((closestReachableBasePosition == null && Core.getInstance().getGame().elapsedTime()
						- BASELOCATIONS_SEARCHED.get(location) >= BASELOCATIONS_TIME_PASSED)
						|| (Core.getInstance().getGame().elapsedTime()
								- BASELOCATIONS_SEARCHED.get(location) >= BASELOCATIONS_TIME_PASSED
								&& this.unit.getDistance(location) < this.unit
										.getDistance(closestReachableBasePosition))) {
					closestReachableBasePosition = baseRegion.getCenter();
				}
			}
		}

		// Check actions first to prevent an infinite amount of action
		// generations
		if (scoutBaseLocationAction == null) {
			this.addAvailableAction(new ScoutBaseLocationAction(closestReachableBasePosition));
		} else {
			((ScoutBaseLocationAction) scoutBaseLocationAction).setTarget(closestReachableBasePosition);
		}
	}

	/**
	 * Create a new AttackMoveUnitAction instance or change the current one to
	 * target a new TilePosition of another unit.
	 */
	private void actOnUnitsKnown() {
		GoapAction attackMoveUnitAction = this.getActionFromInstance(AttackMoveUnitAction.class);
		TilePosition closestUnitTilePosition = null;
		
		List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>(UnitTrackerModule.getInstance().enemyUnits);
		enemyUnits.addAll(UnitTrackerModule.getInstance().enemyBuildings);

		// Find the closest unit of the known ones
		for (EnemyUnit unit : enemyUnits) {
			if (closestUnitTilePosition == null || this.unit.getDistance(unit.getLastSeenTilePosition().toPosition()) < this.unit
					.getDistance(closestUnitTilePosition.toPosition())) {
				closestUnitTilePosition = unit.getLastSeenTilePosition();
			}
		}
		
		// Check actions first to prevent an infinite amount of action
		// generations
		if (attackMoveUnitAction == null) {
			this.addAvailableAction(new AttackMoveUnitAction(closestUnitTilePosition));
		} else {
			((AttackMoveUnitAction) attackMoveUnitAction).setTarget(closestUnitTilePosition);
		}
	}

	/**
	 * Get the GoapAction from the availableActions HashSet that is an instance
	 * of the specific class.
	 * 
	 * @param instanceClass
	 *            the class of which an instance is being searched in the
	 *            availableActions HashSet.
	 * @return the action that is an instance of the given class.
	 */
	private <T> GoapAction getActionFromInstance(Class<T> instanceClass) {
		GoapAction actionMatch = null;

		for (GoapAction action : this.getAvailableActions()) {
			if (instanceClass.isInstance(action)) {
				actionMatch = action;

				break;
			}
		}
		return actionMatch;
	}

	/**
	 * Update the searched BaseLocations if the unit is in the range of one of
	 * them.
	 */
	private void updateBaseLocationsSearched() {
		for (BaseLocation location : BWTA.getBaseLocations()) {
			if (this.isNear(location.getRegion().getCenter().toTilePosition(), null)) {
				BASELOCATIONS_SEARCHED.put(location, Core.getInstance().getGame().elapsedTime());
			}
		}
	}

	/**
	 * Convenience function.
	 * 
	 * @see #changeWorldStateEffect(String effect, Object value)
	 */
	private void changeEnemyKnown(Object value) {
		this.changeWorldStateEffect("enemyKnown", value);
	}

	/**
	 * Convenience function.
	 * 
	 * @see #changeWorldStateEffect(String effect, Object value)
	 */
	private void changeDestroyUnit(Object value) {
		this.changeWorldStateEffect("destroyUnit", value);
	}

	/**
	 * Change the world state accordingly.
	 * 
	 * @param effect
	 *            the effect which is going to be changed.
	 * @param value
	 *            the value the effect shall have.
	 */
	private void changeWorldStateEffect(String effect, Object value) {
		for (GoapState state : this.getWorldState()) {
			if (state.effect.equals(effect)) {
				state.value = value;

				break;
			}
		}
	}

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return this.unit;
	}

	// -------------------- Eventlisteners

	// -------------------- Events
}
