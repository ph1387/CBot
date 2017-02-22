package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
import core.Core;
import core.Display;
import unitControlModule.actions.AttackMoveAction;
import unitControlModule.actions.AttackUnitAction;
import unitControlModule.actions.RetreatFromNearestUnitAction;
import unitControlModule.actions.ScoutBaseLocationAction;
import unitControlModule.goapActionTaking.GoapAction;
import unitControlModule.goapActionTaking.GoapState;
import unitControlModule.goapActionTaking.GoapUnit;
import unitTrackerModule.EnemyUnit;
import unitTrackerModule.UnitTrackerModule;

/**
 * PlayerUnit.java --- Wrapper for a player unit.
 * 
 * @author P H - 20.02.2017
 *
 */
public class PlayerUnit extends GoapUnit {
	
	protected static Hashtable<BaseLocation, Integer> BASELOCATIONS_SEARCHED = new Hashtable<>();
	protected static int BASELOCATIONS_TIME_PASSED = 180;
	protected static Integer DEFAULT_SEARCH_RADIUS = 5;
	public final static int CONFIDENCE_TILE_RADIUS = 15;
	protected static double CONFIDENCE_THRESHHOLD = 0.7;
	
	protected Unit unit;
	protected Unit nearestEnemyUnitInSight;
	protected Unit nearestEnemyUnitInConfidenceRange;
	protected double confidence = 1.;
	
	protected enum UnitStates {
		ENEMY_MISSING, ENEMY_KNOWN
	}
	
	protected enum ConfidenceRangeStates {
		NO_UNIT_IN_RANGE, UNIT_IN_RANGE
	}

	protected UnitStates currentState = UnitStates.ENEMY_MISSING;
	protected ConfidenceRangeStates currentRangeState = ConfidenceRangeStates.NO_UNIT_IN_RANGE;

