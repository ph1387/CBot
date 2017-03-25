package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import core.Core;
import core.Display;
import javaGOAP.GoapUnit;
import javaGOAP.GoapAction;
import unitControlModule.Vector;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.updater.Updater;
import unitTrackerModule.EnemyUnit;

/**
 * PlayerUnit.java --- Wrapper for a player unit. All Player Units derive from
 * this.
 * 
 * @author P H - 20.02.2017
 *
 */
public abstract class PlayerUnit extends GoapUnit {

	public static final int BASELOCATIONS_TIME_PASSED = 180;
	public static final double CONFIDENCE_THRESHHOLD = 0.7;
	protected static final Integer DEFAULT_TILE_SEARCH_RADIUS = 5;
	protected static final int CONFIDENCE_TILE_RADIUS = 15;

	protected static HashMap<BaseLocation, Integer> BaselocationsSearched = new HashMap<>();

	// Information regarding the enemy Units.
	protected static HashMap<TilePosition, Integer> playerAirAttackTilePositions;
	protected static HashMap<TilePosition, Integer> playerGroundAttackTilePositions;
	protected static HashMap<TilePosition, Integer> enemyAirAttackTilePositions;
	protected static HashMap<TilePosition, Integer> enemyGroundAttackTilePositions;
	protected static List<EnemyUnit> enemyBuildings;
	protected static List<EnemyUnit> enemyUnits;

	protected Unit unit;
	protected Unit closestEnemyUnitInSight;
	protected Unit closestEnemyUnitInConfidenceRange;
	protected double confidence = 1.;
	protected int extraConfidencePixelRangeToClosestUnits = 16;
	protected double confidenceDefault = 0.75;

	// Vector related stuff
	protected static final int ALPHA_MAX = 90;
	protected double maxDistance = CONFIDENCE_TILE_RADIUS * Core.getInstance().getTileSize();
	protected double alphaMod = 75.;
	protected double alphaAdd = 10.; // AlphaMod + AlphaAdd < AlphaMax
	// vecEU -> Vector(enemyUnit, playerUnit)
	// vecUTP -> Vector(playerUnit, targetPosition)
	// vecUTPRotatedL -> Rotated Vector left
	// vecUTPRotatedR -> Rotated Vector right
	protected Vector vecEU, vecUTP, vecUTPRotatedL, vecUTPRotatedR;

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
		// FSM worldState changes in one cycle.
		if (this.currentState == UnitStates.ENEMY_MISSING && (enemyUnits.size() != 0 || enemyBuildings.size() != 0)) {
			this.resetActions();
			this.currentState = UnitStates.ENEMY_KNOWN;
		}
		if (this.currentState == UnitStates.ENEMY_KNOWN) {
			if (enemyUnits.size() == 0 && enemyBuildings.size() == 0) {
				this.resetActions();
				this.currentState = UnitStates.ENEMY_MISSING;
			} else {
				this.actOnUnitsKnown();
			}
		}

		try {
			this.worldStateUpdater.update(this);
			this.goalStateUpdater.update(this);

			// Vector update has to be here since some depend on the results of
			// these.
			this.updateVectors();

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
		this.closestEnemyUnitInSight = this
				.getClosestUnit(this.getAllEnemyUnitsInRange(this.unit.getType().sightRange()));
		this.closestEnemyUnitInConfidenceRange = this.getClosestUnit(this.getAllEnemyUnitsInConfidenceRange());

		this.updateConfidence();
		this.updateConfidenceState();
		this.updateCurrentRangeState();
	}

