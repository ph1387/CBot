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
	private HashMap<TilePosition, Integer> playerHealthTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> playerSupportTilePositions = new HashMap<>();

	private HashMap<TilePosition, Integer> enemyAirAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyGroundAttackTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemyHealthTilePositions = new HashMap<>();
	private HashMap<TilePosition, Integer> enemySupportTilePositions = new HashMap<>();
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

	public HashMap<TilePosition, Integer> getPlayerHealthTilePositions() {
		return playerHealthTilePositions;
	}

	public void setPlayerHealthTilePositions(HashMap<TilePosition, Integer> playerHealthTilePositions) {
		this.playerHealthTilePositions = playerHealthTilePositions;
	}

	public HashMap<TilePosition, Integer> getPlayerSupportTilePositions() {
		return playerSupportTilePositions;
	}

	public void setPlayerSupportTilePositions(HashMap<TilePosition, Integer> playerSupportTilePositions) {
		this.playerSupportTilePositions = playerSupportTilePositions;
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

	public HashMap<TilePosition, Integer> getEnemyHealthTilePositions() {
		return enemyHealthTilePositions;
	}

	public void setEnemyHealthTilePositions(HashMap<TilePosition, Integer> enemyHealthTilePositions) {
		this.enemyHealthTilePositions = enemyHealthTilePositions;
	}

	public HashMap<TilePosition, Integer> getEnemySupportTilePositions() {
		return enemySupportTilePositions;
	}

	public void setEnemySupportTilePositions(HashMap<TilePosition, Integer> enemySupportTilePositions) {
		this.enemySupportTilePositions = enemySupportTilePositions;
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
