package unitControlModule;

import java.util.Hashtable;
import java.util.Queue;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import unitControlModule.actions.ScoutBaseLocationAction;
import unitControlModule.actions.ScoutBuildingLocationAction;
import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;
import unitTrackerModule.EnemyUnit;
import unitTrackerModule.UnitTrackerModule;

/**
 * PlayerUnit.java --- Wrapper for a unit of the player
 * 
 * @author P H - 29.01.2017
 *
 */
public class PlayerUnit extends GoapUnit {

	protected static Hashtable<BaseLocation, Integer> BASELOCATIONS_SEARCHED = new Hashtable<>();
	protected static int BASELOCATIONS_TIME_PASSED = 180;
	protected static Integer DEFAULT_SEARCH_RADIUS = 5;

	protected Unit unit;

	/**
	 * @param unit
	 *            the unit the class wraps around.
	 */
	public PlayerUnit(Unit unit) {
		this.unit = unit;

		this.addWorldState(new GoapState(1, "enemyBuildingsKnown", false));
		this.addWorldState(new GoapState(1, "informationGathering", false));

		this.addGoalState(new GoapState(1, "enemyBuildingsKnown", true));
		this.addGoalState(new GoapState(2, "informationGathering", true));

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
		if (UnitTrackerModule.getInstance().enemyBuildings.size() == 0) {
			this.changeEnemyBuildingsKnown(false);

			this.actOnBuildingsMissing();
		} else {
			this.actOnBuildingsKnown();
		}

		this.updateBaseLocationsSearched();

		// Reset the information gathering woldState so that units always try to
		// scout enemy building locations as a last resort.
		this.changeInformationgathering(false);
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
	 * the new BaseLoaction.
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
	 * Scout the enemies buildings since their position is known.
	 */
	private void actOnBuildingsKnown() {
		GoapAction scoutBuildingLocationAction = this.getActionFromInstance(ScoutBuildingLocationAction.class);
		TilePosition closestBuildingTilePosition = null;

		// Find the closest building of the known ones
		for (EnemyUnit building : UnitTrackerModule.getInstance().enemyBuildings) {
			if (closestBuildingTilePosition == null
					|| this.unit.getDistance(building.getUnit().getPosition()) < this.unit
							.getDistance(closestBuildingTilePosition.toPosition())) {
				closestBuildingTilePosition = building.getLastSeenTilePosition();
			}
		}

		// Check actions first to prevent an infinite amount of action
		// generations
		if (scoutBuildingLocationAction == null) {
			this.addAvailableAction(new ScoutBuildingLocationAction(closestBuildingTilePosition));
		} else {
			((ScoutBuildingLocationAction) scoutBuildingLocationAction).setTarget(closestBuildingTilePosition);
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
	private GoapAction getActionFromInstance(Class instanceClass) {
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
	 * them
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
	private void changeEnemyBuildingsKnown(Object value) {
		this.changeWorldStateEffect("enemyBuildingsKnown", value);
	}

	/**
	 * Convenience function.
	 * 
	 * @see #changeWorldStateEffect(String effect, Object value)
	 */
	private void changeInformationgathering(Object value) {
		this.changeWorldStateEffect("informationGathering", value);
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
