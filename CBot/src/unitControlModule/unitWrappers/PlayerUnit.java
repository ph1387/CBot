package unitControlModule.unitWrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;

import bwapi.Pair;
import bwapi.Player;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.WeaponType;
import bwapiMath.Point;
import bwta.BWTA;
import bwta.BaseLocation;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.GoapUnit;
import javaGOAP.GoapAction;
import unitControlModule.stateFactories.StateFactory;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
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

	// TODO: UML REMOVE
	// The timer after a BaseLocation might be searched again.
	// public static final int BASELOCATIONS_TIME_PASSED = 60;
	// TODO: Possible Change: Reevaluate the importance of Units choosing their
	// own parameters
	protected static final double CONFIDENCE_THRESHHOLD = 0.5;
	protected static final Integer DEFAULT_TILE_SEARCH_RADIUS = 2;
	private static final int CONFIDENCE_TILE_RADIUS = 15;

	// The default tile range that is applied to the detection check of the
	private static final int DEFAULT_DETECTION_TILERANGE = 10;

	// Information preserver which holds all important information
	protected InformationStorage informationStorage;
	protected Unit unit;

	protected Unit closestEnemyUnitInConfidenceRange;
	// The special UnitTypes that the Unit is looking out for and prioritizes in
	// its target choosing. These are mostly sentry turrets and static buildings
	// that can attack Units.
	protected List<UnitType> specialUnitTypes = Arrays.asList(
			new UnitType[] { UnitType.Terran_Bunker, UnitType.Terran_Missile_Turret, UnitType.Protoss_Photon_Cannon,
					UnitType.Zerg_Creep_Colony, UnitType.Zerg_Spore_Colony, UnitType.Zerg_Sunken_Colony, });
	// The closest enemy Unit this one can attack:
	protected Unit closestAttackableEnemyUnitInConfidenceRange;
	protected Unit closestAttackableEnemyUnitWithWeapon;
	protected Unit closestAttackableEnemySpecialUnitInConfidenceRange;
	protected Unit closestAttackableEnemyWorkerInConfidenceRange;
	protected Unit closestAttackableEnemySupplyProviderInConfidenceRange;
	protected Unit closestAttackableEnemyCenterInConfidenceRange;
	protected Unit attackableEnemyUnitToReactTo;
	// The closest enemy Unit that this one can be attacked by:
	protected Unit closestAttackingEnemyUnitInConfidenceRange;
	protected Unit attackingEnemyUnitToReactTo;

	protected double confidence = 1.;
	// Properties used for modifying a generated confidence:
	// The distance at which the center range confidence multiplier activates.
	private int maxCenterPixelDistanceConfidenceBoost = 320;
	private double confidenceMultiplierSingleCenter = 2.5;
	private double confidenceMultiplierInMaxCenterDistance = 1.5;

	// The wrapper that is controlling the resets of the Unit if an issue
	// occurrs.
	private IssueStateWrapper issueStateWrapper = new IssueStateWrapper();

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
		if (this.informationStorage.getBaselocationsSearched().size() == 0) {
			for (BaseLocation location : BWTA.getBaseLocations()) {
				this.informationStorage.getBaselocationsSearched().put(location, 0);
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
					&& (!this.informationStorage.getTrackerInfo().getEnemyUnits().isEmpty()
							|| !this.informationStorage.getTrackerInfo().getEnemyBuildings().isEmpty())) {
				this.resetActions();
				this.currentState = UnitStates.ENEMY_KNOWN;
			}
			if (this.currentState == UnitStates.ENEMY_KNOWN) {
				if (this.informationStorage.getTrackerInfo().getEnemyUnits().isEmpty()
						&& this.informationStorage.getTrackerInfo().getEnemyBuildings().isEmpty()) {
					this.resetActions();
					this.currentState = UnitStates.ENEMY_MISSING;
				} else {
					this.actOnUnitsKnown();
				}
			}

			// Check if the Unit is stuck or idling.
			this.checkForUnresolvedIssue();

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
		// Update the references to the different enemy Units that are around
		// this Unit.
		this.updateEnemyUnitReferences();

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

	/**
	 * Function for updating all references regarding enemy Units. This includes
	 * i.e. the closest one towards this Unit.
	 */
	private void updateEnemyUnitReferences() {
		List<Unit> sortedEnemyUnitsInConfidenceRange = this
				.sortByDistance(new ArrayList<Unit>(this.getAllEnemyUnitsInConfidenceRange()));

		// Reset all references set in the previous iteration.
		this.resetUnitReferences();

		// Reassign all references to the various enemy Units around the current
		// one based on their distance.
		this.reassignUnitReferences(sortedEnemyUnitsInConfidenceRange);

		// Set the most important references of this Unit: The ones that it must
		// react to!
		this.attackableEnemyUnitToReactTo = this.generateAttackableEnemyUnitToReactTo();
		this.attackingEnemyUnitToReactTo = this.generateAttackingEnemyUnitToReactTo();
	}

	/**
	 * Function for resetting all references regarding the different possible
	 * types of enemy Units.
	 */
	private void resetUnitReferences() {
		this.closestEnemyUnitInConfidenceRange = null;
		this.closestAttackableEnemyUnitInConfidenceRange = null;
		this.closestAttackableEnemyUnitWithWeapon = null;
		this.closestAttackableEnemySpecialUnitInConfidenceRange = null;
		this.closestAttackableEnemyWorkerInConfidenceRange = null;
		this.closestAttackableEnemySupplyProviderInConfidenceRange = null;
		this.closestAttackableEnemyCenterInConfidenceRange = null;
		this.attackableEnemyUnitToReactTo = null;
	}

	/**
	 * Function for sorting a given List of Units based on their distance
	 * towards the current instance's Unit.
	 * 
	 * @param inputList
	 *            the List of Units that is going to be sorted.
	 * @return the provided List's instance sorted based on the different Unit
	 *         distances towards the current one calling this function.
	 */
	private List<Unit> sortByDistance(List<Unit> inputList) {
		final Unit referenceUnit = this.unit;

		inputList.sort(new Comparator<Unit>() {

			@Override
			public int compare(Unit u1, Unit u2) {
				return Integer.compare(referenceUnit.getDistance(u1), referenceUnit.getDistance(u2));
			}
		});
		return inputList;
	}

	/**
	 * Function for reassigning the different types of enemy Unit references
	 * based on different criteria. The provided List must contain a selection
	 * of Units that can be chosen by this one as targets or ones to generally
	 * react to. This function will take the first matching Unit found in the
	 * provided List. Therefore the order in which the Units are places in the
	 * List matters! <br>
	 * (-> Sort by distance to make the executing Unit always choose the nearest
	 * possible match for each criteria!)
	 * 
	 * @param sortedList
	 *            the List from which the different Unit references are being
	 *            taken if they match the criteria.
	 */
	private void reassignUnitReferences(List<Unit> sortedList) {
		boolean running = true;
		int currentIndex = 0;

		// Flags for an efficient assigning of the different references. (O(n)
		// instead of O(n*n)!).
		boolean closestEnemyUnitInConfidenceRangeAssigned = false;
		boolean closestAttackableEnemyUnitWithWeaponAssigned = false;
		boolean closestAttackableEnemyUnitInConfidenceRangeAssigned = false;
		boolean closestAttackableEnemySpecialUnitInConfidenceRangeAssigned = false;
		boolean closestAttackableEnemyWorkerInConfidenceRangeAssigned = false;
		boolean closestAttackableEnemySupplyProviderInConfidenceRangeAssigned = false;
		boolean closestAttackableEnemyCenterInConfidenceRangeAssigned = false;

		boolean closestAttackingEnemyUnitInConfidenceRangeAssigned = false;

		// Try assigning each reference to a Unit. Always choose the first one
		// matching since this is the one closest to the Unit!
		while (currentIndex < sortedList.size() && running) {
			Unit currentUnit = sortedList.get(currentIndex);

			// ---------- Attackable enemy Units:
			if (!closestEnemyUnitInConfidenceRangeAssigned) {
				this.closestEnemyUnitInConfidenceRange = currentUnit;
				closestEnemyUnitInConfidenceRangeAssigned = true;
			}
			if (!closestAttackableEnemyUnitWithWeaponAssigned && this.hasWeapon(currentUnit)
					&& this.canAttack(currentUnit)) {
				this.closestAttackableEnemyUnitWithWeapon = currentUnit;
				closestAttackableEnemyUnitWithWeaponAssigned = true;
			}
			if (!closestAttackableEnemyUnitInConfidenceRangeAssigned && this.canAttack(currentUnit)) {
				this.closestAttackableEnemyUnitInConfidenceRange = currentUnit;
				closestAttackableEnemyUnitInConfidenceRangeAssigned = true;
			}
			if (!closestAttackableEnemySpecialUnitInConfidenceRangeAssigned
					&& specialUnitTypes.contains(currentUnit.getType()) && this.canAttack(currentUnit)) {
				this.closestAttackableEnemySpecialUnitInConfidenceRange = currentUnit;
				closestAttackableEnemySpecialUnitInConfidenceRangeAssigned = true;
			}
			if (!closestAttackableEnemyWorkerInConfidenceRangeAssigned && currentUnit.getType().isWorker()
					&& this.canAttack(currentUnit)) {
				this.closestAttackableEnemyWorkerInConfidenceRange = currentUnit;
				closestAttackableEnemyWorkerInConfidenceRangeAssigned = true;
			}
			if (!closestAttackableEnemySupplyProviderInConfidenceRangeAssigned
					&& currentUnit.getType() == Core.getInstance().getGame().enemy().getRace().getSupplyProvider()
					&& this.canAttack(currentUnit)) {
				this.closestAttackableEnemySupplyProviderInConfidenceRange = currentUnit;
				closestAttackableEnemySupplyProviderInConfidenceRangeAssigned = true;
			}
			if (!closestAttackableEnemyCenterInConfidenceRangeAssigned
					&& currentUnit.getType() == Core.getInstance().getGame().enemy().getRace().getCenter()
					&& this.canAttack(currentUnit)) {
				this.closestAttackableEnemyCenterInConfidenceRange = currentUnit;
				closestAttackableEnemyCenterInConfidenceRangeAssigned = true;
			}

			// ---------- Attacking enemy Units:
			if (!closestAttackingEnemyUnitInConfidenceRangeAssigned && this.canBeAttackedBy(currentUnit)) {
				this.closestAttackingEnemyUnitInConfidenceRange = currentUnit;
				closestAttackingEnemyUnitInConfidenceRangeAssigned = true;
			}

			// Stop when each reference was updated.
			running = !(closestEnemyUnitInConfidenceRangeAssigned && closestAttackableEnemyUnitWithWeaponAssigned
					&& closestAttackableEnemyUnitInConfidenceRangeAssigned
					&& closestAttackableEnemySpecialUnitInConfidenceRangeAssigned
					&& closestAttackableEnemyWorkerInConfidenceRangeAssigned
					&& closestAttackableEnemySupplyProviderInConfidenceRangeAssigned
					&& closestAttackableEnemyCenterInConfidenceRangeAssigned
					&& closestAttackingEnemyUnitInConfidenceRangeAssigned);
			currentIndex++;
		}
	}

	/**
	 * Function for generating / finding the Unit that this one has to react to
	 * when taking any offensive actions. The Unit has to chose between
	 * different kinds of Units in it's surrounding like workers or centers.
	 * 
	 * @return the Unit that this one should react to when taking offensive
	 *         actions.
	 */
	private Unit generateAttackableEnemyUnitToReactTo() {
		Unit unitToReactTo = null;

		// Prioritize the special Units above all other Units: In weapon range.
		if (this.closestAttackableEnemySpecialUnitInConfidenceRange != null
				&& this.unit.isInWeaponRange(this.closestAttackableEnemySpecialUnitInConfidenceRange)) {
			unitToReactTo = this.closestAttackableEnemySpecialUnitInConfidenceRange;
		}
		// Otherwise decide based on other conditions.
		else if (this.closestAttackableEnemyUnitInConfidenceRange != null) {
			// Enemies with weapons.
			if (this.closestAttackableEnemyUnitWithWeapon != null) {
				unitToReactTo = this.closestAttackableEnemyUnitWithWeapon;
			}
			// Workers. (Probably never called since workers can attack ground
			// Units)
			else if (this.closestAttackableEnemyWorkerInConfidenceRange != null) {
				unitToReactTo = this.closestAttackableEnemyWorkerInConfidenceRange;
			}
			// SupplyProvider: No enemy is able to attack this Unit.
			else if (this.closestAttackableEnemySupplyProviderInConfidenceRange != null
					&& this.closestAttackingEnemyUnitInConfidenceRange == null) {
				unitToReactTo = this.closestAttackableEnemySupplyProviderInConfidenceRange;
			}
			// Center: No enemy is able to attack this Unit.
			else if (this.closestAttackableEnemyCenterInConfidenceRange != null
					&& this.closestAttackingEnemyUnitInConfidenceRange == null) {
				unitToReactTo = this.closestAttackableEnemyCenterInConfidenceRange;
			}
			// No Unit prioritized => The closest one is targeted.
			else {
				unitToReactTo = this.closestAttackableEnemyUnitInConfidenceRange;
			}
		}

		return unitToReactTo;
	}

	/**
	 * Function for generating / finding the Unit that this one has to react to
	 * when taking any defensive actions.
	 * 
	 * @return the Unit that this one should react to when taking defensive
	 *         actions.
	 */
	private Unit generateAttackingEnemyUnitToReactTo() {
		Unit unitToReactTo;

		if (this.closestAttackingEnemyUnitInConfidenceRange == null) {
			unitToReactTo = this.closestEnemyUnitInConfidenceRange;
		} else {
			unitToReactTo = this.closestAttackingEnemyUnitInConfidenceRange;
		}
		return unitToReactTo;
	}

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
	protected double generateModifiedConfidence() {
		Integer closestCenterDistance = this.generateClosestCenterDistance();
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
		// OR
		// If the Unit is invulnerable (No enemy is able to attack it) then set
		// the confidence to 1.0 since it can do whatever it wants (More
		// specifically can attack whatever it wants!).
		else if (closestCenterDistance == null || this.isInvulnerable()) {
			modifiedConfidence = 1.;
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
	 * Convenience function for
	 * {@link #generatePlayerAndEnemyStrengths(HashMap, HashMap)}.
	 * 
	 * @return the air strength of the Player and the enemy in the PlayerUnit's
	 *         confidence range.
	 */
	protected Pair<Double, Double> generatePlayerAndEnemyAirStrengths() {
		return this.generatePlayerAndEnemyStrengths(
				this.informationStorage.getTrackerInfo().getPlayerAirAttackTilePositions(),
				this.informationStorage.getTrackerInfo().getEnemyAirAttackTilePositions());
	}

	/**
	 * Convenience function for
	 * {@link #generatePlayerAndEnemyStrengths(HashMap, HashMap)}.
	 * 
	 * @return the ground strength of the Player and the enemy in the
	 *         PlayerUnit's confidence range.
	 */
	protected Pair<Double, Double> generatePlayerAndEnemyGroundStrengths() {
		return this.generatePlayerAndEnemyStrengths(
				this.informationStorage.getTrackerInfo().getPlayerGroundAttackTilePositions(),
				this.informationStorage.getTrackerInfo().getEnemyGroundAttackTilePositions());
	}

	/**
	 * Convenience function for
	 * {@link #generatePlayerAndEnemyStrengths(HashMap, HashMap)}.
	 * 
	 * @return the health strength of the Player and the enemy in the
	 *         PlayerUnit's confidence range.
	 */
	protected Pair<Double, Double> generatePlayerAndEnemyHealthStrengths() {
		return this.generatePlayerAndEnemyStrengths(
				this.informationStorage.getTrackerInfo().getPlayerHealthTilePositions(),
				this.informationStorage.getTrackerInfo().getEnemyHealthTilePositions());
	}

	/**
	 * Convenience function for
	 * {@link #generatePlayerAndEnemyStrengths(HashMap, HashMap)}.
	 * 
	 * @return the support strength of the Player and the enemy in the
	 *         PlayerUnit's confidence range.
	 */
	protected Pair<Double, Double> generatePlayerAndEnemySupportStrengths() {
		return this.generatePlayerAndEnemyStrengths(
				this.informationStorage.getTrackerInfo().getPlayerSupportTilePositions(),
				this.informationStorage.getTrackerInfo().getEnemySupportTilePositions());
	}

	/**
	 * Used to determine the strength of the PlayerUnits and the enemies by
	 * summing up their representative TileValues in the confidence radius
	 * around the Unit.
	 * 
	 * @param playerHashMap
	 *            the HashMap which provides the Player's values that are going
	 *            to be added together.
	 * @param enemyHashMap
	 *            the HashMap which provides the enemy's values that are going
	 *            to be added together.
	 * @return the strength of the Player and the enemy in the PlayerUnit's
	 *         confidence range.
	 */
	protected Pair<Double, Double> generatePlayerAndEnemyStrengths(HashMap<TilePosition, Integer> playerHashMap,
			HashMap<TilePosition, Integer> enemyHashMap) {
		List<Integer> enemyStrengths = new ArrayList<Integer>();
		List<Integer> playerStrengths = new ArrayList<Integer>();

		// Sum the total strength of the player and the enemy in a given radius
		// around the unit.
		for (int i = -CONFIDENCE_TILE_RADIUS; i <= CONFIDENCE_TILE_RADIUS; i++) {
			for (int j = -CONFIDENCE_TILE_RADIUS; j <= CONFIDENCE_TILE_RADIUS; j++) {
				TilePosition key = new TilePosition(this.unit.getTilePosition().getX() + i,
						this.unit.getTilePosition().getY() + j);
				int eStrength = enemyHashMap.getOrDefault(key, 0);
				int pStrength = playerHashMap.getOrDefault(key, 0);

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
	 * Function for checking for an unresolved issue that the Unit is currently
	 * experiencing. This can be i.e. the Unit being stuck or idling. Therefore
	 * a check must be performed so that the actions of the Unit are reset. This
	 * effectively causes the Unit to search for a new / other / same goal to
	 * pursue with a new set of actions. <br>
	 * <b>Note:</b><br>
	 * This is necessary since either an action of an Unit does not work
	 * correctly or a command passed to the BroodWar-API did not execute.
	 * Therefore a "hard reset" is necessary to ensure that Units i.e. do not
	 * stand in the base idling.
	 */
	private void checkForUnresolvedIssue() {
		if ((this.unit.isStuck() || this.unit.isIdle()) && this.issueStateWrapper.signalIssue()) {
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
				this.informationStorage.getBaselocationsSearched().put(location,
						Core.getInstance().getGame().elapsedTime());
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

	/**
	 * Function for extracting Units that match a provided List of UnitTypes
	 * from a HashSet of Units.
	 * 
	 * @param units
	 *            the Units which the matching ones are being extracted from.
	 * @param specialUnitTypes
	 *            the List of UnitTypes that define which Units are going to be
	 *            extracted.
	 * @return a HashSet of Units based on the provided units matching the
	 *         UnitTypes of the given List.
	 */
	public HashSet<Unit> getAttackableSpecialUnits(HashSet<Unit> units, List<UnitType> specialUnitTypes) {
		HashSet<Unit> attackableUnits = new HashSet<>();

		for (Unit unit : units) {
			if (specialUnitTypes.contains(unit.getType()) && this.canAttack(unit)) {
				attackableUnits.add(unit);
			}
		}
		return attackableUnits;
	}

	/**
	 * Function for extracting workers from a provided HashSet of Units that
	 * this Unit can attack.
	 * 
	 * @param units
	 *            the Units which the workers are being extracted from.
	 * @return a HashSet of Units which are the workers of the initial HashSet
	 *         that this Unit can attack.
	 */
	public HashSet<Unit> getAttackableWorkers(HashSet<Unit> units) {
		HashSet<Unit> attackableWorkers = new HashSet<>();

		for (Unit unit : units) {
			if (unit.getType().isWorker() && this.canAttack(unit)) {
				attackableWorkers.add(unit);
			}
		}
		return attackableWorkers;
	}

	/**
	 * Function for extracting enemy supply providers from a provided HashSet of
	 * Units that this Unit can attack.
	 * 
	 * @param units
	 *            the Units which the supply providers are being extracted from.
	 * @return a HashSet of Units which are the supply providers of the initial
	 *         HashSet that this Unit can attack.
	 */
	public HashSet<Unit> getAttackableEnemySupplyProviders(HashSet<Unit> units) {
		HashSet<Unit> attackableSupplyProviders = new HashSet<>();

		for (Unit unit : units) {
			if (unit.getType() == Core.getInstance().getGame().enemy().getRace().getSupplyProvider()
					&& this.canAttack(unit)) {
				attackableSupplyProviders.add(unit);
			}
		}
		return attackableSupplyProviders;
	}

	/**
	 * Function for extracting centers from a provided HashSet of Units that
	 * this Unit can attack.
	 * 
	 * @param units
	 *            the Units which the centers are being extracted from.
	 * @return a HashSet of Units which are the centers of the initial HashSet
	 *         that this Unit can attack.
	 */
	public HashSet<Unit> getAttackableEnemyCenters(HashSet<Unit> units) {
		HashSet<Unit> attackableCenters = new HashSet<>();

		for (Unit unit : units) {
			if (unit.getType() == Core.getInstance().getGame().enemy().getRace().getCenter() && this.canAttack(unit)) {
				attackableCenters.add(unit);
			}
		}
		return attackableCenters;
	}

	/**
	 * Function for extracting enemy Units that have either a ground or air
	 * weapon from a provided HashSet of Units that this Unit can attack.
	 * 
	 * @param units
	 *            the Units which the enemy Units are being extracted from.
	 * @return a HashSet of Units which are the enemy Units with weapons of the
	 *         initial HashSet that this Unit can attack.
	 */
	public HashSet<Unit> getAttackableUnitsWithWeapons(HashSet<Unit> units) {
		HashSet<Unit> attackableUnitsWithWeapons = new HashSet<>();

		for (Unit unit : units) {
			if (this.hasWeapon(unit) && this.canAttack(unit)) {
				attackableUnitsWithWeapons.add(unit);
			}
		}
		return attackableUnitsWithWeapons;
	}

	/**
	 * Function for extracting enemy Units that this Unit can attack (Flying,
	 * non flying) based on this Unit's weapon.
	 * 
	 * @param units
	 *            the Units which the attackable Units are being extracted from.
	 * @return a HashSet of attackable Units (Flying, non flying) of the initial
	 *         HashSet based on this Unit's weapon.
	 */
	public HashSet<Unit> getAttackableUnits(HashSet<Unit> units) {
		HashSet<Unit> attackableUnits = new HashSet<>();

		for (Unit unit : units) {
			if (this.canAttack(unit)) {
				attackableUnits.add(unit);
			}
		}
		return attackableUnits;
	}

	/**
	 * Function for getting the enemy Units in the provided HashSet that can
	 * attack it. This does NOT include their specific weapon range, but only
	 * the TYPE of weapon that they use. I.e. this function returns all enemies
	 * with ground weapons that can attack a ground Unit. If the Unit is
	 * airborne the function returns all enemy Units with air weapons that can
	 * attack flying Units.
	 * 
	 * @param units
	 *            the Units which the attacking Units are being extracted from.
	 * @return a HashSet of enemy Units of the provided HashSet that can attack
	 *         the Unit itself based on it's state (Flying, not flying).
	 */
	public HashSet<Unit> getAttackingUnits(HashSet<Unit> units) {
		HashSet<Unit> attackingUnits = new HashSet<>();

		for (Unit unit : units) {
			if (this.canBeAttackedBy(unit)) {
				attackingUnits.add(unit);
			}
		}
		return attackingUnits;
	}

	/**
	 * Function for checking if the provided Unit has either an air or a ground
	 * weapon to attack with.
	 * 
	 * @param unit
	 *            the Unit that is going to be checked.
	 * @return true if the Unit has either an air weapon or a ground weapon,
	 *         otherwise false.
	 */
	public boolean hasWeapon(Unit unit) {
		return (unit.getType().groundWeapon().damageAmount() > 0 || unit.getType().airWeapon().damageAmount() > 0);
	}

	/**
	 * Function for testing if the Unit can attack a given Unit. This takes the
	 * current Unit's weapon into account (Ground, air).
	 * 
	 * @param unit
	 *            the Unit that this Unit's weapons are being tested against.
	 * @return true if this Unit can attack the given one with it's weapons.
	 */
	public boolean canAttack(Unit unit) {
		WeaponType groundWeapon = this.unit.getType().groundWeapon();
		WeaponType airWeapon = this.unit.getType().airWeapon();

		return !isInvulnerable(unit)
				&& ((groundWeapon.targetsGround() && !unit.isFlying()) || (airWeapon.targetsAir() && unit.isFlying()));
	}

	/**
	 * Function for testing if the Unit can be attacked by a given Unit. This
	 * takes the provided Unit's weapon into account (Ground, air).
	 * 
	 * @param unit
	 *            the Unit whose weapons are being tested against this Unit.
	 * @return true if this Unit can be attacked by the given one.
	 */
	public boolean canBeAttackedBy(Unit unit) {
		WeaponType groundWeapon = unit.getType().groundWeapon();
		WeaponType airWeapon = unit.getType().airWeapon();

		return (groundWeapon.targetsGround() && !this.unit.isFlying())
				|| (airWeapon.targetsAir() && this.unit.isFlying());
	}

	/**
	 * Function for extracting the distance to the closest Player center
	 * building on the map. This function either returns the actual distance
	 * casted to int or null, if no center building is found.
	 * 
	 * @return the smallest distance to the therefore closest center building
	 *         casted to int or null if none is found.
	 */
	public Integer generateClosestCenterDistance() {
		Integer distance = null;
		Unit closestCenter = this.getClosestCenter();

		if (closestCenter != null) {
			distance = this.unit.getDistance(closestCenter);
		}
		return distance;
	}

	/**
	 * Function for extracting the center Unit that is the closest one to the
	 * current PlayerUnit's Position.
	 * 
	 * @return the closest center Unit.
	 */
	public Unit getClosestCenter() {
		HashSet<Unit> centers = this.informationStorage.getCurrentGameInformation().getCurrentUnits()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
		Unit closestCenter = null;

		if (centers != null) {
			closestCenter = this.getClosestUnit(centers);
		}
		return closestCenter;
	}

	/**
	 * Function for defining the StateFactory that will be used to determine the
	 * available actions, the world-and goal-states as well as the associated
	 * updaters.
	 * 
	 * @return the StateFactory that will be used for this PlayerUnit instance.
	 */
	protected abstract StateFactory createFactory();

	/**
	 * Function for testing if the confidence of the Unit is below the set
	 * threshold.
	 * 
	 * @return true if the confidence is below the set threshold, otherwise
	 *         false.
	 */
	public boolean isConfidenceBelowThreshold() {
		return isConfidenceBelowThreshold(this.confidence);
	}

	/**
	 * Function for testing if the confidence of the Unit is above the set
	 * threshold.
	 * 
	 * @return true if the confidence is above the set threshold, otherwise
	 *         false.
	 */
	public boolean isConfidenceAboveThreshold() {
		return isConfidenceAboveThreshold(this.confidence);
	}

	/**
	 * Function for testing if a confidence is below the set threshold.
	 * 
	 * @return true if the confidence is below the set threshold, otherwise
	 *         false.
	 */
	public static boolean isConfidenceBelowThreshold(Double confidence) {
		return confidence < CONFIDENCE_THRESHHOLD;
	}

	/**
	 * Function for testing if a confidence is above the set threshold.
	 * 
	 * @return true if the confidence is above the set threshold, otherwise
	 *         false.
	 */
	public static boolean isConfidenceAboveThreshold(Double confidence) {
		return confidence >= CONFIDENCE_THRESHHOLD;
	}

	/**
	 * Function for reseting all actions, values and references associated with
	 * the {@link PlayerUnit} instance. This function should only be called when
	 * the reference to this instance is discarded and therefore not being used
	 * anymore.
	 */
	public void destroy() {
		this.manuallyResetActions();

		// Remove the Unit from any assigned groups. This is necessary since
		// they are not removed from them by an outside force.
		BaseAction.removeGroupAssociations(this);
	}

	/**
	 * Function for manually resetting all current actions of the PlayerUnit.
	 * This is the equivalent of the Unit itself calling the
	 * {@link #resetActions()} function. Therefore this function should not be
	 * excessively used!
	 */
	public void manuallyResetActions() {
		this.resetActions();
	}

	/**
	 * Function for testing if the Unit itself is currently invulnerable. This
	 * is the case if the Unit is burrowed or cloaked and no enemy detector Unit
	 * is near it. Therefore no enemies are able to attack it => Invulnerable.
	 * 
	 * @return true if the Unit is invulnerable and not attackable, false if
	 *         not.
	 */
	public boolean isInvulnerable() {
		return isInvulnerable(this.unit);
	}

	/**
	 * Convenience function.
	 * 
	 * @see #isInvulnerable(Unit, int)
	 * 
	 * @param unit
	 *            the Unit that is going to be checked.
	 * @return true if the Unit is invulnerable and not attackable, false if
	 *         not.
	 */
	public static boolean isInvulnerable(Unit unit) {
		return isInvulnerable(unit, DEFAULT_DETECTION_TILERANGE);
	}

	/**
	 * Function for testing if an Unit is currently invulnerable. This is the
	 * case if the Unit is burrowed or cloaked and no detector Unit of the other
	 * Player is near it. Therefore no enemies are able to attack it =>
	 * Invulnerable.
	 * 
	 * @param unit
	 *            the Unit that is going to be checked.
	 * @param tileRange
	 *            the range in tiles that a possible enemy detection Unit must
	 *            be in.
	 * @return true if the Unit is invulnerable and not attackable, false if
	 *         not.
	 */
	public static boolean isInvulnerable(Unit unit, int tileRange) {
		return (unit.isBurrowed() || unit.isCloaked()) && !isDetected(unit, tileRange);
	}

	/**
	 * Convenience function.
	 * 
	 * @see #isDetected(Unit, int)
	 * 
	 * @param unit
	 *            the Unit that is going to be checked.
	 * @return true if the Unit is in the given tile range of an enemy detection
	 *         Unit.
	 */
	public static boolean isDetected(Unit unit) {
		return isDetected(unit, DEFAULT_DETECTION_TILERANGE);
	}

	/**
	 * Function for checking if the Unit is near an enemy detection Unit. This
	 * function can be applied to either side since it compares the Player of
	 * the provided Unit with the one of each checked Unit. <br>
	 * <b>Note:</b><br>
	 * This function is only necessary due to the default implementation of the
	 * "isDetected" function not working properly!
	 * 
	 * @param unit
	 *            the Unit that is going to be checked.
	 * @param tileRange
	 *            the range that a possible enemy detection Unit must be in for
	 *            this function to return true.
	 * @return true if the Unit is in the given tile range of an enemy detection
	 *         Unit.
	 */
	public static boolean isDetected(Unit unit, int tileRange) {
		Player player = unit.getPlayer();
		boolean isDetected = false;

		for (Unit unitInRadius : unit.getUnitsInRadius(tileRange * Core.getInstance().getTileSize())) {
			if (unitInRadius != unit && unitInRadius.getPlayer() != player && unitInRadius.getType().isDetector()) {
				isDetected = true;

				break;
			}
		}
		return isDetected;
	}

	// -------------------- RetreatUnit

	public Point defineCurrentPosition() {
		return new Point(this.unit.getPosition());
	}

	// ------------------------------ Getter / Setter

	public Unit getUnit() {
		return this.unit;
	}

	public double getConfidence() {
		return confidence;
	}

	public Unit getClosestEnemyUnitInConfidenceRange() {
		return this.closestEnemyUnitInConfidenceRange;
	}

	public Unit getClosestAttackableEnemyUnitWithWeapon() {
		return closestAttackableEnemyUnitWithWeapon;
	}

	public Unit getClosestAttackableEnemyUnitInConfidenceRange() {
		return closestAttackableEnemyUnitInConfidenceRange;
	}

	public Unit getClosestAttackableEnemySpecialUnitInConfidenceRange() {
		return closestAttackableEnemySpecialUnitInConfidenceRange;
	}

	public Unit getClosestAttackableEnemyWorkerInConfidenceRange() {
		return closestAttackableEnemyWorkerInConfidenceRange;
	}

	public Unit getClosestAttackableEnemySupplyProviderInConfidenceRange() {
		return closestAttackableEnemySupplyProviderInConfidenceRange;
	}

	public Unit getClosestAttackableEnemyCenterInConfidenceRange() {
		return closestAttackableEnemyCenterInConfidenceRange;
	}

	public Unit getAttackableEnemyUnitToReactTo() {
		return attackableEnemyUnitToReactTo;
	}

	public Unit getClosestAttackingEnemyUnitInConfidenceRange() {
		return closestAttackingEnemyUnitInConfidenceRange;
	}

	public Unit getAttackingEnemyUnitToReactTo() {
		return attackingEnemyUnitToReactTo;
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