	/**
	 * @param unit
	 *            the unit the class wraps around.
	 */
	public PlayerUnit(Unit unit) {
		this.unit = unit;

		this.addWorldState(new GoapState(1, "enemyKnown", false));
		this.addWorldState(new GoapState(1, "destroyUnit", false));
		this.addWorldState(new GoapState(1, "retreatFromUnit", false));
		this.addWorldState(new GoapState(1, "unitsInRange", false));
		this.addWorldState(new GoapState(1, "unitsInSight", false));

		this.addGoalState(new GoapState(1, "enemyKnown", true));
		this.addGoalState(new GoapState(2, "destroyUnit", true));
		this.addGoalState(new GoapState(1, "retreatFromUnit", true));
		
		this.addAvailableAction(new ScoutBaseLocationAction(null));
		this.addAvailableAction(new AttackMoveAction(null));
		this.addAvailableAction(new AttackUnitAction(this.nearestEnemyUnitInSight));
		this.addAvailableAction(new RetreatFromNearestUnitAction(this.nearestEnemyUnitInSight));

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
		if (this.currentState == UnitStates.ENEMY_MISSING) {
			this.changeDestroyUnit(false);
			this.changeEnemyKnown(false);

			this.actOnBuildingsMissing();

			if (UnitTrackerModule.getInstance().enemyUnits.size() != 0
					|| UnitTrackerModule.getInstance().enemyBuildings.size() != 0) {
				this.resetActions();
				this.currentState = UnitStates.ENEMY_KNOWN;
			}
		}
		if (this.currentState == UnitStates.ENEMY_KNOWN) {
			if (UnitTrackerModule.getInstance().enemyUnits.size() == 0
					&& UnitTrackerModule.getInstance().enemyBuildings.size() == 0) {
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
	protected void actOnBuildingsMissing() {
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
		
		((ScoutBaseLocationAction) this.getActionFromInstance(ScoutBaseLocationAction.class)).setTarget(closestReachableBasePosition);
	}

	/**
	 * Function for acting on the fact that enemy units (units and buildings)
	 * are known of.
	 */
	protected void actOnUnitsKnown() {
		this.nearestEnemyUnitInSight = this.getClosestUnit(this.getAllEnemyUnitsInRange(this.unit.getType().sightRange()));
		this.nearestEnemyUnitInConfidenceRange = this.getClosestUnit(this.getAllEnemyUnitsInRange(CONFIDENCE_TILE_RADIUS * Display.TILESIZE));
		
		this.updateConfidence();
		this.updateWorldState();
		this.updateGoalState();
		this.attackMoveToNearestKnownUnitConfiguration();
		
		// No "else if" to perform change in one cycle if an enemy Unit is in range.
		if(this.currentRangeState == ConfidenceRangeStates.NO_UNIT_IN_RANGE && this.nearestEnemyUnitInConfidenceRange != null){
			this.currentRangeState = ConfidenceRangeStates.UNIT_IN_RANGE;
			this.resetActions();
		}
		if(this.currentRangeState == ConfidenceRangeStates.UNIT_IN_RANGE) {
			
			if(this.nearestEnemyUnitInConfidenceRange == null) {
				this.currentRangeState = ConfidenceRangeStates.NO_UNIT_IN_RANGE;
				this.resetActions();
			} else {
				((AttackUnitAction) this.getActionFromInstance(AttackUnitAction.class)).setTarget(this.nearestEnemyUnitInConfidenceRange);
				((RetreatFromNearestUnitAction) this.getActionFromInstance(RetreatFromNearestUnitAction.class)).setTarget(this.nearestEnemyUnitInConfidenceRange);
				
				// TODO: REMOVE TEST
				Display.drawTileFilled(Core.getInstance().getGame(), this.nearestEnemyUnitInConfidenceRange.getTilePosition().getX(), this.nearestEnemyUnitInConfidenceRange.getTilePosition().getY(), 1, 1, new Color(0, 0, 255));
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
	
	/**
	 * Function for updating the confidence of the Unit which determines it attacks an enemy Unit / building or retreats to another player Unit.
	 */
	protected void updateConfidence() {
		HashSet<Integer> enemyStrengths = new HashSet<Integer>();
		HashSet<Integer> playerStrengths = new HashSet<Integer>();
		double enemyStrengthTotal = 0.;
		double playerStrengthTotal = 0.;
		
		
		// TODO: Possible Change: AirStrength Implementation
		
		// Sum the total strength of the player and the enemy in a given radius around the unit.
		for(int i = - CONFIDENCE_TILE_RADIUS; i <= CONFIDENCE_TILE_RADIUS; i++) {
			for(int j = - CONFIDENCE_TILE_RADIUS; j <= CONFIDENCE_TILE_RADIUS; j++) {
				TilePosition key = new TilePosition(this.unit.getTilePosition().getX() + i, this.unit.getTilePosition().getY() + j);
				
				Integer eStrength = UnitTrackerModule.getInstance().enemyGroundAttackTilePositions.get(key);
				Integer pStrength = UnitTrackerModule.getInstance().playerGroundAttackTilePositions.get(key);
				
				if(eStrength != null) {
					enemyStrengths.add(eStrength);
				}
				if(pStrength != null) {
					playerStrengths.add(pStrength);
				}
			}
		}
		
		enemyStrengthTotal = sumHashSet(enemyStrengths);
		playerStrengthTotal = sumHashSet(playerStrengths);

		// Has to be set for following equation
		if(enemyStrengthTotal == 0.) {
			enemyStrengthTotal = 1.;
		}
		
		this.confidence = playerStrengthTotal / enemyStrengthTotal;
	}
	
	/**
	 * Function for updating all worldState references.
	 */
	protected void updateWorldState() {
		if(this.nearestEnemyUnitInSight == null) {
			this.changeUnitsInSight(false);
		} else {
			this.changeUnitsInSight(true);
		}
		if(this.getAllEnemyUnitsInWeaponRange().isEmpty()) {
			this.changeUnitsInRange(false);
		} else {
			this.changeUnitsInRange(true);
		}
	}
	
	/**
	 * Function for updating all goalState references.
	 */
	protected void updateGoalState() {
//		if(this.confidence >= CONFIDENCE_THRESHHOLD) {
//			this.changeGoalStateImportance("retreatFromUnit", 1);
//		} else {
			this.changeGoalStateImportance("retreatFromUnit", 3);
//		}
	}
	
	/**
	 * Function for the unit to configure its AttackMoveAction to the nearest enemy
	 * Unit (can be a building).
	 */
	protected void attackMoveToNearestKnownUnitConfiguration() {
		TilePosition closestUnitTilePosition = null;

		List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>(UnitTrackerModule.getInstance().enemyUnits);
		enemyUnits.addAll(UnitTrackerModule.getInstance().enemyBuildings);

		// Find the closest unit of the known ones
		for (EnemyUnit unit : enemyUnits) {
			if (closestUnitTilePosition == null
					|| this.unit.getDistance(unit.getLastSeenTilePosition().toPosition()) < this.unit
							.getDistance(closestUnitTilePosition.toPosition())) {
				closestUnitTilePosition = unit.getLastSeenTilePosition();
			}
		}
		((AttackMoveAction) this.getActionFromInstance(AttackMoveAction.class)).setTarget(closestUnitTilePosition);
	}
	
	/**
	 * Used for getting the sum of all elements inside a HashSet of Integers.
	 * @param set the set the sum is calculated of.
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
	 * Function for extracting the closest Unit relative to this Unit from a given HashSet.
	 * @param set the HashSet which is going to be searched.
	 * @return the closest Unit from the given HashSet or null if the HashSet is empty.
	 */
	protected Unit getClosestUnit(HashSet<Unit> set) {
		Unit closestUnit = null;

		for (Unit setUnit : set) {
			if (closestUnit == null
					|| this.unit.getDistance(setUnit.getPosition()) < this.unit.getDistance(closestUnit.getPosition())) {
				closestUnit = setUnit;
			}
		}
		return closestUnit;
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
	protected <T> GoapAction getActionFromInstance(Class<T> instanceClass) {
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
	 * Convenience function.
	 * 
	 * @param effect the effect of the goalState whose importance is going to be changed.
	 * @param importance the new importance that the goalState is going to be assigned to.
	 * @return true or false depending if the goalState is found or not.
	 * 
	 * @see #changeGoalStateImportance(GoapState state, int importance)
	 */
	protected boolean changeGoalStateImportance(String effect, int importance) {
		return this.changeGoalStateImportance(this.getGoalFromEffect(effect), importance);
	}
	
	/**
	 * Function for changing the importance of a specific goalState.
	 * @param state the goalState whose importance is going to be changed.
	 * @param importance the new importance that the goalState is going to be assigned to.
	 * @return true or false depending if the goalState is found or not.
	 */
	protected boolean changeGoalStateImportance(GoapState state, int importance) {
		try {
			state.importance = importance;
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Function for retrieving a set goalState from this units goalState List depending on the effect the goal was assigned.
	 * @param effect the effect whose goalState is going to be searched for. 
	 * @return the goalState that has the given effect or null if none is found.
	 */
	protected GoapState getGoalFromEffect(String effect){
		GoapState state = null;
		
		for (GoapState goalState : this.getGoalState()) {
			if(goalState.effect.equals(effect)) {
				state = goalState;
			}
		}
		return state;
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

	/**
	 * Convenience function.
	 * 
	 * @see #changeWorldStateEffect(String effect, Object value)
	 */
	protected void changeEnemyKnown(Object value) {
		this.changeWorldStateEffect("enemyKnown", value);
	}

	/**
	 * Convenience function.
	 * 
	 * @see #changeWorldStateEffect(String effect, Object value)
	 */
	protected void changeDestroyUnit(Object value) {
		this.changeWorldStateEffect("destroyUnit", value);
	}
	
	/**
	 * Convenience function.
	 * 
	 * @see #changeWorldStateEffect(String effect, Object value)
	 */
	protected void changeUnitsInRange(Object value) {
		this.changeWorldStateEffect("unitsInRange", value);
	}
	
	/**
	 * Convenience function.
	 * 
	 * @see #changeWorldStateEffect(String effect, Object value)
	 */
	protected void changeUnitsInSight(Object value) {
		this.changeWorldStateEffect("unitsInSight", value);
	}

	/**
	 * Change the world state accordingly.
	 * 
	 * @param effect
	 *            the effect which is going to be changed.
	 * @param value
	 *            the value the effect shall have.
	 */
	protected void changeWorldStateEffect(String effect, Object value) {
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

}
