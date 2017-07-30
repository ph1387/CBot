package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import bwapi.Color;
import bwapi.Pair;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import core.Core;
import core.Display;
import informationStorage.InformationStorage;
import javaGOAP.GoapUnit;
import javaGOAP.GoapAction;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.updater.Updater;

/**
 * PlayerUnit.java --- Wrapper for a player unit. All Player Units derive from
 * this.
 * 
 * @author P H - 20.02.2017
 *
 */
public abstract class PlayerUnit extends GoapUnit {

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
	protected int extraConfidencePixelRangeToClosestUnits = 32;
	protected double confidenceDefault = 0.75;

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
	public ConfidenceState currentConfidenceState = ConfidenceState.UNDER_THRESHOLD;

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

		// Only update the following information if an enemy is in the
		// confidence range.
		// -> FPS boost!
		if (this.closestEnemyUnitInConfidenceRange != null) {
			this.updateConfidence();
			this.updateConfidenceState();
			this.updateCurrentRangeState();
		}
	}

	/**
	 * Function for updating the confidence of the Unit which determines if it
	 * attacks an enemy Unit / building, retreats to another player Unit or
	 * takes a completely different action. <br>
	 * <b>Note:</b> Some classes might override the method. Whenever changes are
	 * made at this point make sure to apply them to all subclasses as well.
	 */
	protected void updateConfidence() {
		Pair<Double, Double> playerEnemyStrengths = this.generatePlayerAndEnemyStrengths();
		double playerStrengthTotal = playerEnemyStrengths.first;
		double enemyStrengthTotal = playerEnemyStrengths.second;
		// TODO: Possible Change: Change the way the life offset is calculated.
		// Calculate the offset of the confidence based on the current Units
		// health.
		double lifeConfidenceMultiplicator = (double) (this.unit.getHitPoints())
				/ (double) (this.unit.getType().maxHitPoints());

		// Has to be set for following equation
		if (enemyStrengthTotal == 0.) {
			enemyStrengthTotal = 1.;
		}

		// TODO: Possible Change: AirWeapon Implementation
		// Allow kiting if the PlayerUnit is outside of the other Unit's attack
		// range. Also this allows Units to further attack and not running
		// around aimlessly when they are on low health.
		// -> PlayerUnit in range of enemy Unit + extra
		if (this.closestEnemyUnitInConfidenceRange.getType().groundWeapon().maxRange()
				+ this.extraConfidencePixelRangeToClosestUnits >= this.getUnit()
						.getDistance(this.closestEnemyUnitInConfidenceRange)) {
			this.confidence = (playerStrengthTotal / enemyStrengthTotal) * lifeConfidenceMultiplicator
					* this.confidenceDefault;
		}
		// -> PlayerUnit out of range of the enemy Unit
		else {
			this.confidence = (playerStrengthTotal / enemyStrengthTotal);
		}
	}

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

	/**
	 * Function for extracting the closest Unit relative to this Unit from a
	 * given HashSet.
	 * 
	 * @param set
	 *            the HashSet which is going to be searched.
	 * @return the closest Unit from the given HashSet or null if the HashSet is
	 *         empty.
	 */
	protected Unit getClosestUnit(HashSet<Unit> set) {
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

	protected abstract StateFactory createFactory();

	public boolean isConfidenceBelowThreshold() {
		return this.confidence < CONFIDENCE_THRESHHOLD;
	}

	public boolean isConfidenceAboveThreshold() {
		return this.confidence >= CONFIDENCE_THRESHHOLD;
	}

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return this.unit;
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
