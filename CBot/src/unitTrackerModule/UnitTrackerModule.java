package unitTrackerModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.ValidationEvent;

import bwapi.Color;
import bwapi.Game;
import bwapi.Player;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;
import unitControlModule.UnitControlModule;

public class UnitTrackerModule implements CBotBWEventListener {

	private static UnitTrackerModule instance;
	private static final int MAX_TIME_UPDATE_WAIT = 3;
	private static final int MAX_TIME_UNTIL_OUTDATED = 20;
	private static final int MAX_ATTACK_TILE_RANGE = 3;

	private Integer lastUpdateTimestamp = null;

	private List<ValueTilePosition> playerAirAttackTilePositions = new ArrayList<ValueTilePosition>();
	private List<ValueTilePosition> playerGroundAttackTilePositions = new ArrayList<ValueTilePosition>();
	private List<ValueTilePosition> enemyAirAttackTilePositions = new ArrayList<ValueTilePosition>();
	private List<ValueTilePosition> enemyGroundAttackTilePositions = new ArrayList<ValueTilePosition>();
	private List<EnemyUnit> enemyBuildings = new ArrayList<EnemyUnit>();
	private List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>();

	private UnitTrackerModule() {
		CBotBWEventDistributor.getInstance().addListener(this);
	}

	// -------------------- Functions

	// Singleton function
	public static UnitTrackerModule getInstance() {
		if (instance == null) {
			instance = new UnitTrackerModule();
		}
		return instance;
	}

	// Function for updating all lists regarding the enemies units and buildings
	private void updateEnemyUnitLists() {
		// Add new units to the corresponding lists
		this.addVisibleUnits(this.enemyUnits, this.enemyBuildings);

		// Update the known units based on the currently visible tiles (units
		// and buildings)
		this.verifyKnownTiles(this.enemyBuildings);
		this.verifyKnownTiles(this.enemyUnits);

		// Remove all outdated unitpositions
		this.removeOutdatedEntries(this.enemyUnits);
		
		// Generate the lists of tilepositions which contain the attack power of the enemies and the players units 
//		this.enemyAirAttackTilePositions = this.generateEnemyAirAttackTilePositions();
		this.enemyGroundAttackTilePositions = this.generateEnemyGroundAttackTilePositions();
//		this.playerAirAttackTilePositions = this.generatePlayerAirAttackTilePositions();
		this.playerGroundAttackTilePositions = this.generatePlayerGroundAttackTilePositions();
	}

	// Add visible units to the corresponding lists if they are not already in
	// them
	private void addVisibleUnits(List<EnemyUnit> unitList, List<EnemyUnit> buildingList) {
		Game game = Core.getInstance().getGame();

		for (Unit unit : game.enemy().getUnits()) {
			if (unit.isVisible()) {
				EnemyUnit newEnemyUnit = new EnemyUnit(unit.getTilePosition(), unit, game.elapsedTime());

				// Tileposition is not in list
				if (unit.getType().isBuilding() && !this.isInUnitList(this.enemyBuildings, unit)) {
					buildingList.add(newEnemyUnit);
				} else if (!this.isInUnitList(this.enemyUnits, unit)) {
					unitList.add(newEnemyUnit);
				}
			}
		}
	}

	// Test if a units tileposition is in a enemy unit list
	private boolean isInUnitList(List<EnemyUnit> unitList, Unit unit) {
		boolean isInList = false;

		for (int i = 0; i < unitList.size() && !isInList; i++) {
			if (unitList.get(i).getLastSeenTilePosition() == unit.getTilePosition()) {
				isInList = true;
			}
		}
		return isInList;
	}

