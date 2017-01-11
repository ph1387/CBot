package enemyTrackerModule;

import java.util.ArrayList;
import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.TilePosition;
import bwapi.Unit;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;
import unitControlModule.UnitControlModule;

public class EnemyTrackerModule implements CBotBWEventListener {

	private static EnemyTrackerModule instance;
	private static final int MAX_TIME_UPDATE_WAIT = 3;
	private static final int MAX_TIME_UNTIL_OUTDATED = 20;

	private Integer lastUpdateTimestamp = null;

	private List<EnemyUnit> enemyBuildings = new ArrayList<EnemyUnit>();
	private List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>();

	private EnemyTrackerModule() {
		CBotBWEventDistributor.getInstance().addListener(this);
	}

	// -------------------- Functions

	// Singleton function
	public static EnemyTrackerModule getInstance() {
		if (instance == null) {
			instance = new EnemyTrackerModule();
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
		EnemyTrackerDisplay.showBuildingsLastPosition(this.enemyBuildings);
		EnemyTrackerDisplay.showUnitsLastPosition(this.enemyUnits);
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
