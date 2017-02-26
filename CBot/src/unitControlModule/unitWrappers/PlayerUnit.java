package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import core.Display;
import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.actions.executableActions.AttackMoveAction;
import unitControlModule.stateFactories.actions.executableActions.AttackUnitAction;
import unitControlModule.stateFactories.actions.executableActions.RetreatFromNearestUnitAction;
import unitControlModule.stateFactories.actions.executableActions.ScoutBaseLocationAction;
import unitControlModule.stateFactories.updater.Updater;
import unitTrackerModule.EnemyUnit;
import unitTrackerModule.UnitTrackerModule;

/**
 * PlayerUnit.java --- Wrapper for a player unit.
 * 
 * @author P H - 20.02.2017
 *
 */
public abstract class PlayerUnit extends GoapUnit {

	public static Hashtable<BaseLocation, Integer> BASELOCATIONS_SEARCHED = new Hashtable<>();
	public static final int BASELOCATIONS_TIME_PASSED = 180;
	public static final int CONFIDENCE_TILE_RADIUS = 15;
	public static double CONFIDENCE_THRESHHOLD = 0.7;
	protected static final Integer DEFAULT_SEARCH_RADIUS = 5;

	protected Unit unit;
	public Unit nearestEnemyUnitInSight;
	public Unit nearestEnemyUnitInConfidenceRange;
	public double confidence = 1.;

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

	public UnitStates currentState = UnitStates.ENEMY_MISSING;
	public ConfidenceRangeStates currentRangeState = ConfidenceRangeStates.NO_UNIT_IN_RANGE;

	/**
	 * @param unit
	 *            the unit the class wraps around.
	 */
	public PlayerUnit(Unit unit) {
		this.unit = unit;
		this.stateFactory = this.createFactory();
		this.worldStateUpdater = this.stateFactory.getMatchingWorldStateUpdater(this);
		this.goalStateUpdater = this.stateFactory.getMatchingGoalStateUpdater(this);
		this.actionUpdater = this.stateFactory.getMatchingActionUpdater(this);

		this.setWorldState(this.stateFactory.generateWorldState());
		this.setGoalState(this.stateFactory.generateGoalState());
		this.setAvailableActions(this.stateFactory.generateAvailableActions());

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
		// FSM worldState changes in one cycle.
		if (this.currentState == UnitStates.ENEMY_MISSING && (UnitTrackerModule.getInstance().enemyUnits.size() != 0
				|| UnitTrackerModule.getInstance().enemyBuildings.size() != 0)) {
			this.resetActions();
			this.currentState = UnitStates.ENEMY_KNOWN;
		}
		if (this.currentState == UnitStates.ENEMY_KNOWN) {
			if (UnitTrackerModule.getInstance().enemyUnits.size() == 0
					&& UnitTrackerModule.getInstance().enemyBuildings.size() == 0) {
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

	/**
	 * Function for acting on the fact that enemy units (units and buildings)
	 * are known of.
	 */
	protected void actOnUnitsKnown() {
		this.nearestEnemyUnitInSight = this
				.getClosestUnit(this.getAllEnemyUnitsInRange(this.unit.getType().sightRange()));
		this.nearestEnemyUnitInConfidenceRange = this.getClosestUnit(this.getAllEnemyUnitsInConfidenceRange());

		this.updateConfidence();
		this.updateCurrentRangeState();
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
				&& this.nearestEnemyUnitInConfidenceRange != null) {
			this.currentRangeState = ConfidenceRangeStates.UNIT_IN_RANGE;
			this.resetActions();
		}
		if (this.currentRangeState == ConfidenceRangeStates.UNIT_IN_RANGE) {
			if (this.nearestEnemyUnitInConfidenceRange == null) {
				this.currentRangeState = ConfidenceRangeStates.NO_UNIT_IN_RANGE;
				this.resetActions();
			} else {

				// TODO: DEBUG INFO
				// Nearest enemy Unit display.
				Display.drawTileFilled(Core.getInstance().getGame(),
						this.nearestEnemyUnitInConfidenceRange.getTilePosition().getX(),
						this.nearestEnemyUnitInConfidenceRange.getTilePosition().getY(), 1, 1, new Color(0, 0, 255));

			}
		}
	}

	/**
	 * Update the searched BaseLocations if the unit is in the range of one of
	 * them.
	 */
	protected void updateBaseLocationsSearched() {
		for (BaseLocation location : BWTA.getBaseLocations()) {
			if (this.isNear(location.getRegion().getCenter().toTilePosition(), null)) {
				BASELOCATIONS_SEARCHED.put(location, Core.getInstance().getGame().elapsedTime());
			}
		}
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
	 * Function for updating the confidence of the Unit which determines it
	 * attacks an enemy Unit / building or retreats to another player Unit.
	 */
	protected void updateConfidence() {
		HashSet<Integer> enemyStrengths = new HashSet<Integer>();
		HashSet<Integer> playerStrengths = new HashSet<Integer>();
		double enemyStrengthTotal = 0.;
		double playerStrengthTotal = 0.;

		// TODO: Possible Change: AirStrength Implementation

		// Sum the total strength of the player and the enemy in a given radius
		// around the unit.
		for (int i = -CONFIDENCE_TILE_RADIUS; i <= CONFIDENCE_TILE_RADIUS; i++) {
			for (int j = -CONFIDENCE_TILE_RADIUS; j <= CONFIDENCE_TILE_RADIUS; j++) {
				TilePosition key = new TilePosition(this.unit.getTilePosition().getX() + i,
						this.unit.getTilePosition().getY() + j);

				Integer eStrength = UnitTrackerModule.getInstance().enemyGroundAttackTilePositions.get(key);
				Integer pStrength = UnitTrackerModule.getInstance().playerGroundAttackTilePositions.get(key);

				if (eStrength != null) {
					enemyStrengths.add(eStrength);
				}
				if (pStrength != null) {
					playerStrengths.add(pStrength);
				}
			}
		}

		enemyStrengthTotal = sumHashSet(enemyStrengths);
		playerStrengthTotal = sumHashSet(playerStrengths);

		// Has to be set for following equation
		if (enemyStrengthTotal == 0.) {
			enemyStrengthTotal = 1.;
		}

		this.confidence = playerStrengthTotal / enemyStrengthTotal;
	}

	/**
	 * Used for getting the sum of all elements inside a HashSet of Integers.
	 * 
	 * @param set
	 *            the set the sum is calculated of.
	 * @return the sum of all elements inside the given Integer HashSet.
	 */
	public static int sumHashSet(HashSet<Integer> set) {
		int sum = 0;

		for (Integer integer : set) {
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
		return this.getAllEnemyUnitsInRange(CONFIDENCE_TILE_RADIUS * Display.TILESIZE);
	}

	/**
	 * Function for retrieving a HashSet of all player Units in the confidence
	 * range of this Unit.
	 * 
	 * @return a HashSet of all player Units in the confidence range.
	 */
	public HashSet<Unit> getAllPlayerUnitsInConfidenceRange() {
		return this.getAllPlayerUnitsInRange(CONFIDENCE_TILE_RADIUS * Display.TILESIZE);
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

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return this.unit;
	}

}
