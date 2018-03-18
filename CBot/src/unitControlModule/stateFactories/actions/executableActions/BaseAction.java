package unitControlModule.stateFactories.actions.executableActions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import bwapi.Game;
import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Polygon;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import core.CBot;
import core.Core;
import informationStorage.BaseActionSharedInformation;
import informationStorage.DistantRegion;
import informationStorage.MapInformation;
import javaGOAP.GoapAction;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.grouping.GroupActionManager;
import unitControlModule.stateFactories.actions.executableActions.grouping.GroupableAction;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * BaseAction.java --- Superclass for all PlayerUnit actions.
 * 
 * @author P H - 09.02.2017
 *
 */
public abstract class BaseAction extends GoapAction implements GroupableAction {

	// TODO: UML ADD
	/**
	 * SmartlyMovingActionWrapper.java --- Interface for action wrappers using
	 * the
	 * {@link BaseAction#performSmartlyMovingToRegion(IGoapUnit, Region, SmartlyMovingActionWrapper)}
	 * function.
	 * 
	 * @author P H - 17.03.2018
	 *
	 */
	protected interface SmartlyMovingActionWrapper {

		/**
		 * Function for performing the general action of the Unit (Moving,
		 * attacking, etc.).
		 * 
		 * @param goapUnit
		 *            the executing Unit.
		 * @param target
		 *            the forwarded target of the current action.
		 * @return true if the action was executed successfully, otherwise
		 *         false.
		 */
		public boolean performInternalAction(IGoapUnit goapUnit, Object target);

		/**
		 * Function for converting the target into a Position.
		 * 
		 * @param target
		 *            the forwarded target of the current action that must be
		 *            converted.
		 * @return the converted target as Position instance.
		 */
		public Position convertTarget(Object target);

	}

	private static final int EXPAND_MULTIPLIER_MAX = 5;
	private static final int TILE_RADIUS_AROUND_UNITS_SEARCH = 1;
	private static final int DEFAULT_SEARCH_STEP_DISTANCE = 200;
	private static final int DEFAULT_MIN_DISTANCE = 256;
	// TODO: UML ADD
	// The distance towards ChokePoint at which the Region on the other side is
	// used in finding the next ChokePoint towards a target.
	private static final int SKIP_DISTANCE_TO_CHOKEPOINT = 256;

	// Flag for re-enabling the action change trigger when the Unit is the new
	// leader of a group action.
	private boolean wasPrevLeader = false;

	protected boolean actionChangeTrigger = false;
	protected IGoapUnit currentlyExecutingUnit;

