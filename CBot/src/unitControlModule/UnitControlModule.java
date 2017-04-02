package unitControlModule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;

import bwapi.*;
import core.Core;
import core.Display;
import javaGOAP.GoapAgent;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
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
	private Queue<UnitType> buildingQueue = new LinkedList<UnitType>();

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
		while (!this.unitsToAdd.isEmpty()) {
			Unit unit = this.unitsToAdd.poll();

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
		while (!this.unitsToRemove.isEmpty()) {
			Unit unit = this.unitsToRemove.poll();
			GoapAgent matchingAgent = null;

			for (GoapAgent agent : this.agents) {
				if (((PlayerUnit) agent.getAssignedGoapUnit()).getUnit() == unit) {
					matchingAgent = agent;

					break;
				}
			}

			if (matchingAgent != null) {
				this.agents.remove(matchingAgent);

				if (unit.getType().isWorker()) {
					this.removeAssignedWorkerEntries(unit);
				}
			}
		}
	}

	/**
	 * Function for removing a assigned worker Unit from the mapped gathering
	 * sources HashMap. This is only necessary if the Unit was destroyed before
	 * its Confidence got updated, since it then would have remove itself.
	 * 
	 * @param unit
	 *            the Unit (worker) that is going to be removed from the
	 *            assigned gathering sources HashMap.
	 */
	private void removeAssignedWorkerEntries(Unit unit) {
		final List<Unit> mappedSources = new ArrayList<Unit>();

		// Find the assigned sources of the Unit.
		PlayerUnitWorker.mappedAccessibleGatheringSources.forEach(new BiConsumer<Unit, ArrayList<Unit>>() {
			public void accept(Unit source, ArrayList<Unit> units) {
				for (Unit mappedUnit : units) {
					if (mappedUnit.equals(mappedUnit)) {
						mappedSources.add(source);
					}
				}
			}
		});

		// Remove the Unit from the found sources.
		for (Unit source : mappedSources) {
			PlayerUnitWorker.mappedAccessibleGatheringSources.get(source).remove(unit);
		}
	}

	/**
	 * Get all necessary information from a UnitTracker and transfer them into
	 * the PlayerUnit class. This removes the dependency of the Actions and
	 * updaters from this class as well as the UnitTrackerModule.
	 */
	private void updateInformation() {
		UnitTrackerModule utm = UnitTrackerModule.getInstance();

		PlayerUnit.setPlayerAirAttackTilePositions(utm.getPlayerAirAttackTilePositions());
		PlayerUnit.setPlayerGroundAttackTilePositions(utm.getPlayerGroundAttackTilePositions());
		PlayerUnit.setEnemyAirAttackTilePositions(utm.getEnemyAirAttackTilePositions());
		PlayerUnit.setEnemyGroundAttackTilePositions(utm.getEnemyGroundAttackTilePositions());
		PlayerUnit.setEnemyBuildings(utm.getEnemyBuildings());
		PlayerUnit.setEnemyUnits(utm.getEnemyUnits());
		PlayerUnit.setBuildingQueue(this.buildingQueue);
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
	public void addToBuildingQueue(UnitType unit) {
		if (unit.isBuilding()) {
			this.buildingQueue.add(unit);
		}
	}
}
