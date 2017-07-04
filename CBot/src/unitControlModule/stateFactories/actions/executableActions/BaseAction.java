package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.Game;
import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwta.Region;
import core.CBot;
import core.Core;
import javaGOAP.GoapAction;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * BaseAction.java --- Superclass for all PlayerUnit actions.
 * 
 * @author P H - 09.02.2017
 *
 */
public abstract class BaseAction extends GoapAction {

	private static final int EXPAND_MULTIPLIER_MAX = 5;
	private static final int TILE_RADIUS_AROUND_UNITS_SEARCH = 1;
	private static final int DEFAULT_SEARCH_STEP_DISTANCE = 200;

	protected static HashMap<PlayerUnit, BaseAction> currentlyExecutingActions = new HashMap<>();

	protected boolean actionChangeTrigger = false;
	protected IGoapUnit currentlyExecutingUnit;

	public BaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performAction(IGoapUnit goapUnit) {
		BaseAction storedAction = BaseAction.currentlyExecutingActions.get((PlayerUnit) goapUnit);

		// Check if the executing GoapAction has changed and if it did, enable a
		// trigger on which the subclass can react to.
		if (storedAction != null && storedAction.equals(this)) {
			this.actionChangeTrigger = false;
		} else {
			this.actionChangeTrigger = true;
		}

		// Store the executed action in the HashMap as well as the executing
		// Unit separately for having access to the Action and reset it when it
		// finishes.
		BaseAction.currentlyExecutingActions.put((PlayerUnit) goapUnit, this);
		this.currentlyExecutingUnit = goapUnit;

		return this.performSpecificAction(goapUnit);
	}

	protected abstract boolean performSpecificAction(IGoapUnit goapUnit);

	/**
	 * Function used for resetting the entry in the currentlyExecutingActions
	 * HashMap. This function has to be called when the GoapAction finishes so
	 * that the actionTrigger is going to be enabled in the next iteration.
	 */
	protected void resetStoredAction() {
		BaseAction.currentlyExecutingActions.put((PlayerUnit) this.currentlyExecutingUnit, null);
	}

	/**
	 * Function for testing if a Position is inside the map.
	 * 
	 * @param p
	 *            the Position that is going to be checked.
	 * @return true or false depending if the Position is inside the map or not.
	 */
	protected boolean isInsideMap(Position p) {
		Game game = Core.getInstance().getGame();

		return (p.getX() < (game.mapWidth() * Core.getInstance().getTileSize()) || p.getX() >= 0
				|| p.getY() < (game.mapHeight() * Core.getInstance().getTileSize()) || p.getY() >= 0);
	}

	@Override
	protected void reset() {
		this.target = null;
		this.resetStoredAction();
		this.resetSpecific();
	}

	/**
	 * Gets called when the Action is finished or removed from the FSM Stack
	 * after resetting the currentlyExecutingActions entry in the corresponding
	 * HashMap.
	 */
	protected abstract void resetSpecific();

	/**
	 * Function for finding the Polygon that the Position is in.
	 * 
	 * @param position
	 *            the Position that is being checked.
	 * @return the Region and the Polygon that the Position is located in.
	 */
	public static Pair<Region, Polygon> findBoundariesPositionIsIn(Position position) {
		Pair<Region, Polygon> matchingRegionPolygonPair = null;

		// Search for the Pair of Regions and Polygons that includes the Unit's
		// Position.
		for (Pair<Region, Polygon> pair : CBot.getInstance().getInformationStorage().getMapInfo().getMapBoundaries()) {
			if (pair.first.getPolygon().isInside(position)) {
				matchingRegionPolygonPair = pair;
				break;
			}
		}
		return matchingRegionPolygonPair;
	}

	protected interface PlayerUnitSearchCondition {
		public boolean isConditionMet(PlayerUnit playerUnit, Unit testingUnit);
	}

