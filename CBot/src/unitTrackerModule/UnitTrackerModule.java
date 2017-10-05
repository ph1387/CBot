package unitTrackerModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.WeaponType;
import core.Core;
import informationStorage.CurrentGameInformation;
import informationStorage.InformationStorage;
import informationStorage.UnitTrackerInformation;

/**
 * UnitTrackerModule.java --- Module for tracking enemy units and storing
 * information regarding their position and strength. Also stores information
 * regarding the players unit strength.
 * 
 * @author P H - 31.01.2017
 *
 */
public class UnitTrackerModule {

	private static final int MAX_TIME_UNTIL_OUTDATED = 20;

	// TODO: UML ADD
	// The frame difference after which the Class performs the update.
	private static final int FRAME_UPDATE_DIFF = 24;
	// TODO: UML ADD
	private int lastUpdateTimeStamp = 0;

	// TODO: UML ADD
	// The UnitTypes that are ignored in all tracking instances. This does
	// include the ground / air strength as well as the health and other types!
	private List<UnitType> ignoredUnitTypes = Arrays.asList(new UnitType[] { UnitType.Terran_Vulture_Spider_Mine });

	// Tracking information
	private HashMap<TilePosition, Integer> playerAirAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> playerGroundAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> playerHealthTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> playerSupportTilePositions = new HashMap<>();

	private HashMap<TilePosition, Integer> enemyAirAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyGroundAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyHealthTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemySupportTilePositions = new HashMap<>();
	private List<EnemyUnit> enemyBuildings = new ArrayList<EnemyUnit>();
	private List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>();

	// Current Player / Game information.
	private int unitCountTotal;
	private int unitCountWorkers;
	private int unitCountBuildings;
	private int unitCountCombat;
	private double currentWorkerPercent;
	private double currentBuildingsPercent;
	private double currentCombatUnitsPercent;
	private HashMap<UnitType, HashSet<Unit>> currentUnits = new HashMap<>();

	// The container holding all information.
	private InformationStorage informationStorage;

	// TODO: UML ADD
	// The multiplier for either cloaked or burrowed Units that are not detected
	// by a detector Unit. Therefore the Player's Units are unable to attack
	// them.
	private double invulnerableMultiplier = 5.0;

	// TODO: UML ADD
	// The multiplier that is applied to the enemy buildings that have a weapon.
	// This is needed since stationary turrets etc. must NOT be rushed into!
	private double buildingMultiplier = 2.5;
	// TODO: UML ADD
	// The tile range which gets added towards each Unit's default weapon range.
	// Using this value Units can react sooner to incoming threats.
	private int extraTileRange = 3;
	// TODO: UML ADD
	// The multiplier for workers since they are normally not used as combat
	// Units. Therefore their strength must be adjusted.
	private double workerMultiplier = 0.3;

	// Needed for increasing the multiplier in the calculations that generates
	// the total value of the weapon types. Any positive number can be inserted
	// here. The bigger the number, the stronger the effect of a shorter range
	// is.
	private int generalMultiplier = 41;
	private int generalDPSMultiplier = 100;
	// The general health multiplier that will be used by the Player's Units as
	// well as the enemy ones.
	private int generalHealthMultiplier = 1000;
	// The general support multiplier that will be used by the Player's Units as
	// well as the enemy ones.
	private int generalSupportMultiplier = 250;
	// The tile range that each support Unit will use as radius for it's effect.
	private int supportEffectTileRange = 3;
	// The UnitTypes that are considered support Units.
	private List<UnitType> supportUnitTypes = Arrays
			.asList(new UnitType[] { UnitType.Terran_Medic, UnitType.Terran_Science_Vessel, UnitType.Zerg_Queen,
					UnitType.Zerg_Defiler, UnitType.Protoss_Corsair, UnitType.Protoss_Arbiter });
	// The generated dividers that are / were used for calculating the
	// multipliers for the different attack ranges. These can be stored and do
	// not need to be calculated again since they are the same for each inserted
	// range.
	private HashMap<Integer, Double> generatedDividers = new HashMap<>();

