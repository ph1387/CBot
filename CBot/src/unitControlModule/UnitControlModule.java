package unitControlModule;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bwapi.*;
import core.Core;
import core.Display;
import javaGOAP.GoapAgent;
import unitControlModule.unitWrappers.PlayerUnit;
import unitTrackerModule.EnemyUnit;
import unitTrackerModule.UnitTrackerModule;

/**
 * UnitControlModule.java --- Module for controlling the player units
 * 
 * @author P H - 29.01.2017
 *
 */
public class UnitControlModule {

	private static UnitControlModule instance;
	private static int WORKER_SCOUTING_TRIGGER = 9;

	private boolean workerOnceAssigned = false;

	private HashSet<GoapAgent> agents = new HashSet<GoapAgent>();
	private Queue<Unit> unitsToAdd = new LinkedList<Unit>();
	private Queue<Unit> unitsToRemove = new LinkedList<Unit>();
	private Queue<Unit> buildingsToBuild = new LinkedList<Unit>();

	private Hashtable<TilePosition, Integer> playerAirAttackTilePositions;
	private Hashtable<TilePosition, Integer> playerGroundAttackTilePositions;
	private Hashtable<TilePosition, Integer> enemyAirAttackTilePositions;
	private Hashtable<TilePosition, Integer> enemyGroundAttackTilePositions;
	private List<EnemyUnit> enemyBuildings;
	private List<EnemyUnit> enemyUnits;

	private UnitControlModule() {

	}

	// -------------------- Functions

	/**
	 * Singleton function.
	 * 
	 * @return instance of the class.
	 */
	public static UnitControlModule getInstance() {
		if (instance == null) {
			instance = new UnitControlModule();
		}
		return instance;
	}

	/**
	 * Used for updating all Units and their actions in game.
	 */
	public void update() {
		this.addNewAgents();
		this.removeAgents();
		this.updateInformation();

		// Update all agents
		for (GoapAgent goapAgent : this.agents) {

			// TODO: DEBUG INFO
			Display.showUnitTarget(Core.getInstance().getGame(),
					((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit(), new Color(0, 0, 255));

			goapAgent.update();
		}
	}

	/**
	 * Function for adding new agents to the HashSet.
	 */
	private void addNewAgents() {
		for (Unit unit : this.unitsToAdd) {
			try {
				this.agents.add(GoapAgentFactory.createAgent(unit));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Function used for removing all agents whose Units got queued from the
	 * HashSet.
	 */
	private void removeAgents() {
		for (Unit unit : this.unitsToRemove) {
			GoapAgent matchingAgent = null;

			for (GoapAgent agent : this.agents) {
				if (((PlayerUnit) agent.getAssignedGoapUnit()).getUnit() == unit) {
					matchingAgent = agent;

					break;
				}
			}

			if (matchingAgent != null) {
				this.agents.remove(matchingAgent);
			}
		}
	}

	/**
	 * Get all necessary information from a UnitTracker. Providing one centered
	 * pool for information is better than trying to gather information from all
	 * over the place.
	 */
	private void updateInformation() {
		UnitTrackerModule utm = UnitTrackerModule.getInstance();

		this.playerAirAttackTilePositions = utm.getPlayerAirAttackTilePositions();
		this.playerGroundAttackTilePositions = utm.getPlayerGroundAttackTilePositions();
		this.enemyAirAttackTilePositions = utm.getEnemyAirAttackTilePositions();
		this.enemyGroundAttackTilePositions = utm.getEnemyGroundAttackTilePositions();
		this.enemyBuildings = utm.getEnemyBuildings();
		this.enemyUnits = utm.getEnemyUnits();
	}

	/**
	 * Function for adding a Unit to the List of controllable Units.
	 * 
	 * @param unit
	 *            the Unit that is going to be controlled.
	 */
	public void addToUnitControl(Unit unit) {
		if (!unit.getType().isBuilding() && unit.getPlayer() == Core.getInstance().getPlayer()) {
			this.unitsToAdd.add(unit);
		}
	}

	/**
	 * Function for removing a Unit from the List of controllable Units.
	 * 
	 * @param unit
	 *            the Unit that is going to be removed.
	 */
	public void removeUnitFromUnitControl(Unit unit) {
		this.unitsToRemove.add(unit);
	}

	/**
	 * Function for adding a Building to the building Queue.
	 * 
	 * @param unit
	 *            the building that is going to be build.
	 */
	public void addToBuildingQueue(Unit unit) {
		if (unit.getType().isBuilding()) {
			this.buildingsToBuild.add(unit);
		}
	}

	// TODO: Implementation: tryTransferringWorkerToCombatUnits
	// /**
	// * Function for transferring a single worker to the combat units, so that
	// it
	// * is used to scout the enemy base at a certain worker amount.
	// */
	// private void tryTransferringWorkerToCombatUnits() {
	// int workerCount = 0;
	//
	// for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
	// if (unit.getType().isWorker()) {
	// workerCount++;
	// }
	// }
	//
	// if (workerCount >= WORKER_SCOUTING_TRIGGER) {
	// for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
	//
	// // TODO: REMOVE
	// System.out.println("UNIT: " + unit + " " + unit.getType() + " " +
	// unit.getType().isWorker() + " "
	// + unit.isGatheringMinerals() + " " + unit.isCompleted() + " " +
	// this.workerOnceAssigned);
	// // Unit references are still not the same
	//
	// if (unit.getType().isWorker() && unit.isGatheringMinerals() &&
	// unit.isCompleted()
	// && !this.workerOnceAssigned) {
	//
	// // TODO: REMOVE
	// System.out.println("CHOSEN: " + unit);
	//
	// this.dispatchNewSperateUnitEvent(unit);
	// // TODO: Problem: References are not equal
	// // this.newCombatUnits.add(unit);
	// this.workerOnceAssigned = true;
	// }
	// }
	// }
	// }

	// ------------------------------ Getter / Setter

	public Hashtable<TilePosition, Integer> getPlayerAirAttackTilePositions() {
		return playerAirAttackTilePositions;
	}

	public Hashtable<TilePosition, Integer> getPlayerGroundAttackTilePositions() {
		return playerGroundAttackTilePositions;
	}

	public Hashtable<TilePosition, Integer> getEnemyAirAttackTilePositions() {
		return enemyAirAttackTilePositions;
	}

	public Hashtable<TilePosition, Integer> getEnemyGroundAttackTilePositions() {
		return enemyGroundAttackTilePositions;
	}

	public List<EnemyUnit> getEnemyBuildings() {
		return enemyBuildings;
	}

	public List<EnemyUnit> getEnemyUnits() {
		return enemyUnits;
	}
}