	/**
	 * Convenience function.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that the search is based around.
	 * @return a HashSet containing all Units in a range around the given
	 *         PlayerUnit.
	 */
	public static HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit playerUnit) {
		return getPlayerUnitsInIncreasingRange(playerUnit, 0, DEFAULT_SEARCH_STEP_DISTANCE, null);
	}

	/**
	 * Convenience function.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that the search is based around.
	 * @param stepDistance
	 *            the step distance at which Units are being searched for.
	 * @return a HashSet containing all Units in a range around the given
	 *         PlayerUnit.
	 */
	public static HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit playerUnit, double stepDistance) {
		return getPlayerUnitsInIncreasingRange(playerUnit, 0, stepDistance, null);
	}

	/**
	 * Convenience function.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that the search is based around.
	 * @param stepDistance
	 *            the step distance at which Units are being searched for.
	 * @param condition
	 *            the applying condition which each Unit has to fulfill.
	 * @return a HashSet containing all Units in a range around the given
	 *         PlayerUnit.
	 */
	public static HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit playerUnit, double stepDistance,
			PlayerUnitSearchCondition condition) {
		return getPlayerUnitsInIncreasingRange(playerUnit, 0, stepDistance, condition);
	}

	/**
	 * Convenience function.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that the search is based around.
	 * @param condition
	 *            the applying condition which each Unit has to fulfill.
	 * @return a HashSet containing all Units in a range around the given
	 *         PlayerUnit.
	 */
	public static HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit playerUnit,
			PlayerUnitSearchCondition condition) {
		return getPlayerUnitsInIncreasingRange(playerUnit, 0, DEFAULT_SEARCH_STEP_DISTANCE, condition);
	}

	/**
	 * Function for retrieving all Units in an increasing range around the given
	 * PlayerUnit. The range at which the Units are searched for increases
	 * stepwise until Units are found or the preset maximum is reached. This
	 * function runs until either Units are found or the maximum expand
	 * multiplier is reached. It also returns only Units that have a minimum
	 * distance towards the given PlayerUnit.
	 * 
	 * @param playerUnit
	 *            the PlayerUnit that the search is based around.
	 * @param minDistance
	 *            the minimum distance a Unit has to be away from the PlayerUnit
	 *            for it to count.
	 * @param stepDistance
	 *            the step distance at which Units are being searched for.
	 * @return a HashSet containing all Units in a range around the given
	 *         PlayerUnit with at least minimum distance to it.
	 */
	public static HashSet<Unit> getPlayerUnitsInIncreasingRange(PlayerUnit playerUnit, double minDistance,
			double stepDistance, PlayerUnitSearchCondition condition) {
		HashSet<Unit> unitsTooClose = new HashSet<Unit>();
		HashSet<Unit> unitsInRange = new HashSet<Unit>();
		int iterationCounter = 1;

		// Increase range until a Unit is found or the threshold is reached.
		while (unitsInRange.isEmpty() && iterationCounter <= EXPAND_MULTIPLIER_MAX) {
			HashSet<Unit> foundUnits = playerUnit.getAllPlayerUnitsInRange((int) (iterationCounter * stepDistance));
			HashSet<Unit> unitsToBeRemoved = new HashSet<Unit>();

			if (condition == null) {
				// Test all found Units, a Unit has to have a minimum distance
				// to
				// the PlayerUnit.
				for (Unit unit : foundUnits) {
					if (!unitsTooClose.contains(unit)
							&& playerUnit.getUnit().getDistance(unit.getPosition()) < minDistance) {
						unitsToBeRemoved.add(unit);
						unitsTooClose.add(unit);
					}
				}
			} else {
				// Test all found Units, a Unit has to have a minimum distance
				// to
				// the PlayerUnit AND also fulfill the given condition.
				for (Unit unit : foundUnits) {
					if (!unitsTooClose.contains(unit)
							&& playerUnit.getUnit().getDistance(unit.getPosition()) < minDistance
							|| !condition.isConditionMet(playerUnit, unit)) {
						unitsToBeRemoved.add(unit);
						unitsTooClose.add(unit);
					}
				}
			}

			for (Unit unit : unitsToBeRemoved) {
				foundUnits.remove(unit);
			}

			unitsInRange.addAll(foundUnits);
			iterationCounter++;
		}
		return unitsInRange;
	}

	/**
	 * Function for retrieving the Unit with the greatest sum of strengths
	 * around the units TilePosition.
	 * 
	 * @param units
	 *            a HashSet containing all units which are going to be cycled
	 *            through.
	 * @param goapUnit
	 *            the currently executing IGoapUnit.
	 * @return the Unit with the greatest sum of strengths at its TilePosition.
	 */
	public static Unit getUnitWithGreatestTileStrengths(HashSet<Unit> units, IGoapUnit goapUnit) {
		Unit bestUnit = null;
		int bestUnitStrengthTotal = 0;

		// Iterate over the Units and over their TilePositions in a specific
		// radius.
		for (Unit unit : units) {
			int currentStrengths = 0;

			for (int i = -TILE_RADIUS_AROUND_UNITS_SEARCH; i <= TILE_RADIUS_AROUND_UNITS_SEARCH; i++) {
				for (int j = -TILE_RADIUS_AROUND_UNITS_SEARCH; j <= TILE_RADIUS_AROUND_UNITS_SEARCH; j++) {

					// TODO: Possible Change: AirStrength Implementation
					Integer value = ((PlayerUnit) goapUnit).getInformationStorage().getTrackerInfo()
							.getPlayerGroundAttackTilePositions().get(new TilePosition(
									unit.getTilePosition().getX() + i, unit.getTilePosition().getY() + j));

					if (value != null) {
						currentStrengths += value;
					}
				}
			}

			if (bestUnit == null || currentStrengths > bestUnitStrengthTotal) {
				bestUnit = unit;
				bestUnitStrengthTotal = currentStrengths;
			}
		}
		return bestUnit;
	}

	/**
	 * Function for finding the closest Unit in an iterable collection.
	 * 
	 * @param units
	 *            the collection that is going to be searched.
	 * @param targetUnit
	 *            the reference Unit to which the closest Unit from the
	 *            collection is returned later on.
	 * @return the closest Unit to the given reference Unit.
	 */
	public static Unit getClosestUnit(Iterable<Unit> units, Unit targetUnit) {
		Unit closestUnit = null;

		for (Unit unit : units) {
			if (closestUnit == null || unit.getDistance(targetUnit) < closestUnit.getDistance(targetUnit)) {
				closestUnit = unit;
			}
		}
		return closestUnit;
	}

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return this.target;
	}
}
