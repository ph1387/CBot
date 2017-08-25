package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapiMath.Point;
import bwta.BWTA;
import bwta.BaseLocation;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.GoapUnit;
import javaGOAP.GoapAction;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.actions.executableActions.RetreatUnit;
import unitControlModule.stateFactories.updater.Updater;

/**
 * PlayerUnit.java --- Wrapper for a player unit. All Player Units derive from
 * this.
 * 
 * @author P H - 20.02.2017
 *
 */
public abstract class PlayerUnit extends GoapUnit implements RetreatUnit {

	// The timer after a BaseLocation might be searched again.
	public static final int BASELOCATIONS_TIME_PASSED = 60;
	// TODO: Possible Change: Reevaluate the importance of Units choosing their
	// own parameters
	protected static final double CONFIDENCE_THRESHHOLD = 0.7;
	protected static final Integer DEFAULT_TILE_SEARCH_RADIUS = 2;
	private static final int CONFIDENCE_TILE_RADIUS = 15;

	protected static HashMap<BaseLocation, Integer> BaselocationsSearched = new HashMap<>();

	// Information preserver which holds all important information
	protected InformationStorage informationStorage;

	protected Unit unit;
	protected Unit closestEnemyUnitInConfidenceRange;
	protected double confidence = 1.;

	// Extra distance that will be added to the enemy when determining if the
	// Unit should retreat or not.
	protected int extraConfidencePixelRangeToClosestUnits = 32;
	protected double confidenceDefault = 0.75;

	// Properties used for modifying a generated confidence:
	// TODO: UML ADD
	// The distance at which the center range confidence multiplier activates.
	private int maxCenterPixelDistanceConfidenceBoost = 320;
	// TODO: UML ADD
	private double confidenceMultiplierSingleCenter = 2.5;
	// TODO: UML ADD
	private double confidenceMultiplierInMaxCenterDistance = 1.5;

	// Factories and Objects needed for an accurate representation of the Units
	// capabilities.
	private StateFactory stateFactory;
	private Updater worldStateUpdater;
	private Updater goalStateUpdater;
	private Updater actionUpdater;

	// Enums are accessed by the Updater -> public
	public enum UnitStates {
		ENEMY_MISSING, ENEMY_KNOWN
	}

	public enum ConfidenceRangeStates {
		NO_UNIT_IN_RANGE, UNIT_IN_RANGE
	}

	public enum ConfidenceState {
		UNDER_THRESHOLD, ABOVE_THRESHOLD
	}

	public UnitStates currentState = UnitStates.ENEMY_MISSING;
	public ConfidenceRangeStates currentRangeState = ConfidenceRangeStates.NO_UNIT_IN_RANGE;
	public ConfidenceState currentConfidenceState = ConfidenceState.ABOVE_THRESHOLD;

	/**
	 * Flag for enabling / disabling hollow updates. If this flag is set,
	 * calling the update function has no effect. This can be used if the
	 * GoapAgent's update function has to be called twice for the underlying FSM
	 * to work properly and not wanting to create unnecessary CPU overhead by
	 * doubling the amount of performed calculations.
	 */
	private boolean hollowUpdatesEnabled = false;

	// Listeners for removing the corresponding Agent from the collection of
	// active Agents.
	private List<Object> agentRemoveListeners = new ArrayList<Object>();

	/**
	 * @param unit
	 *            the unit the class wraps around.
	 */
	public PlayerUnit(Unit unit, InformationStorage informationStorage) {
		this.unit = unit;
		this.informationStorage = informationStorage;

		this.stateFactory = this.createFactory();
		this.worldStateUpdater = this.stateFactory.getMatchingWorldStateUpdater(this);
		this.goalStateUpdater = this.stateFactory.getMatchingGoalStateUpdater(this);
		this.actionUpdater = this.stateFactory.getMatchingActionUpdater(this);

		this.setWorldState(this.stateFactory.generateWorldState());
		this.setGoalState(this.stateFactory.generateGoalState());
		this.setAvailableActions(this.stateFactory.generateAvailableActions());

		// Set default values in the beginning.
		if (BaselocationsSearched.size() == 0) {
			for (BaseLocation location : BWTA.getBaseLocations()) {
				BaselocationsSearched.put(location, 0);
			}
		}
	}