	/**
	 * Function for updating the confidence of the Unit which determines if it
	 * attacks an enemy Unit / building, retreats to another player Unit or
	 * takes a completely different action.
	 */
	protected void updateConfidence() {
		List<Integer> enemyStrengths = new ArrayList<Integer>();
		List<Integer> playerStrengths = new ArrayList<Integer>();
		double enemyStrengthTotal = 0.;
		double playerStrengthTotal = 0.;
		// TODO: Possible Change: Change the way the life offset is calculated.
		// Calculate the offset of the confidence based on the current Units
		// health.
		double lifeConfidenceMultiplicator = (double) (this.unit.getHitPoints())
				/ (double) (this.unit.getInitialHitPoints());

		// TODO: Possible Change: AirStrength Implementation

		// Sum the total strength of the player and the enemy in a given radius
		// around the unit.
		for (int i = -CONFIDENCE_TILE_RADIUS; i <= CONFIDENCE_TILE_RADIUS; i++) {
			for (int j = -CONFIDENCE_TILE_RADIUS; j <= CONFIDENCE_TILE_RADIUS; j++) {
				TilePosition key = new TilePosition(this.unit.getTilePosition().getX() + i,
						this.unit.getTilePosition().getY() + j);
				int eStrength = enemyGroundAttackTilePositions.getOrDefault(key, 0);
				int pStrength = playerGroundAttackTilePositions.getOrDefault(key, 0);

				if (eStrength != 0) {
					enemyStrengths.add(eStrength);
				}
				if (pStrength != 0) {
					playerStrengths.add(pStrength);
				}
			}
		}

		enemyStrengthTotal = getSum(enemyStrengths);
		playerStrengthTotal = getSum(playerStrengths);

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
			this.confidence = (playerStrengthTotal / enemyStrengthTotal) * lifeConfidenceMultiplicator * this.confidenceDefault;
		}
		// -> PlayerUnit out of range of the enemy Unit
		else {
			this.confidence = (playerStrengthTotal / enemyStrengthTotal);
		}
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
		if (this.currentRangeState == ConfidenceRangeStates.UNIT_IN_RANGE) {
			if (this.closestEnemyUnitInConfidenceRange == null) {
				this.currentRangeState = ConfidenceRangeStates.NO_UNIT_IN_RANGE;
				this.resetActions();
			} else {

				// TODO: DEBUG INFO
				// Nearest enemy Unit display.
				Display.drawTileFilled(Core.getInstance().getGame(),
						this.closestEnemyUnitInConfidenceRange.getTilePosition().getX(),
						this.closestEnemyUnitInConfidenceRange.getTilePosition().getY(), 1, 1, new Color(0, 0, 255));

			}
		}
	}

	/**
	 * Function for updating all vectors of the PlayerUnit.
	 */
	private void updateVectors() {
		if (this.closestEnemyUnitInConfidenceRange != null) {
			this.updateVecEU();
			this.updateVecUTP();
			this.updateVecRotated();

			// TODO: DEBUG INFO
			// Cone of possible retreat Positions
			Position targetEndPosition = new Position(vecUTP.x + (int) (vecUTP.dirX), vecUTP.y + (int) (vecUTP.dirY));
			// Position rotatedLVecEndPos = new Position(vecUTPRotatedL.x +
			// (int) (vecUTPRotatedL.dirX),
			// vecUTPRotatedL.y + (int) (vecUTPRotatedL.dirY));
			// Position rotatedRVecEndPos = new Position(vecUTPRotatedR.x +
			// (int) (vecUTPRotatedR.dirX),
			// vecUTPRotatedR.y + (int) (vecUTPRotatedR.dirY));
			Core.getInstance().getGame().drawLineMap(this.unit.getPosition(), targetEndPosition,
					new Color(255, 128, 255));
			// Core.getInstance().getGame().drawLineMap(this.unit.getPosition(),
			// rotatedLVecEndPos, new Color(255, 0, 0));
			// Core.getInstance().getGame().drawLineMap(this.unit.getPosition(),
			// rotatedRVecEndPos, new Color(0, 255, 0));
			// Core.getInstance().getGame().drawTextMap(rotatedLVecEndPos,
			// String.valueOf(alphaActual));
			// Core.getInstance().getGame().drawTextMap(rotatedRVecEndPos,
			// String.valueOf(alphaActual));
		}
	}

	/**
	 * Used for updating the Vector from the closest enemy Unit in the
	 * confidence range to the PlayerUnit.
	 */
	private void updateVecEU() {
		// uPos -> Unit Position, ePos -> Enemy Position
		int uPosX = this.unit.getPosition().getX();
		int uPosY = this.unit.getPosition().getY();
		int ePosX = this.closestEnemyUnitInConfidenceRange.getPosition().getX();
		int ePosY = this.closestEnemyUnitInConfidenceRange.getPosition().getY();

		this.vecEU = new Vector(ePosX, ePosY, uPosX - ePosX, uPosY - ePosY);
	}

	/**
	 * Used for updating the Vector from the PlayerUnit to a possible retreat
	 * position.
	 */
	private void updateVecUTP() {
		double vecRangeMultiplier = (this.maxDistance - vecEU.length()) / this.maxDistance;
		double neededDistanceMultiplier = this.maxDistance / vecEU.length();

		// The direction-Vector is projected on the maxDistance and then
		// combined with the rangeMultiplier to receive a representation of
		// the distance between the enemyUnit and the currentUnit based on
		// their distance to another.
		int tPosX = (int) (vecRangeMultiplier * neededDistanceMultiplier * vecEU.dirX);
		int tPosY = (int) (vecRangeMultiplier * neededDistanceMultiplier * vecEU.dirY);

		this.vecUTP = new Vector(this.vecEU.x, this.vecEU.y, tPosX, tPosY);
	}

	/**
	 * Used for updating all Vectors which are the rotated equivalent to the
	 * Vector targeting the possible retreat position.
	 */
	private void updateVecRotated() {
		double alphaActual = (this.alphaMod * (this.vecEU.length() / this.maxDistance)) + this.alphaAdd;

		// Create two vectors that are left and right rotated
		// representations of the vector(playerUnit, targetPosition) by the
		// actual alpha value.
		// vecRotatedL -> Rotated Vector left
		// vecRotatedR -> Rotated Vector right
		Vector rotatedL = new Vector(this.vecUTP.x, this.vecUTP.y, this.vecUTP.dirX, this.vecUTP.dirY);
		Vector rotatedR = new Vector(this.vecUTP.x, this.vecUTP.y, this.vecUTP.dirX, this.vecUTP.dirY);
		rotatedL.rotateLeftDEG(alphaActual);
		rotatedR.rotateRightDEG(alphaActual);

		this.vecUTPRotatedL = rotatedL;
		this.vecUTPRotatedR = rotatedR;
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

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return this.unit;
	}

	public static HashMap<BaseLocation, Integer> getBaselocationsSearched() {
		return BaselocationsSearched;
	}

	public Unit getClosestEnemyUnitInSight() {
		return this.closestEnemyUnitInSight;
	}

	public Unit getClosestEnemyUnitInConfidenceRange() {
		return this.closestEnemyUnitInConfidenceRange;
	}

	public double getConfidence() {
		return this.confidence;
	}

	public Vector getVecUTP() {
		return this.vecUTP;
	}

	public Vector getVecUTPRotatedL() {
		return this.vecUTPRotatedL;
	}

	public Vector getVecUTPRotatedR() {
		return this.vecUTPRotatedR;
	}

	public static HashMap<TilePosition, Integer> getPlayerAirAttackTilePositions() {
		return playerAirAttackTilePositions;
	}

	public static void setPlayerAirAttackTilePositions(HashMap<TilePosition, Integer> playerAirAttackTilePositions) {
		PlayerUnit.playerAirAttackTilePositions = playerAirAttackTilePositions;
	}

	public static HashMap<TilePosition, Integer> getPlayerGroundAttackTilePositions() {
		return playerGroundAttackTilePositions;
	}

	public static void setPlayerGroundAttackTilePositions(
			HashMap<TilePosition, Integer> playerGroundAttackTilePositions) {
		PlayerUnit.playerGroundAttackTilePositions = playerGroundAttackTilePositions;
	}

	public static HashMap<TilePosition, Integer> getEnemyAirAttackTilePositions() {
		return enemyAirAttackTilePositions;
	}

	public static void setEnemyAirAttackTilePositions(HashMap<TilePosition, Integer> enemyAirAttackTilePositions) {
		PlayerUnit.enemyAirAttackTilePositions = enemyAirAttackTilePositions;
	}

	public static HashMap<TilePosition, Integer> getEnemyGroundAttackTilePositions() {
		return enemyGroundAttackTilePositions;
	}

	public static void setEnemyGroundAttackTilePositions(
			HashMap<TilePosition, Integer> enemyGroundAttackTilePositions) {
		PlayerUnit.enemyGroundAttackTilePositions = enemyGroundAttackTilePositions;
	}

	public static List<EnemyUnit> getEnemyBuildings() {
		return enemyBuildings;
	}

	public static void setEnemyBuildings(List<EnemyUnit> enemyBuildings) {
		PlayerUnit.enemyBuildings = enemyBuildings;
	}

	public static List<EnemyUnit> getEnemyUnits() {
		return enemyUnits;
	}

	public static void setEnemyUnits(List<EnemyUnit> enemyUnits) {
		PlayerUnit.enemyUnits = enemyUnits;
	}

}