	public UnitTrackerModule(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	/**
	 * Used for updating all information regarding enemy Units in the game.
	 */
	public void update() {
		// Use a fixed update cycle to prevent the tracker from taking too many
		// CPU resources and therefore slowing the frame rate of the game down
		// (m*O(n) instead of O(n*m)).
		if (Core.getInstance().getGame().getFrameCount() - this.lastUpdateTimeStamp >= FRAME_UPDATE_DIFF) {
			this.updateCurrentGameInformation();
			this.updateEnemyUnitLists();
			this.updateTilePositionInformation();
			this.forwardInformation();

			this.lastUpdateTimeStamp = Core.getInstance().getGame().getFrameCount();
		}

		UnitTrackerDisplay.showBuildingsLastPosition(this.enemyBuildings);
		UnitTrackerDisplay.showUnitsLastPosition(this.enemyUnits);

		// Update the display of the calculated combat values of the ground and
		// air forces of the enemy and the player. Player has to the shown
		// first, since the enemy list might be empty which would result in none
		// of them being shown.
		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayPlayerAirStrength()) {
			UnitTrackerDisplay.showPlayerUnitTileStrength(this.playerAirAttackTilePositions);
		}
		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayPlayerGroundStrength()) {
			UnitTrackerDisplay.showPlayerUnitTileStrength(this.playerGroundAttackTilePositions);
		}
		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayPlayerHealthStrength()) {
			UnitTrackerDisplay.showPlayerUnitTileStrength(this.playerHealthTilePositions);
		}
		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayPlayerSupportStrength()) {
			UnitTrackerDisplay.showPlayerUnitTileStrength(this.playerSupportTilePositions);
		}

		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayEnemyAirStrength()) {
			UnitTrackerDisplay.showEnemyUnitTileStrength(this.enemyAirAttackTilePositions);
		}
		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayEnemyGroundStrength()) {
			UnitTrackerDisplay.showEnemyUnitTileStrength(this.enemyGroundAttackTilePositions);
		}
		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayEnemyHealthStrength()) {
			UnitTrackerDisplay.showEnemyUnitTileStrength(this.enemyHealthTilePositions);
		}
		if (this.informationStorage.getiUnitTrackerModuleConfig().enableDisplayEnemySupportStrength()) {
			UnitTrackerDisplay.showEnemyUnitTileStrength(this.enemySupportTilePositions);
		}
	}

	/**
	 * Function for updating the shared storage instance with the current game
	 * information like worker and combat Unit counts.
	 */
	private void updateCurrentGameInformation() {
		// Reset any previous set information.
		this.currentUnits = new HashMap<>();
		this.unitCountWorkers = 0;
		this.unitCountBuildings = 0;
		this.unitCountCombat = 0;
		this.unitCountTotal = 0;

		// Extract all necessary information from the current state of the game.
		// This does NOT include any building Queues etc. These factors must be
		// considered elsewhere.
		// Units:
		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			UnitType type = unit.getType();

			// Add and instantiate a new HashSet if none is found.
			if (!this.currentUnits.containsKey(type)) {
				this.currentUnits.put(type, new HashSet<Unit>());
			}

			this.currentUnits.get(type).add(unit);

			// Count the different types of Units.
			if (type.isWorker()) {
				this.unitCountWorkers++;
			} else if (type.isBuilding()) {
				this.unitCountBuildings++;
			} else {
				this.unitCountCombat++;
			}
			this.unitCountTotal++;
		}

		// Calculate the percentage representation of the Units:
		this.currentWorkerPercent = ((double) (this.unitCountWorkers)) / ((double) (this.unitCountTotal));
		this.currentBuildingsPercent = ((double) (this.unitCountBuildings)) / ((double) (this.unitCountTotal));
		this.currentCombatUnitsPercent = ((double) (this.unitCountCombat)) / ((double) (this.unitCountTotal));
	}

	/**
	 * Function for updating all lists regarding the enemies units and
	 * buildings.
	 */
	private void updateEnemyUnitLists() {
		this.verifyKnownTiles(this.enemyBuildings);
		this.verifyKnownTiles(this.enemyUnits);

		this.addVisibleUnits(this.enemyUnits, this.enemyBuildings);

		this.removeOutdatedEntries(this.enemyUnits);
	}

	/**
	 * Forward all received information to the information storage for all other
	 * modules to react to.
	 */
	private void forwardInformation() {
		UnitTrackerInformation trackerInfo = this.informationStorage.getTrackerInfo();
		CurrentGameInformation currentGameInfo = this.informationStorage.getCurrentGameInformation();

		// Forward the UnitTrackerModule information.
		trackerInfo.setPlayerAirAttackTilePositions(this.playerAirAttackTilePositions);
		trackerInfo.setPlayerGroundAttackTilePositions(this.playerGroundAttackTilePositions);
		trackerInfo.setPlayerHealthTilePositions(this.playerHealthTilePositions);
		trackerInfo.setPlayerSupportTilePositions(this.playerSupportTilePositions);
		trackerInfo.setEnemyAirAttackTilePositions(this.enemyAirAttackTilePositions);
		trackerInfo.setEnemyGroundAttackTilePositions(this.enemyGroundAttackTilePositions);
		trackerInfo.setEnemyHealthTilePositions(this.enemyHealthTilePositions);
		trackerInfo.setEnemySupportTilePositions(this.enemySupportTilePositions);
		trackerInfo.setEnemyBuildings(this.enemyBuildings);
		trackerInfo.setEnemyUnits(this.enemyUnits);

		// Forward the current Player / Game information.
		currentGameInfo.setCurrentUnitCountTotal(this.unitCountTotal);
		currentGameInfo.setCurrentWorkerCount(this.unitCountWorkers);
		currentGameInfo.setCurrentBuildingCount(this.unitCountBuildings);
		currentGameInfo.setCurrentCombatUnitCount(this.unitCountCombat);
		currentGameInfo.setCurrentWorkerPercent(this.currentWorkerPercent);
		currentGameInfo.setCurrentBuildingsPercent(this.currentBuildingsPercent);
		currentGameInfo.setCurrentCombatUnitsPercent(this.currentCombatUnitsPercent);
		currentGameInfo.setCurrentUnits(this.currentUnits);
	}

	/**
	 * Add visible units to the corresponding lists if they are not already in
	 * them.
	 * 
	 * @param unitList
	 *            the List the units are being added to.
	 * @param buildingList
	 *            the List the buildings are being added to.
	 */
	private void addVisibleUnits(List<EnemyUnit> unitList, List<EnemyUnit> buildingList) {
		Game game = Core.getInstance().getGame();

		for (Unit unit : game.enemy().getUnits()) {
			if (unit.isVisible()) {
				EnemyUnit newEnemyUnit = new EnemyUnit(unit.getTilePosition(), unit, game.elapsedTime());

				// TilePosition has not been added to the corresponding list yet
				if (unit.getType().isBuilding() && !this.isInUnitList(this.enemyBuildings, unit)) {
					buildingList.add(newEnemyUnit);
				} else if (!this.isInUnitList(this.enemyUnits, unit)) {
					unitList.add(newEnemyUnit);
				}
			}
		}
	}

	/**
	 * Test if a units TilePosition is in a enemy unit list.
	 * 
	 * @param unitList
	 *            the List that is being checked.
	 * @param unit
	 *            the unit that is being searched for.
	 * @return true or false depending if the units TilePosition was found.
	 */
	private boolean isInUnitList(List<EnemyUnit> unitList, Unit unit) {
		boolean isInList = false;

		for (int i = 0; i < unitList.size() && !isInList; i++) {
			if (unitList.get(i).getLastSeenTilePosition().equals(unit.getTilePosition())) {
				isInList = true;
			}
		}

		return isInList;
	}

	/**
	 * Validate units in a unit List with the currently seen units on the map.
	 * If the unit is not at the saved location, remove the entry from the list.
	 *
	 * @param unitList
	 *            the List of which all entries are being checked.
	 */
	private void verifyKnownTiles(List<EnemyUnit> unitList) {
		Game game = Core.getInstance().getGame();
		List<Unit> enemieUnits = game.enemy().getUnits();

		// Since units get removed, start at the end of the list (-> left
		// shifting)
		for (int i = unitList.size() - 1; i >= 0; i--) {
			EnemyUnit unit = unitList.get(i);

			if (game.isVisible(unit.getLastSeenTilePosition())) {
				boolean missing = true;

				// Iterate through all visible enemy units and remove any
				// entries, which are not up to date
				for (int j = 0; j < enemieUnits.size() && missing; j++) {
					if (enemieUnits.get(j).isVisible() && unit.getUnit() == enemieUnits.get(j)) {
						unit.setTimestampLastSeen(game.elapsedTime());
						unit.setLastSeenTilePosition(enemieUnits.get(j).getTilePosition());
						missing = false;
					}
				}

				if (missing) {
					unitList.remove(i);
				}
			}
		}
	}

	/**
	 * Function for removing all old, outdated entries in the given List, since
	 * units are unknown as long as they are not seen even if they are saved
	 * inside a variable.
	 *
	 * @param unitList
	 *            the List of which outdated entries are being removed.
	 */
	private void removeOutdatedEntries(List<EnemyUnit> unitList) {
		Game game = Core.getInstance().getGame();

		// Since units get removed, start at the end of the list (-> left
		// shifting)
		for (int i = unitList.size() - 1; i >= 0; i--) {
			EnemyUnit enemyUnit = unitList.get(i);
			UnitType enemyUnitType = enemyUnit.getUnitType();
			// All buildings that have a weapon (Air OR ground) must NOT be
			// removed since they influence the confidence of their surrounding
			// Units.
			boolean isNoAttackingBuilding = !(enemyUnitType.isBuilding()
					&& ((enemyUnitType.groundWeapon() != null && enemyUnitType.groundWeapon().damageAmount() > 0)
							|| (enemyUnitType.airWeapon() != null && enemyUnitType.airWeapon().damageAmount() > 0)));

			if (isNoAttackingBuilding
					&& enemyUnit.getTimestampLastSeen() + MAX_TIME_UNTIL_OUTDATED <= game.elapsedTime()) {
				unitList.remove(i);
			}
		}
	}

	/**
	 * Function for updating all information regarding the Player's and the
	 * enemy's different strengths on the TilePositions.
	 */
	private void updateTilePositionInformation() {
		this.updatePlayerTilePositionInformation();
		this.updateEnemyTilePositionInformation();
	}

	/**
	 * Function for updating all information regarding the Player's strengths.
	 */
	private void updatePlayerTilePositionInformation() {
		HashMap<TilePosition, Integer> playerAir = new HashMap<>();
		HashMap<TilePosition, Integer> playerGround = new HashMap<>();
		HashMap<TilePosition, Integer> playerHealth = new HashMap<>();
		HashMap<TilePosition, Integer> playerSupport = new HashMap<>();

		// Generate all Player strengths.
		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (!this.ignoredUnitTypes.contains(unit.getType())) {
				double unitHealthMultiplier = this.generateUnitMultiplier(unit);

				// TODO: Possible Change: Consider upgrades.
				// Player Air:
				if (unit.isCompleted() && unit.getType().airWeapon() != null
						&& unit.getType().airWeapon().damageAmount() > 0) {
					this.addValueInAreaToTilePositionValue(unit.getTilePosition(), playerAir,
							this.generateAttackGenerationInformation(unit.getType().airWeapon(), unitHealthMultiplier));
				}

				// TODO: Possible Change: Consider upgrades.
				// Player Ground:
				if (unit.isCompleted() && unit.getType().groundWeapon() != null
						&& unit.getType().groundWeapon().damageAmount() > 0) {
					this.addValueInAreaToTilePositionValue(unit.getTilePosition(), playerGround, this
							.generateAttackGenerationInformation(unit.getType().groundWeapon(), unitHealthMultiplier));
				}

				// TODO: Possible Change: Consider upgrades.
				// Player Health:
				if (unit.isCompleted() && ((unit.getType().groundWeapon() != null
						&& unit.getType().groundWeapon().damageAmount() > 0)
						|| (unit.getType().airWeapon() != null && unit.getType().airWeapon().damageAmount() > 0))) {
					this.addValueInAreaToTilePositionValue(unit.getTilePosition(), playerHealth,
							this.generateHealthGenerationInformation(unit.getType(), unitHealthMultiplier));
				}

				// TODO: Possible Change: Consider upgrades.
				// Player Support:
				if (this.supportUnitTypes.contains(unit.getType()) && unit.isCompleted()) {
					this.addValueInAreaToTilePositionValue(unit.getTilePosition(), playerSupport,
							this.generateSupportGenerationInformation(unitHealthMultiplier));
				}
			}
		}

		this.playerAirAttackTilePositions = playerAir;
		this.playerGroundAttackTilePositions = playerGround;
		this.playerHealthTilePositions = playerHealth;
		this.playerSupportTilePositions = playerSupport;
	}

	/**
	 * Function for updating all information regarding the enemy's strengths.
	 * This includes buildings as well as Units.
	 */
	private void updateEnemyTilePositionInformation() {
		HashMap<TilePosition, Integer> enemyAir = new HashMap<>();
		HashMap<TilePosition, Integer> enemyGround = new HashMap<>();
		HashMap<TilePosition, Integer> enemyHealth = new HashMap<>();
		HashMap<TilePosition, Integer> enemySupport = new HashMap<>();

		// Generate all enemy strengths. Differentiate between buildings and
		// Units.
		// Units:
		for (EnemyUnit enemyUnit : this.enemyUnits) {
			if (!this.ignoredUnitTypes.contains(enemyUnit.getUnitType())) {
				this.generateEnemyUnitTilePositions(enemyUnit, enemyAir, enemyGround, enemyHealth, enemySupport);
			}
		}

		// Buildings:
		for (EnemyUnit enemyBuilding : this.enemyBuildings) {
			if (!this.ignoredUnitTypes.contains(enemyBuilding.getUnitType())) {
				this.generateEnemyUnitTilePositions(enemyBuilding, enemyAir, enemyGround, enemyHealth, enemySupport);
			}
		}

		this.enemyAirAttackTilePositions = enemyAir;
		this.enemyGroundAttackTilePositions = enemyGround;
		this.enemyHealthTilePositions = enemyHealth;
		this.enemySupportTilePositions = enemySupport;
	}

	/**
	 * Function for actually updating the values of the different types of
	 * strengths the enemy possesses which are added towards the provided
	 * HashMaps.
	 * 
	 * @param enemyUnit
	 *            the EnemyUnit whose strengths are going to be added towards
	 *            the different HashMaps
	 * @param enemyAir
	 *            the HashMap containing the values regarding the enemy's air
	 *            strengths.
	 * @param enemyGround
	 *            the HashMap containing the values regarding the enemy's ground
	 *            strengths.
	 * @param enemyHealth
	 *            the HashMap containing the values regarding the enemy's health
	 *            strengths.
	 * @param enemySupport
	 *            the HashMap containing the values regarding the enemy's
	 *            support strengths.
	 */
	private void generateEnemyUnitTilePositions(EnemyUnit enemyUnit, HashMap<TilePosition, Integer> enemyAir,
			HashMap<TilePosition, Integer> enemyGround, HashMap<TilePosition, Integer> enemyHealth,
			HashMap<TilePosition, Integer> enemySupport) {
		// TODO: Possible Change: Consider upgrades. (+ Health and support!)
		boolean enemyIsKnownOf = enemyUnit.getUnit().isVisible() && enemyUnit.getUnit().isCompleted()
				|| !enemyUnit.getUnit().isVisible();
		boolean hasValidGroundWeapon = enemyUnit.getUnitType().groundWeapon() != null
				&& enemyUnit.getUnitType().groundWeapon().damageAmount() > 0;
		boolean hasValidAirWeapon = enemyUnit.getUnitType().airWeapon() != null
				&& enemyUnit.getUnitType().airWeapon().damageAmount() > 0;
		double unitHealthMultiplier;

		// Generate different multipliers based on the visibility of the enemy
		// Unit.
		if (enemyUnit.getUnit().isVisible()) {
			unitHealthMultiplier = this.generateUnitMultiplier(enemyUnit.getUnit());
		} else {
			unitHealthMultiplier = this.generateEnemyUnitMultiplier(enemyUnit);
		}

		// Enemy Air:
		if (hasValidAirWeapon && enemyIsKnownOf) {
			this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), enemyAir, this
					.generateAttackGenerationInformation(enemyUnit.getUnitType().airWeapon(), unitHealthMultiplier));
		}

		// Enemy Ground:
		if (hasValidGroundWeapon && enemyIsKnownOf) {
			this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), enemyGround, this
					.generateAttackGenerationInformation(enemyUnit.getUnitType().groundWeapon(), unitHealthMultiplier));
		}

		// Enemy Health:
		if (enemyIsKnownOf && (hasValidGroundWeapon || hasValidAirWeapon)) {
			this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), enemyHealth,
					this.generateHealthGenerationInformation(enemyUnit.getUnitType(), unitHealthMultiplier));
		}

		// Enemy Support:
		if (enemyIsKnownOf && this.supportUnitTypes.contains(enemyUnit.getUnitType())) {
			this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), enemySupport,
					this.generateSupportGenerationInformation(unitHealthMultiplier));
		}
	}

	/**
	 * Function for generating a multiplier for an EnemyUnit. This includes all
	 * kinds of information like types, shields or life.
	 * 
	 * @param enemyUnit
	 *            the EnemyUnit for which a multiplier is being created.
	 * @return a multiplier for an EnemyUnit which resembles it's strength.
	 */
	private double generateEnemyUnitMultiplier(EnemyUnit enemyUnit) {
		double multiplier = 1.;

		if (enemyUnit.getUnitType().isBuilding()) {
			multiplier *= this.buildingMultiplier;
		} else if (enemyUnit.getUnitType().isWorker()) {
			multiplier *= this.workerMultiplier;
		} else if (enemyUnit.isInvulnerable()) {
			multiplier *= this.invulnerableMultiplier;
		}

		return multiplier;
	}

	/**
	 * Function for generating a multiplier for an Unit. This includes all kinds
	 * of information like types, shields or life.
	 * 
	 * @param playerUnit
	 *            the Unit for which a multiplier is being created.
	 * @return a multiplier for an Unit which resembles it's strength.
	 */
	private double generateUnitMultiplier(Unit unit) {
		double multiplier;

		// Only account the health + shield difference for non building Units!
		// This is due to defensive structures being a constant threat.
		if (unit.getType().isBuilding()) {
			multiplier = this.buildingMultiplier;
		} else if (unit.getType().isWorker()) {
			multiplier = this.workerMultiplier;
		} else {
			// The Unit has a shield and therefore its value must be considered.
			if (unit.getType().maxShields() > 0) {
				multiplier = (double) (unit.getHitPoints() + unit.getShields())
						/ (double) (unit.getType().maxHitPoints() + unit.getType().maxShields());
			}
			// The Unit has no shield and only its health matters.
			else {
				multiplier = (double) (unit.getHitPoints()) / (double) (unit.getType().maxHitPoints());
			}

			// The Unit can not be targeted by the Player's Units.
			if ((unit.isBurrowed() || unit.isCloaked()) && !unit.isDetected()) {
				multiplier += this.invulnerableMultiplier;
			}
		}

		return multiplier;
	}

	/**
	 * Function for generating a {@link TilePositionValueGenerationInformation}
	 * instance containing the attack value of a UnitType based on the provided
	 * {@link WeaponType}.
	 * 
	 * @param weaponType
	 *            the weapon type on which the
	 *            {@link TilePositionValueGenerationInformation} instance's
	 *            multiplier will be based on.
	 * @param unitHealthMultiplier
	 *            the health multiplier of the Unit that is going to be
	 *            associated with the provided {@link WeaponType}.
	 * @return a {@link TilePositionValueGenerationInformation} instance
	 *         containing the range of the {@link WeaponType} as well as a
	 *         multiplier matching the strength of the Unit combined with the
	 *         provided {@link WeaponType}.
	 */
	private TilePositionValueGenerationInformation generateAttackGenerationInformation(WeaponType weaponType,
			double unitHealthMultiplier) {
		int maxAttackTileRange = (int) (Double.valueOf(weaponType.maxRange())
				/ Double.valueOf(Core.getInstance().getTileSize()));

		// If the unit is a melee unit, the attack range is 0 and there will be
		// no calculations regarding the ValueTile lists. So the range has to be
		// set to 1.
		maxAttackTileRange = (int) (Math.max(maxAttackTileRange, 1));

		// The dps multiplier for each Unit.
		double dpsMultiplier = Double.valueOf(this.generalDPSMultiplier)
				* new Double(weaponType.damageAmount() * weaponType.damageFactor())
				/ new Double(weaponType.damageCooldown());

		// The multiplier that is going to be used in the strength calculations
		// for the different TilePositions.
		double multiplier = new Double(this.generalMultiplier * dpsMultiplier * unitHealthMultiplier)
				/ this.generateDivider(maxAttackTileRange);

		return new TilePositionValueGenerationInformation(maxAttackTileRange, multiplier);
	}

	/**
	 * Function for generating a {@link TilePositionValueGenerationInformation}
	 * instance containing the health value of a UnitType based on the provided
	 * {@link UnitType}.
	 * 
	 * @param unitType
	 *            the {@link UnitType} on which the
	 *            {@link TilePositionValueGenerationInformation} instance's
	 *            multiplier will be based on.
	 * @param unitHealthMultiplier
	 *            the health multiplier of the Unit that is going to be
	 *            associated with the provided {@link UnitType}.
	 * @return a {@link TilePositionValueGenerationInformation} instance
	 *         containing the provided {@link UnitType}s health and shield
	 *         values combined with the provided health multiplier of the Unit.
	 */
	private TilePositionValueGenerationInformation generateHealthGenerationInformation(UnitType unitType,
			double unitHealthMultiplier) {
		int tileRange = (int) ((Math.max(unitType.airWeapon().maxRange(), unitType.groundWeapon().maxRange()))
				/ (double) (Core.getInstance().getTileSize()));

		// Prevent division by 0.
		tileRange = (int) (Math.max(tileRange, 1));

		double multiplier = (Double.valueOf(this.generalHealthMultiplier) * unitHealthMultiplier)
				/ this.generateDivider(tileRange);

		return new TilePositionValueGenerationInformation(tileRange, multiplier);
	}

	/**
	 * Function for generating a {@link TilePositionValueGenerationInformation}
	 * instance containing the fixed support value of a UnitType based.
	 * 
	 * @param unitHealthMultiplier
	 *            the health multiplier of the Unit that is going to be
	 *            associated with this instance.
	 * @return a {@link TilePositionValueGenerationInformation} instance
	 *         containing the provided multiplier as well as a fixed tile range.
	 */
	private TilePositionValueGenerationInformation generateSupportGenerationInformation(double unitHealthMultiplier) {
		double multiplier = Double.valueOf(this.generalSupportMultiplier)
				/ this.generateDivider(this.supportEffectTileRange);

		return new TilePositionValueGenerationInformation(this.supportEffectTileRange, multiplier);
	}

	/**
	 * Function for generating a matching divider for the function
	 * {@link #addValueInAreaToTilePositionValue(TilePosition, HashMap, TilePositionValueGenerationInformation)}
	 * given the <b>same</b> tile range.
	 * 
	 * @param input
	 *            the range for which a matching divider will be generated.
	 * @return a matching divider for the provided range / input that can be
	 *         used with the named function (With the same range!).
	 */
	private double generateDivider(int input) {
		// No need to generate the same values over and over.
		if (!this.generatedDividers.containsKey(input)) {
			this.generatedDividers.put(input, (double) (8. * Math.pow(input, 2) * Math.sin(1.)));
		}

		return this.generatedDividers.get(input);
	}

	/**
	 * TilePositionValueGenerationInformation.java --- Class for storing
	 * temporary information that will be used for generating the different
	 * values that will be added towards the HashMaps representing the strengths
	 * of both the Player as well as the enemy in different kinds of fields.
	 * 
	 * @author P H - 13.09.2017
	 *
	 */
	private class TilePositionValueGenerationInformation {

		private int tileRange;
		private double multiplier;

		public TilePositionValueGenerationInformation(int tileRange, double multiplier) {
			this.tileRange = tileRange;
			this.multiplier = multiplier;
		}

		public int getTileRange() {
			return tileRange;
		}

		public double getMultiplier() {
			return multiplier;
		}

	}

	/**
	 * Function for adding a units strength to the corresponding
	 * ValueTilePosition table. The added strength and the range at which these
	 * are being added are determined by different factors.
	 *
	 * @param tilePosition
	 *            the TilePosition the calculations are being done around.
	 * @param valueTiles
	 *            the table of all ValueTiles the function can work with.
	 * @param generationInformation
	 *            the storage instance that holds information regarding the tile
	 *            range and the multiplier that are going to be used in the
	 *            following calculations.
	 */
	private void addValueInAreaToTilePositionValue(TilePosition tilePosition, HashMap<TilePosition, Integer> valueTiles,
			TilePositionValueGenerationInformation generationInformation) {
		int maxTileRange = generationInformation.getTileRange() + this.extraTileRange;

		// Fill the tiles which the Unit can reach with the appropriate values.
		for (int i = -maxTileRange; i <= maxTileRange; i++) {
			for (int j = -maxTileRange; j <= maxTileRange; j++) {
				if (tilePosition.getX() + i >= 0 && tilePosition.getY() + j >= 0) {
					TilePosition mappedTilePosition = new TilePosition(tilePosition.getX() + i,
							tilePosition.getY() + j);
					Integer foundIntegerValue = valueTiles.get(mappedTilePosition);

					if (foundIntegerValue == null) {
						foundIntegerValue = 0;
					}

					// Add the strength of the unit to the tiles value
					// proportional to the distance between the units tile and
					// the current tile. The function used here creates together
					// with the multiplier used a 3d cone shaped object whose
					// integral is the damage of the unit multiplied by the
					// general multiplier. Therefore the units strength is
					// accurately displayed in a circle around itself based on
					// the "strength" of the unit and the range (of its attack).
					// Using this method ranged units do not get inaccurate
					// strength values regarding their superior range as their
					// strength is proportionally mapped to it using a general
					// multiplier.
					Integer sum = foundIntegerValue + (int) ((Math.cos(i / maxTileRange) + Math.cos(j / maxTileRange))
							* generationInformation.getMultiplier());
					valueTiles.put(mappedTilePosition, sum);
				}
			}
		}
	}

	// ------------------------------ Getter / Setter

	public List<EnemyUnit> getEnemyBuildings() {
		return this.enemyBuildings;
	}

	public List<EnemyUnit> getEnemyUnits() {
		return this.enemyUnits;
	}

	public HashMap<TilePosition, Integer> getPlayerAirAttackTilePositions() {
		return this.playerAirAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getPlayerGroundAttackTilePositions() {
		return this.playerGroundAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getPlayerHealthTilePositions() {
		return playerHealthTilePositions;
	}

	public HashMap<TilePosition, Integer> getPlayerSupportTilePositions() {
		return playerSupportTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemyAirAttackTilePositions() {
		return this.enemyAirAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemyGroundAttackTilePositions() {
		return this.enemyGroundAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemyHealthTilePositions() {
		return enemyHealthTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemySupportTilePositions() {
		return enemySupportTilePositions;
	}

}