	// -------------------- Functions

	@Override
	public void goapPlanFound(Queue<GoapAction> actions) {

	}

	@Override
	public void goapPlanFailed(Queue<GoapAction> actions) {

	}

	@Override
	public void goapPlanFinished() {

	}

	@Override
	public void update() {
		// Only update the PlayerUnit if the flag is not set.
		if (!this.hollowUpdatesEnabled) {
			// FSM worldState changes in one cycle.
			if (this.currentState == UnitStates.ENEMY_MISSING
					&& (this.informationStorage.getTrackerInfo().getEnemyUnits().size() != 0
							|| this.informationStorage.getTrackerInfo().getEnemyBuildings().size() != 0)) {
				this.resetActions();
				this.currentState = UnitStates.ENEMY_KNOWN;
			}
			if (this.currentState == UnitStates.ENEMY_KNOWN) {
				if (this.informationStorage.getTrackerInfo().getEnemyUnits().size() == 0
						&& this.informationStorage.getTrackerInfo().getEnemyBuildings().size() == 0) {
					this.resetActions();
					this.currentState = UnitStates.ENEMY_MISSING;
				} else {
					this.actOnUnitsKnown();
				}
			}

			try {
				this.worldStateUpdater.update(this);
				this.goalStateUpdater.update(this);
				this.actionUpdater.update(this);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.updateBaseLocationsSearched();
		}
	}

	/**
	 * Function for acting on the fact that enemy units (units and buildings)
	 * are known of.
	 */
	protected void actOnUnitsKnown() {
		this.closestEnemyUnitInConfidenceRange = this.getClosestUnit(this.getAllEnemyUnitsInConfidenceRange());

		// Always update the confidence and the states and not only when an
		// enemy is in confidence range since this would cause the Unit to
		// simply stay at one Position and not move. This effect is due to the
		// confidence not being updated and therefore the threshold not changing
		// which causes certain actions to not finish (IsDone() i.e. returns
		// false when it should not).
		try {
			// Either generate a modified confidence value or a "normal" one.
			if (this.informationStorage.getiPlayerUnitConfig().enableModifiedConfidenceGeneration()) {
				this.confidence = this.generateModifiedConfidence();
			} else {
				this.confidence = this.generateConfidence();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.updateConfidenceState();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			this.updateCurrentRangeState();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// TODO: UML ADD
	/**
	 * Function for generating a modified version of the standard confidence.
	 * This function utilizes the {@link #generateConfidence()} method that is
	 * implemented by the Subclasses and applies a modifier to it that is based
	 * on the distance of the Unit to the nearest (center) building/-s. Using
	 * this method it is possible for the Unit to react to it's defensive
	 * surroundings, either defending them (centers) or fighting near them
	 * (bunkers).
	 * 
	 * @return the modified confidence of the Unit that takes various other
	 *         factors into account like the distance to certain buildings and
	 *         locations.
	 */
	private double generateModifiedConfidence() {
		Integer closestCenterDistance = this.extractClosestCenterDistance();
		double modifiedConfidence = this.generateConfidence();

		// If the Unit is near a center building apply a buff to the confidence
		// to it because they MUST be defended!.
		if (closestCenterDistance != null && closestCenterDistance <= this.maxCenterPixelDistanceConfidenceBoost) {
			// If only one center remains, fight with maximum force. This
			// ensures that no Units run away when the last remaining center is
			// attacked.
			// NOTE:
			// Helps to defend against rushes!
			if (this.informationStorage.getCurrentGameInformation().getCurrentUnitCounts()
					.get(Core.getInstance().getPlayer().getRace().getCenter()).equals(1)) {
				modifiedConfidence *= this.confidenceMultiplierSingleCenter;
			} else {
				modifiedConfidence *= this.confidenceMultiplierInMaxCenterDistance;
			}
		}
		// Otherwise if no distance could be calculated go all in and attack
		// with all force possible since no center remains. This probably means
		// that the enemy is right inside the base and MUST be destroyed since
		// this is the only possible way to maybe win.
		else if (closestCenterDistance == null) {
			modifiedConfidence = Double.MAX_VALUE;
		}

		return modifiedConfidence;
	}

	/**
	 * Function for generating the confidence of the Unit which determines if it
	 * attacks an enemy Unit / building, retreats to another player Unit or
	 * takes a completely different action.
	 * 
	 * @return the base confidence of the Unit.
	 */
	protected abstract double generateConfidence();

	/**
	 * Used to determine the strength of the PlayerUnits and the enemies by
	 * summing up their representative TileValues.
	 * 
	 * @return the strength of the Player and the enemy in the PlayerUnit's
	 *         confidence range.
	 */
	protected Pair<Double, Double> generatePlayerAndEnemyStrengths() {
		List<Integer> enemyStrengths = new ArrayList<Integer>();
		List<Integer> playerStrengths = new ArrayList<Integer>();

		// TODO: Possible Change: AirStrength Implementation
		// Sum the total strength of the player and the enemy in a given radius
		// around the unit.
		for (int i = -CONFIDENCE_TILE_RADIUS; i <= CONFIDENCE_TILE_RADIUS; i++) {
			for (int j = -CONFIDENCE_TILE_RADIUS; j <= CONFIDENCE_TILE_RADIUS; j++) {
				TilePosition key = new TilePosition(this.unit.getTilePosition().getX() + i,
						this.unit.getTilePosition().getY() + j);
				int eStrength = this.informationStorage.getTrackerInfo().getEnemyGroundAttackTilePositions()
						.getOrDefault(key, 0);
				int pStrength = this.informationStorage.getTrackerInfo().getPlayerGroundAttackTilePositions()
						.getOrDefault(key, 0);

				if (eStrength != 0) {
					enemyStrengths.add(eStrength);
				}
				if (pStrength != 0) {
					playerStrengths.add(pStrength);
				}
			}
		}

		return new Pair<Double, Double>((double) getSum(playerStrengths), (double) getSum(enemyStrengths));
	}

	/**
	 * Mostly used to reset the action Stack if the current confidence of the
	 * PlayerUnit decreases too much.This ensures, that the Unit is retreating
	 * when the tides of the battle turn in a unfavorable position.
	 */
	protected void updateConfidenceState() {
		if (this.currentConfidenceState == ConfidenceState.UNDER_THRESHOLD
				&& this.confidence >= CONFIDENCE_THRESHHOLD) {
			this.currentConfidenceState = ConfidenceState.ABOVE_THRESHOLD;
			this.resetActions();
		} else if (this.currentConfidenceState == ConfidenceState.ABOVE_THRESHOLD
				&& this.confidence < CONFIDENCE_THRESHHOLD) {
			this.currentConfidenceState = ConfidenceState.UNDER_THRESHOLD;
			this.resetActions();
		}
	}

	/**
	 * Function for updating the currentRangeState of the Unit. Is separated
	 * from {@link #actOnUnitsKnown()} due to some Units needing to change the
	 * implementation.
	 */
	protected void updateCurrentRangeState() {
		// No "else if" to perform change in one cycle if an enemy Unit is in
		// range.
		if (this.currentRangeState == ConfidenceRangeStates.NO_UNIT_IN_RANGE
				&& this.closestEnemyUnitInConfidenceRange != null) {
			this.currentRangeState = ConfidenceRangeStates.UNIT_IN_RANGE;
			this.resetActions();
		}
		if (this.currentRangeState == ConfidenceRangeStates.UNIT_IN_RANGE
				&& this.closestEnemyUnitInConfidenceRange == null) {
			this.currentRangeState = ConfidenceRangeStates.NO_UNIT_IN_RANGE;
			this.resetActions();
		}
	}

	/**
	 * Update the searched BaseLocations if the unit is in the range of one of
	 * them.
	 */
	protected void updateBaseLocationsSearched() {
		for (BaseLocation location : BWTA.getBaseLocations()) {
			if (this.isNearTilePosition(location.getRegion().getCenter().toTilePosition(), null)) {
				BaselocationsSearched.put(location, Core.getInstance().getGame().elapsedTime());
			}
		}
	}

	@Override
	public boolean moveTo(Object target) {
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
	 * Test if a Unit is near another TilePosition
	 * 
	 * @param targetTilePosition
	 *            the TilePosition the Units Position is being checked against.
	 * @param tileRadius
	 *            the radius around the target in which the function returns
	 *            true.
	 * @return true or false depending if the Unit is in the radius around the
	 *         TilePosition.
	 */
	public boolean isNearTilePosition(TilePosition targetTilePosition, Integer tileRadius) {
		int targetX = targetTilePosition.getX();
		int targetY = targetTilePosition.getY();
		int unitX = this.unit.getTilePosition().getX();
		int unitY = this.unit.getTilePosition().getY();

		if (tileRadius == null) {
			tileRadius = DEFAULT_TILE_SEARCH_RADIUS;
		}

		return (unitX >= targetX - tileRadius && unitX <= targetX + tileRadius && unitY >= targetY - tileRadius
				&& unitY <= targetY + tileRadius);
	}

	/**
	 * Test if a Unit is near another Position.
	 * 
	 * @param targetPosition
	 *            the Position the Units Position is being checked against.
	 * @param radius
	 *            the radius around the target in which the function returns
	 *            true.
	 * @return true or false depending if the Unit is in the radius around the
	 *         Position.
	 */
	public boolean isNearPosition(Position targetPosition, Integer radius) {
		if (radius == null) {
			radius = DEFAULT_TILE_SEARCH_RADIUS * Core.getInstance().getTileSize();
		}

		return this.unit.getDistance(targetPosition) <= radius;
	}

	/**
	 * Used for getting the sum of all elements inside an iterable collection.
	 * 
	 * @param set
	 *            the iterable collection the sum is calculated of.
	 * @return the sum of all elements inside the given iterable collection.
	 */
	public static int getSum(Iterable<Integer> list) {
		int sum = 0;

		for (Integer integer : list) {
			sum += integer;
		}
		return sum;
	}

	// TODO: UML CHANGE VISIBILITY
	/**
	 * Function for extracting the closest Unit relative to this Unit from a
	 * given HashSet.
	 * 
	 * @param set
	 *            the HashSet which is going to be searched.
	 * @return the closest Unit from the given HashSet or null if the HashSet is
	 *         empty.
	 */
	public Unit getClosestUnit(HashSet<Unit> set) {
		Unit closestUnit = null;

		for (Unit setUnit : set) {
			if (closestUnit == null || this.unit.getDistance(setUnit.getPosition()) < this.unit
					.getDistance(closestUnit.getPosition())) {
				closestUnit = setUnit;
			}
		}
		return closestUnit;
	}

	/**
	 * Function for retrieving a HashSet of all units in weapon range, both on
	 * the ground and in the air.
	 * 
	 * @return a HashSet of all enemy units in the weapon range of this unit.
	 */
	public HashSet<Unit> getAllEnemyUnitsInWeaponRange() {
		return this.getAllEnemyUnitsInRange(
				Math.max(this.unit.getType().groundWeapon().maxRange(), this.unit.getType().airWeapon().maxRange()));
	}

	/**
	 * Function for retrieving a HashSet of all enemy Units in the confidence
	 * range of this Unit.
	 * 
	 * @return a HashSet of all enemy Units in the confidence range.
	 */
	public HashSet<Unit> getAllEnemyUnitsInConfidenceRange() {
		return this.getAllEnemyUnitsInRange(CONFIDENCE_TILE_RADIUS * Core.getInstance().getTileSize());
	}

	/**
	 * Function for retrieving a HashSet of all player Units in the confidence
	 * range of this Unit.
	 * 
	 * @return a HashSet of all player Units in the confidence range.
	 */
	public HashSet<Unit> getAllPlayerUnitsInConfidenceRange() {
		return this.getAllPlayerUnitsInRange(CONFIDENCE_TILE_RADIUS * Core.getInstance().getTileSize());
	}

	/**
	 * Function for retrieving a HashSet of all enemy units in a specific range
	 * around the Unit.
	 * 
	 * @param pixelRange
	 *            the range of the search in pixels.
	 * @return a HashSet of all enemy units in the given range of this unit.
	 */
	public HashSet<Unit> getAllEnemyUnitsInRange(int pixelRange) {
		HashSet<Unit> enemyUnits = new HashSet<Unit>();

		for (Unit unit : this.unit.getUnitsInRadius(pixelRange)) {
			if (unit.getPlayer() == Core.getInstance().getGame().enemy()) {
				enemyUnits.add(unit);
			}
		}
		return enemyUnits;
	}

	/**
	 * Function for retrieving a HashSet of all player units in a specific range
	 * around the Unit.
	 * 
	 * @param pixelRange
	 *            the range of the search in pixels.
	 * @return a HashSet of all player units in the given range of this unit.
	 */
	public HashSet<Unit> getAllPlayerUnitsInRange(int pixelRange) {
		HashSet<Unit> playerUnits = new HashSet<Unit>();

		for (Unit unit : this.unit.getUnitsInRadius(pixelRange)) {
			if (unit.getPlayer() == Core.getInstance().getGame().self()) {
				playerUnits.add(unit);
			}
		}
		return playerUnits;
	}

	// TODO: UML ADD
	/**
	 * Function for extracting the distance to the closest Player center
	 * building on the map. This function either returns the actual distance
	 * casted to int or null, if no center building is found.
	 * 
	 * @return the smallest distance to the therefore closest center building
	 *         casted to int or null if none is found.
	 */
	public Integer extractClosestCenterDistance() {
		Integer distance = null;
		HashSet<Unit> centers = this.informationStorage.getCurrentGameInformation().getCurrentUnits()
				.get(Core.getInstance().getPlayer().getRace().getCenter());

		if (centers != null) {
			Unit closestCenter = this.getClosestUnit(centers);

			if (closestCenter != null) {
				distance = this.unit.getDistance(closestCenter);
			}
		}
		return distance;
	}

	protected abstract StateFactory createFactory();

	public boolean isConfidenceBelowThreshold() {
		return this.confidence < CONFIDENCE_THRESHHOLD;
	}

	public boolean isConfidenceAboveThreshold() {
		return this.confidence >= CONFIDENCE_THRESHHOLD;
	}

	// -------------------- RetreatUnit

	public Point defineCurrentPosition() {
		return new Point(this.unit.getPosition());
	}

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return this.unit;
	}

	// TODO: UML ADD
	public double getConfidence() {
		return confidence;
	}

	public static HashMap<BaseLocation, Integer> getBaselocationsSearched() {
		return BaselocationsSearched;
	}

	public Unit getClosestEnemyUnitInConfidenceRange() {
		return this.closestEnemyUnitInConfidenceRange;
	}

	public InformationStorage getInformationStorage() {
		return informationStorage;
	}

	public boolean isHollowUpdatesEnabled() {
		return hollowUpdatesEnabled;
	}

	public void setHollowUpdatesEnabled(boolean hollowUpdatesDisabled) {
		this.hollowUpdatesEnabled = hollowUpdatesDisabled;
	}

	// ------------------------------ Events

	public synchronized void addAgentRemoveListener(Object listener) {
		this.agentRemoveListeners.add(listener);
	}

	public synchronized void removeAgentRemoveListener(Object listener) {
		this.agentRemoveListeners.remove(listener);
	}

	protected synchronized void dispatchRemoveAgentEvent() {
		for (Object listener : this.agentRemoveListeners) {
			((RemoveAgentEvent) listener).removeAgent(this);
		}
	}

	/**
	 * Function for triggering the Event for removing the associated Agent.
	 */
	public void removeCorrespondingAgent() {
		this.dispatchRemoveAgentEvent();
	}

}