	// Validate units in a unit list with the currently seen units on the map.
	// If the unit is not at the saved location, remove the entry from the list.
	private void verifyKnownTiles(List<EnemyUnit> unitList) {
		Game game = Core.getInstance().getGame();
		List<Unit> enemieUnits = game.enemy().getUnits();

		// Since units get removed, start at the end of the list (-> left shifting)
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

	// Function for removing all old, outdated entries in the given list, since
	// units are unknown as long as they are not seen even if they are saved
	// inside a variable.
	private void removeOutdatedEntries(List<EnemyUnit> unitList) {
		Game game = Core.getInstance().getGame();

		// Start from the end, since removed items lead to a left shift inside
		// the list
		for (int i = unitList.size() - 1; i >= 0; i--) {
			if (unitList.get(i).getTimestampLastSeen() + MAX_TIME_UNTIL_OUTDATED <= game.elapsedTime()) {
				unitList.remove(i);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	// Function used to generate the list of value tiles showing the air forces strength of the player units.
	private List<ValueTilePosition> generatePlayerAirAttackTilePositions() {
		return null;
	}
	
	// Function used to generate the list of value tiles showing the ground forces strength of the player units.
	private List<ValueTilePosition> generatePlayerGroundAttackTilePositions() {
		List<ValueTilePosition> valueTiles = new ArrayList<ValueTilePosition>();
		Player player = Core.getInstance().getPlayer();
		
		for (Unit unit : player.getUnits()) {
			if(unit.getType().groundWeapon() != null && unit.getType().groundWeapon().damageAmount() > 0) {
				this.addValueInAreaToTilePositionValue(unit.getTilePosition(), valueTiles, unit.getType());
			}
		}
		return valueTiles;
	}
	
	// Function used to generate the list of value tiles showing the air forces strength of the enemy units and buildings.
	private List<ValueTilePosition> generateEnemyAirAttackTilePositions() {
		return null;
	}
	
	// Function used to generate the list of value tiles showing the ground forces strength of the enemy units and buildings.
	private List<ValueTilePosition> generateEnemyGroundAttackTilePositions() {
		List<ValueTilePosition> valueTiles = new ArrayList<ValueTilePosition>();
		
		// Units
		for (EnemyUnit enemyUnit : this.enemyUnits) {
			if(enemyUnit.getUnitType().groundWeapon() != null && enemyUnit.getUnitType().groundWeapon().damageAmount() > 0) {
				this.addValueInAreaToTilePositionValue(enemyUnit.getLastSeenTilePosition(), valueTiles, enemyUnit.getUnitType());
			}
		}
		
		// Buildings
		for (EnemyUnit enemyBuilding : this.enemyBuildings) {
			if(enemyBuilding.getUnitType().groundWeapon() != null && enemyBuilding.getUnitType().groundWeapon().damageAmount() > 0) {
				this.addValueInAreaToTilePositionValue(enemyBuilding.getLastSeenTilePosition(), valueTiles, enemyBuilding.getUnitType());
			}
		}
		return valueTiles;
	}
	
	// Function for adding a units attack value to the corresponding valuetileposition list. The 
	private void addValueInAreaToTilePositionValue(TilePosition tilePosition, List<ValueTilePosition> valueTiles, UnitType unitType) {
		// In an area around the last seen tileposition add the attack value proportional to the distance to the tiles
		for(int i = - MAX_ATTACK_TILE_RANGE; i < MAX_ATTACK_TILE_RANGE; i++) {
			for(int j = - MAX_ATTACK_TILE_RANGE; j < MAX_ATTACK_TILE_RANGE; j++) {
				if(tilePosition.getX() + i > 0 && tilePosition.getY() + j > 0) {
					// Try to find the valuetileposition inside the list created before. If it is not found, create a new instance
					ValueTilePosition foundValueTilePosition = this.tryToFindTilePositionInValueList(valueTiles, tilePosition, i, j);
					
					// If no valuetileposition in the list is found, create a new one and add it to the list for further tests
					if(foundValueTilePosition == null) {
						foundValueTilePosition = new ValueTilePosition(new TilePosition(tilePosition.getX() + i, tilePosition.getY() + j));
						valueTiles.add(foundValueTilePosition);
					}
					
					// Add the strength of the unit to the tiles value proportional to the distance between the units tile and the current tile
					foundValueTilePosition.addToTileValue((int)(unitType.groundWeapon().damageAmount() / (Math.abs(Math.min(i, j)) + 1)));
				}
			}
		}
	}
	
	// Function for finding a specific tileposition in the valuetileposition list
	private ValueTilePosition tryToFindTilePositionInValueList(List<ValueTilePosition> valueTiles, TilePosition tilePosition, int i, int j) {
		ValueTilePosition foundValueTilePosition = null;
		
		for(int k = 0; k < valueTiles.size() && foundValueTilePosition == null; k++) {
			ValueTilePosition valueTilePosition = valueTiles.get(k);
			
			if(valueTilePosition.getTilePosition().getX() == tilePosition.getX() + i && valueTilePosition.getTilePosition().getY() == tilePosition.getY() + j) {
				foundValueTilePosition = valueTilePosition;
			}
		}
		return foundValueTilePosition;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// ------------------------------ Getter / Setter

	public List<EnemyUnit> getEnemyBuildings() {
		return enemyBuildings;
	}

	public List<EnemyUnit> getEnemyUnits() {
		return enemyUnits;
	}

	// -------------------- Eventlisteners

	// ------------------------------ Own CBotBWEventListener
	@Override
	public void onStart() {

	}

	@Override
	public void onFrame() {
		// Wait a certain amount before updating the lists to prevent cpu spikes
		if (this.lastUpdateTimestamp == null
				|| Core.getInstance().getGame().elapsedTime() - this.lastUpdateTimestamp >= MAX_TIME_UPDATE_WAIT) {
			this.lastUpdateTimestamp = Core.getInstance().getGame().elapsedTime();

			// Update the enemy units lists
			this.updateEnemyUnitLists();
		}

		// Update the display of those units
		UnitTrackerDisplay.showBuildingsLastPosition(this.enemyBuildings);
		UnitTrackerDisplay.showUnitsLastPosition(this.enemyUnits);
		
		// Update the display of the calculated combat values of the ground and air forces of the enemy and the player. Player has to the shown first, since the enemy list is empty.
		UnitTrackerDisplay.showPlayerUnitTileStrength(this.playerGroundAttackTilePositions);
		UnitTrackerDisplay.showEnemyUnitTileStrength(this.enemyGroundAttackTilePositions);
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	@Override
	public void onUnitComplete(Unit unit) {

	}

	@Override
	public void onUnitDestroy(Unit unit) {

	}
}