	public BaseAction(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected boolean performAction(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		BaseActionSharedInformation baseActionSharedInformation = playerUnit.getInformationStorage()
				.getBaseActionSharedInformation();
		BaseAction storedAction = baseActionSharedInformation.getCurrentlyExecutingActions().get(playerUnit);
		boolean success;

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
		baseActionSharedInformation.getCurrentlyExecutingActions().put(playerUnit, this);
		this.currentlyExecutingUnit = goapUnit;

		// If the action is listed as a grouped action, perform it as such.
		if (this.canPerformGrouped()) {
			success = this.performGroupedAction(goapUnit);
		}
		// Otherwise just perform it normally.
		else {
			success = this.performSpecificAction(goapUnit);
		}

		return success;
	}

	/**
	 * Extracted function for performing an action as a group and not alone. The
	 * Unit that performs the action is either just a simple member of the group
	 * or the leader of one. The former returns always true, since the targets
	 * and other important information are processed by the leader, which the
	 * latter returns the actual result of the action. This is either true, if
	 * the action was successfully performed in a group or false, if something
	 * went wrong.
	 * 
	 * @param goapUnit
	 *            the performing Unit.
	 * @return true or false depending if the Unit was a member (Always true) or
	 *         the leader (True if action succeeded, false if something went
	 *         wrong).
	 */
	private boolean performGroupedAction(IGoapUnit goapUnit) {
		GroupActionManager groupActionManager = ((PlayerUnit) goapUnit).getInformationStorage()
				.getBaseActionSharedInformation().getGroupActionManager();
		boolean success;

		if (!groupActionManager.isGrouped(goapUnit)) {
			groupActionManager.addToGroupAction(this.getClass(), goapUnit, this);
		}

		// Re enable the action change trigger for actions that rely on that
		// information that might be now performed if the Unit was not the
		// previous leader and is it now.
		if (!this.wasPrevLeader && groupActionManager.isLeader(goapUnit)) {
			this.wasPrevLeader = true;
			this.actionChangeTrigger = true;
		}

		// Only perform the action if the Unit is the leader of the group. The
		// other members of the group act passively.
		if (groupActionManager.isLeader(goapUnit)) {
			// First perform the action for the leader, then for the other
			// members of the group. This allows the members to use already
			// calculated results.
			success = this.performSpecificAction(goapUnit);

			if (success) {
				success &= groupActionManager.performGroupLeaderAction(goapUnit);
			}
		} else {
			success = true;
		}

		return success;
	}

	/**
	 * The actual performance of the action.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @return true if the action was successful, false if it failed.
	 */
	protected abstract boolean performSpecificAction(IGoapUnit goapUnit);

	/**
	 * Function used for resetting the entry in the currentlyExecutingActions
	 * HashMap. This function has to be called when the GoapAction finishes so
	 * that the actionTrigger is going to be enabled in the next iteration.
	 */
	protected void resetStoredAction() {
		if (this.currentlyExecutingUnit != null) {
			((PlayerUnit) this.currentlyExecutingUnit).getInformationStorage().getBaseActionSharedInformation()
					.getCurrentlyExecutingActions().put((PlayerUnit) this.currentlyExecutingUnit, null);
		}
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

		// Remove any stored group action references.
		if (this.currentlyExecutingUnit != null) {
			((PlayerUnit) this.currentlyExecutingUnit).getInformationStorage().getBaseActionSharedInformation()
					.getGroupActionManager().removeFromGroupAction(this.currentlyExecutingUnit);
		}
		// Reset the group leader flag.
		this.wasPrevLeader = false;
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
		return getPlayerUnitsInIncreasingRange(playerUnit, DEFAULT_MIN_DISTANCE, DEFAULT_SEARCH_STEP_DISTANCE, null);
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
		return getPlayerUnitsInIncreasingRange(playerUnit, DEFAULT_MIN_DISTANCE, stepDistance, null);
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
		return getPlayerUnitsInIncreasingRange(playerUnit, DEFAULT_MIN_DISTANCE, stepDistance, condition);
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
		return getPlayerUnitsInIncreasingRange(playerUnit, DEFAULT_MIN_DISTANCE, DEFAULT_SEARCH_STEP_DISTANCE,
				condition);
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

	// TODO: UML ADD
	/**
	 * Function for performing the action smartly, whereas smartly means moving
	 * from ChokePoint to ChokePoint towards the target. This is due to the
	 * Units sometimes getting stuck in Regions with either blocking minerals or
	 * neutral structures. Therefore moving between ChokePoints is advised since
	 * the used access order (= breadth-search) includes these. ChokePoints are
	 * traversed until either the target Region matches the current one of the
	 * Unit or is one of the directly accessible ones of the current Region.
	 * 
	 * @param goapUnit
	 *            the executing Unit.
	 * @param targetRegion
	 *            the target Region of the Unit.
	 * @param actionWrapper
	 *            the SmartlyMovingActionWrapper instance that contains
	 *            converter functionalities as well as the concrete action that
	 *            the Unit must take.
	 * @return true if the action was performed successfully, which means either
	 *         moving between ChokePoints or executing the actual action.
	 * @throws Exception
	 *             a Exception is thrown (Usually NullPointerException) when a
	 *             Region can not be found via BWTA and therefore no region
	 *             access order is available.
	 */
	protected boolean performSmartlyMovingToRegion(IGoapUnit goapUnit, Region targetRegion,
			SmartlyMovingActionWrapper actionWrapper) throws Exception {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		Region currentRegion = BWTA.getRegion(playerUnit.getUnit().getPosition());
		HashMap<Region, HashSet<Region>> regionAccessOrder = playerUnit.getInformationStorage().getMapInfo()
				.getPrecomputedRegionAcccessOrders().get(currentRegion);
		boolean success = false;

		// Target Region either is the current one or only one step away.
		if (targetRegion.equals(currentRegion)
				|| regionAccessOrder.getOrDefault(currentRegion, new HashSet<Region>()).contains(targetRegion)) {
			success = actionWrapper.performInternalAction(goapUnit, this.target);
		}
		// Target Region is farther away.
		else {
			MapInformation mapInformation = playerUnit.getInformationStorage().getMapInfo();
			Position currentPosition = playerUnit.getUnit().getPosition();
			Position targetPosition = actionWrapper.convertTarget(this.target);
			Chokepoint chokePointToMoveTo = this.findNextChokePointTowardsTarget(mapInformation, currentPosition,
					targetPosition);

			success = playerUnit.getUnit().move(chokePointToMoveTo.getCenter());
		}

		return success;
	}

	// TODO: UML ADD
	/**
	 * Function for finding the next ChokePoint to which a Unit must move in
	 * order to get closer to a given target Position. Since an executing Unit
	 * traverses ChokePoints a check is being performed at which it's different
	 * Regions are used as starting Region.
	 * 
	 * @param mapInformation
	 *            MapInformation instance that holds all necessary breadth
	 *            access orders that are going to be used.
	 * @param currentPosition
	 *            the current Position of the executing Unit.
	 * @param targetPosition
	 *            the target Position of the action.
	 * @return the next ChokePoint that must be traveled to in order to get
	 *         closer towards the target Position, or null if none can be
	 *         generated / found.
	 */
	protected Chokepoint findNextChokePointTowardsTarget(MapInformation mapInformation, Position currentPosition,
			Position targetPosition) {
		Region targetRegion = BWTA.getRegion(targetPosition);
		Region currentRegion = BWTA.getRegion(currentPosition);
		HashMap<Region, Region> reversedRegionAccessOrder = mapInformation.getPrecomputedReversedRegionAccessOrders()
				.get(currentRegion);
		Chokepoint nextChokePoint = null;

		try {
			nextChokePoint = this.findNextChokePointTowardsTarget(reversedRegionAccessOrder, currentRegion,
					targetRegion);

			if (nextChokePoint.getCenter().getDistance(currentPosition) < SKIP_DISTANCE_TO_CHOKEPOINT) {
				// Get the Region on the other side of the ChokePoint and
				// calculate again. This effectively prevents the Unit from
				// getting stuck on a single ChokePoint and not moving any
				// further.
				if (nextChokePoint.getRegions().first.equals(currentRegion)) {
					currentRegion = nextChokePoint.getRegions().second;
				} else {
					currentRegion = nextChokePoint.getRegions().first;
				}

				reversedRegionAccessOrder = mapInformation.getPrecomputedReversedRegionAccessOrders()
						.get(currentRegion);
				nextChokePoint = this.findNextChokePointTowardsTarget(reversedRegionAccessOrder, currentRegion,
						targetRegion);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nextChokePoint;
	}

	// TODO: UML ADD
	/**
	 * Internally used function for extracting the actual ChokePoint to which a
	 * Unit must move in order to get closer to a given target Position.
	 * 
	 * @param reversedRegionAccessOrder
	 *            the reversed access order for the Regions based on the current
	 *            Region.
	 * @param currentRegion
	 *            the current Region the Unit is in. Must be the one the
	 *            reversed Region access order is based on.
	 * @param targetRegion
	 *            the target Region the Unit is trying to move to.
	 * @return the next ChokePoint the Unit must move to in order to get closer
	 *         towards the given target Region or null if no ChokePoint can be
	 *         generated / found.
	 */
	private Chokepoint findNextChokePointTowardsTarget(HashMap<Region, Region> reversedRegionAccessOrder,
			Region currentRegion, Region targetRegion) {
		Region nextRegion = extractNextRegionTowardsTargetRegion(reversedRegionAccessOrder, targetRegion,
				currentRegion);
		Chokepoint nextChokePoint = extractSharedChokePoint(currentRegion, nextRegion);

		return nextChokePoint;
	}

	// TODO: UML ADD
	/**
	 * Function for extracting the farthest Region instance from a HashSet of
	 * given DistantRegions.
	 * 
	 * @param regionDistances
	 *            the DistantRegion instances from which the farthest one is
	 *            being extracted.
	 * @return the farthest Region from the provided HashSet of DistantRegions.
	 */
	public static DistantRegion extractFarthestRegion(HashSet<DistantRegion> regionDistances) {
		return new TreeSet<DistantRegion>(regionDistances).last();
	}

	// TODO: UML ADD
	/**
	 * Function for extracting the Region that leads towards a provided target
	 * Region based on a starting Region.
	 * 
	 * @param reversedRegionAccessOrder
	 *            the reversed Region access order which is based on the
	 *            provided starting Region.
	 * @param targetRegion
	 *            the target Region which the returned one must lead to.
	 * @param startRegion
	 *            the starting Region on which the reversed Region access order
	 *            is based upon. The Region returned is one accessible by this
	 *            one.
	 * @return the next Region accessible by the provided starting one that
	 *         leads towards the target Region.
	 */
	public static Region extractNextRegionTowardsTargetRegion(HashMap<Region, Region> reversedRegionAccessOrder,
			Region targetRegion, Region startRegion) {
		Region currentRegion = targetRegion;

		// Iterate until the NEXT Region is the start one.
		// -> Current one is the one leading towards the target Region.
		while (reversedRegionAccessOrder.get(currentRegion) != startRegion) {
			currentRegion = reversedRegionAccessOrder.get(currentRegion);
		}

		return currentRegion;
	}

	// TODO: UML ADD
	/**
	 * Function for extracting the ChokePoint between two Regions.
	 * 
	 * @param regionOne
	 *            the first Region.
	 * @param regionTwo
	 *            the second Region.
	 * @return the ChokePoint between (= shared) the provided Regions or null,
	 *         if the Regions do not share one.
	 */
	public static Chokepoint extractSharedChokePoint(Region regionOne, Region regionTwo) {
		HashSet<Chokepoint> providedRegionsChokePoints = new HashSet<>(regionOne.getChokepoints());
		Chokepoint sharedChokePoint = null;

		for (Chokepoint chokepoint : regionTwo.getChokepoints()) {
			if (providedRegionsChokePoints.contains(chokepoint)) {
				sharedChokePoint = chokepoint;

				break;
			}
		}

		return sharedChokePoint;
	}

	// -------------------- Group

	/**
	 * Function for removing any associated groupings regarding a single
	 * IGoapUnit. This is necessary since the actions can / must not detect
	 * themselves if the Unit exists / is alive.
	 * 
	 * @param goapUnit
	 *            the Unit whose groupings are being removed.
	 */
	public static void removeGroupAssociations(IGoapUnit goapUnit) {
		((PlayerUnit) goapUnit).getInformationStorage().getBaseActionSharedInformation().getGroupActionManager()
				.removeFromGroupAction(goapUnit);
	}

	// ------------------------------ Getter / Setter

	public void setTarget(Object target) {
		this.target = target;
	}

	public Object getTarget() {
		return this.target;
	}
}
