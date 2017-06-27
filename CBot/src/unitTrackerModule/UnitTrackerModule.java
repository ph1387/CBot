package unitTrackerModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.WeaponType;
import core.Core;
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
	private static final double SHIELD_MULTIPLIER = 1.2;
	private static final double HEALTH_MULTIPLIER = 1.1;
	private static final int MAX_TIME_UNTIL_OUTDATED = 20;

	private static boolean enablePlayerStrength = true;
	private static boolean enableEnemyStrength = true;
	
	// Tracking information
	private HashMap<TilePosition, Integer> playerAirAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> playerGroundAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyAirAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyGroundAttackTilePositions = new HashMap<>();
	private List<EnemyUnit> enemyBuildings = new ArrayList<EnemyUnit>();
	private List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>();

	private InformationStorage informationStorage;

	public UnitTrackerModule(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	/**
	 * Used for updating all information regarding enemy Units in the game.
	 */
	public void update() {
		this.updateEnemyUnitLists();
		this.forwardInformation();

		UnitTrackerDisplay.showBuildingsLastPosition(this.enemyBuildings);
		UnitTrackerDisplay.showUnitsLastPosition(this.enemyUnits);

		// Update the display of the calculated combat values of the ground and
		// air forces of the enemy and the player. Player has to the shown
		// first, since the enemy list might be empty which would result in none
		// of them being shown.
		if(enablePlayerStrength) {
			UnitTrackerDisplay.showPlayerUnitTileStrength(this.playerGroundAttackTilePositions);
		}
		if(enableEnemyStrength) {
			UnitTrackerDisplay.showEnemyUnitTileStrength(this.enemyGroundAttackTilePositions);
		}
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

		this.enemyAirAttackTilePositions = this.generateEnemyAirAttackTilePositions();
		this.enemyGroundAttackTilePositions = this.generateEnemyGroundAttackTilePositions();
		this.playerAirAttackTilePositions = this.generatePlayerAirAttackTilePositions();
		this.playerGroundAttackTilePositions = this.generatePlayerGroundAttackTilePositions();
	}

	/**
	 * Forward all received information to the information storage for all other
	 * modules to react to.
	 */
	private void forwardInformation() {
		UnitTrackerInformation trackerInfo = this.informationStorage.getTrackerInfo();

		// Forward the UnitTrackerModule information.
		trackerInfo.setPlayerAirAttackTilePositions(this.playerAirAttackTilePositions);
		trackerInfo.setPlayerGroundAttackTilePositions(this.playerGroundAttackTilePositions);
		trackerInfo.setEnemyAirAttackTilePositions(this.enemyAirAttackTilePositions);
		trackerInfo.setEnemyGroundAttackTilePositions(this.enemyGroundAttackTilePositions);
		trackerInfo.setEnemyBuildings(this.enemyBuildings);
		trackerInfo.setEnemyUnits(this.enemyUnits);
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
			if (unitList.get(i).getTimestampLastSeen() + MAX_TIME_UNTIL_OUTDATED <= game.elapsedTime()) {
				unitList.remove(i);
			}
		}
	}

	// TODO: Possible Change: Add following functions together
	/**
	 * Function used to generate the table of value tiles showing the air forces
	 * strength of the player units.
	 *
	 * @return a HashMap containing ValueTilePositions that represent the
	 *         players air strength.
	 */
	private HashMap<TilePosition, Integer> generatePlayerAirAttackTilePositions() {
		HashMap<TilePosition, Integer> valueTiles = new HashMap<>();

		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (unit.isCompleted() && unit.getType().airWeapon() != null
					&& unit.getType().airWeapon().damageAmount() > 0) {
				this.addValueInAreaToTilePositionValue(unit.getTilePosition(), valueTiles,
						this.generateUnitMultiplier(unit), unit.getType().airWeapon());
			}
		}
		return valueTiles;
	}

	/**
	 * Function used to generate the table of value tiles showing the ground
	 * forces strength of the player units.
	 * 
	 * @return a HashMap containing ValueTilePositions that represent the
	 *         players air strength.
	 */
	private HashMap<TilePosition, Integer> generatePlayerGroundAttackTilePositions() {
		HashMap<TilePosition, Integer> valueTiles = new HashMap<>();

		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (unit.isCompleted() && unit.getType().groundWeapon() != null
					&& unit.getType().groundWeapon().damageAmount() > 0) {
				this.addValueInAreaToTilePositionValue(unit.getTilePosition(), valueTiles,
						this.generateUnitMultiplier(unit), unit.getType().groundWeapon());
			}
		}
		return valueTiles;
	}

	/**
	 * Function used to generate the table of value tiles showing the air forces
	 * strength of the enemy units and buildings.
	 *
	 * @return a HashMap containing ValueTilePositions that represent the
	 *         enemies air strength.
	 */
	private HashMap<TilePosition, Integer> generateEnemyAirAttackTilePositions() {
		HashMap<TilePosition, Integer> valueTiles = new HashMap<>();

		// Units
		for (EnemyUnit enemyUnit : this.enemyUnits) {
			if (enemyUnit.getUnitType().airWeapon() != null && enemyUnit.getUnitType().airWeapon().damageAmount() > 0
					&& enemyUnit.getUnit().isCompleted()) {
				if (enemyUnit.getUnit() != null) {
					this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), valueTiles,
							this.generateUnitMultiplier(enemyUnit.getUnit()), enemyUnit.getUnitType().airWeapon());
				} else {
					this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), valueTiles,
							this.generateEnemyUnitMultiplier(enemyUnit), enemyUnit.getUnitType().airWeapon());
				}
			}
		}

		// Buildings
		for (EnemyUnit enemyBuilding : this.enemyBuildings) {
			if (enemyBuilding.getUnitType().airWeapon() != null
					&& enemyBuilding.getUnitType().airWeapon().damageAmount() > 0
					&& enemyBuilding.getUnit().isCompleted()) {
				if (enemyBuilding.getUnit() != null) {
					this.addValueInAreaToTilePositionValue(enemyBuilding.getLastSeenTilePosition(), valueTiles,
							this.generateUnitMultiplier(enemyBuilding.getUnit()),
							enemyBuilding.getUnitType().airWeapon());
				} else {
					this.addValueInAreaToTilePositionValue(enemyBuilding.getLastSeenTilePosition(), valueTiles,
							this.generateEnemyUnitMultiplier(enemyBuilding), enemyBuilding.getUnitType().airWeapon());
				}
			}
		}
		return valueTiles;
	}

	/**
	 * Function used to generate the table of value tiles showing the ground
	 * forces strength of the enemy units and buildings.
	 *
	 * @return a HashMap containing ValueTilePositions that represent the
	 *         enemies ground strength.
	 */
	private HashMap<TilePosition, Integer> generateEnemyGroundAttackTilePositions() {
		HashMap<TilePosition, Integer> valueTiles = new HashMap<>();

		// Units
		for (EnemyUnit enemyUnit : this.enemyUnits) {
			if (enemyUnit.getUnitType().groundWeapon() != null
					&& enemyUnit.getUnitType().groundWeapon().damageAmount() > 0 && enemyUnit.getUnit().isCompleted()) {
				if (enemyUnit.getUnit() != null) {
					this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), valueTiles,
							this.generateUnitMultiplier(enemyUnit.getUnit()), enemyUnit.getUnitType().groundWeapon());
				} else {
					this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), valueTiles,
							this.generateEnemyUnitMultiplier(enemyUnit), enemyUnit.getUnitType().groundWeapon());
				}
			}
		}

		// Buildings
		for (EnemyUnit enemyBuilding : this.enemyBuildings) {
			if (enemyBuilding.getUnitType().groundWeapon() != null
					&& enemyBuilding.getUnitType().groundWeapon().damageAmount() > 0
					&& enemyBuilding.getUnit().isCompleted()) {
				if (enemyBuilding.getUnit() != null) {
					this.addValueInAreaToTilePositionValue(enemyBuilding.getLastSeenTilePosition(), valueTiles,
							this.generateUnitMultiplier(enemyBuilding.getUnit()),
							enemyBuilding.getUnitType().groundWeapon());
				} else {
					this.addValueInAreaToTilePositionValue(enemyBuilding.getLastSeenTilePosition(), valueTiles,
							this.generateEnemyUnitMultiplier(enemyBuilding),
							enemyBuilding.getUnitType().groundWeapon());
				}
			}
		}
		return valueTiles;
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
		double multiplier = Double.valueOf(enemyUnit.getUnitType().maxHitPoints()) / 2.;

		if (enemyUnit.getUnitType().maxShields() > 0) {
			multiplier += Double.valueOf(enemyUnit.getUnitType().maxShields()) / 2.;
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
		double multiplier = HEALTH_MULTIPLIER * Double.valueOf(unit.getHitPoints())
				/ Double.valueOf(unit.getType().maxHitPoints());

		if (unit.getType().maxShields() > 0) {
			multiplier += SHIELD_MULTIPLIER
					* (Double.valueOf(unit.getShields()) / Double.valueOf(unit.getType().maxShields()));
		}
		return multiplier;
	}

	// TODO: Possible Change: Simplify function call
	/**
	 * Function for adding a units strength to the corresponding
	 * ValueTilePosition table. The added strength and the range at which these
	 * are being added are determined by different factors.
	 *
	 * @param tilePosition
	 *            the TilePosition the calculations are being done around.
	 * @param valueTiles
	 *            the table of all ValueTiles the function can work with.
	 * @param unitSpecificMultiplier
	 *            the multiplier which will be used to calculate another
	 *            multiplier used by the strength calculation of the Unit's 3d
	 *            cone.
	 * @param weaponType
	 *            the WeaponType of the Unit.
	 */
	private void addValueInAreaToTilePositionValue(TilePosition tilePosition, HashMap<TilePosition, Integer> valueTiles,
			double unitSpecificMultiplier, WeaponType weaponType) {
		int maxAttackTileRange = (int) (Double.valueOf(weaponType.maxRange())
				/ Double.valueOf(Core.getInstance().getTileSize()));

		// If the unit is a meele unit, the attack range is 0 and there will be
		// no calculations regarding the ValueTile lists. So the range has to be
		// set to 1.
		if (maxAttackTileRange == 0) {
			maxAttackTileRange = 1;
		}

		int generalMultiplier = 41; // Needed for increasing the multiplier in
									// the following calculations. Any positive
									// number can be inserted here. The bigger
									// the number, the stronger the effect of a
									// shorter range is.

		double multiplier = new Double(
				generalMultiplier * weaponType.damageAmount() * weaponType.damageFactor() * unitSpecificMultiplier)
				/ new Double(8 * Math.pow(maxAttackTileRange, 2) * Math.sin(1.));

		for (int i = -maxAttackTileRange; i <= maxAttackTileRange; i++) {
			for (int j = -maxAttackTileRange; j <= maxAttackTileRange; j++) {
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
					// the strength of the unit and the range of its attack.
					// Using this method ranged units do not get inaccurate
					// strength values regarding their superior range as their
					// strength is proportionally mapped to it using a general
					// multiplier.
					Integer sum = foundIntegerValue
							+ (int) ((Math.cos(i / maxAttackTileRange) + Math.cos(j / maxAttackTileRange))
									* multiplier);
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

	public HashMap<TilePosition, Integer> getEnemyAirAttackTilePositions() {
		return this.enemyAirAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemyGroundAttackTilePositions() {
		return this.enemyGroundAttackTilePositions;
	}
}
