package informationStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bwapi.TilePosition;
import unitTrackerModule.EnemyUnit;

/**
 * UnitTrackerInformation.java --- Class for storing UnitTrackerModule
 * information.
 * 
 * @author P H - 28.04.2017
 *
 */
public class UnitTrackerInformation {

	private HashMap<TilePosition, Integer> playerAirAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> playerGroundAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyAirAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyGroundAttackTilePositions = new HashMap<>();
	private List<EnemyUnit> enemyBuildings = new ArrayList<EnemyUnit>();
	private List<EnemyUnit> enemyUnits = new ArrayList<EnemyUnit>();

	public UnitTrackerInformation() {

	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

	public HashMap<TilePosition, Integer> getPlayerAirAttackTilePositions() {
		return playerAirAttackTilePositions;
	}

	public void setPlayerAirAttackTilePositions(HashMap<TilePosition, Integer> playerAirAttackTilePositions) {
		this.playerAirAttackTilePositions = playerAirAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getPlayerGroundAttackTilePositions() {
		return playerGroundAttackTilePositions;
	}

	public void setPlayerGroundAttackTilePositions(HashMap<TilePosition, Integer> playerGroundAttackTilePositions) {
		this.playerGroundAttackTilePositions = playerGroundAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemyAirAttackTilePositions() {
		return enemyAirAttackTilePositions;
	}

	public void setEnemyAirAttackTilePositions(HashMap<TilePosition, Integer> enemyAirAttackTilePositions) {
		this.enemyAirAttackTilePositions = enemyAirAttackTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemyGroundAttackTilePositions() {
		return enemyGroundAttackTilePositions;
	}

	public void setEnemyGroundAttackTilePositions(HashMap<TilePosition, Integer> enemyGroundAttackTilePositions) {
		this.enemyGroundAttackTilePositions = enemyGroundAttackTilePositions;
	}

	public List<EnemyUnit> getEnemyBuildings() {
		return enemyBuildings;
	}

	public void setEnemyBuildings(List<EnemyUnit> enemyBuildings) {
		this.enemyBuildings = enemyBuildings;
	}

	public List<EnemyUnit> getEnemyUnits() {
		return enemyUnits;
	}

	public void setEnemyUnits(List<EnemyUnit> enemyUnits) {
		this.enemyUnits = enemyUnits;
	}
}
