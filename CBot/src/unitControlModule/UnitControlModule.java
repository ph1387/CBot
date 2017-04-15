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
import unitControlModule.unitWrappers.PlayerBuilding;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;
import unitTrackerModule.UnitTrackerModule;

/**
 * UnitControlModule.java --- Module for controlling the Player's units.
 * 
 * @author P H - 29.01.2017
 *
 */
public class UnitControlModule {

	private static UnitControlModule instance;
	private static int WORKER_SCOUTING_TRIGGER = 9; // TODO: Implement

	private boolean workerOnceAssigned = false; // TODO: Implement

	private HashSet<GoapAgent> agents = new HashSet<GoapAgent>();
	private HashSet<PlayerBuilding> buildings = new HashSet<PlayerBuilding>();
	private Queue<Unit> unitsToAdd = new LinkedList<Unit>();
	private Queue<Unit> unitsToRemove = new LinkedList<Unit>();

	// Construction related collections
	private Queue<UnitType> buildingQueue = new LinkedList<UnitType>();
	private HashSet<Unit> buildingsBeingCreated = new HashSet<Unit>();

	// Training / Building related collections
	private Queue<UnitType> trainingQueue = new LinkedList<UnitType>();
	private Queue<UnitType> addonQueue = new LinkedList<UnitType>();
	private Queue<UpgradeType> upgradeQueue = new LinkedList<UpgradeType>();
	private Queue<TechType> researchQueue = new LinkedList<TechType>();

	private UnitControlModule() {

		// TODO: REMOVE
		// Buildings
		for (int i = 0; i < 5; i++) {
			this.buildingQueue.add(UnitType.Terran_Supply_Depot);
		}
		// for (int i = 0; i < 2; i++) {
		// this.buildingQueue.add(UnitType.Terran_Factory);
		// }
		for (int i = 0; i < 3; i++) {
			this.buildingQueue.add(UnitType.Terran_Barracks);
		}
		// Units
		for (int i = 0; i < 5; i++) {
			this.trainingQueue.add(UnitType.Terran_SCV);
		}

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
	 * Used for updating all Player Units and buildings (and their actions) in
	 * the game.
	 */
	public void update() {
		this.addTrackedUnits();
		this.removeTrackedUnits();
		this.updateInformation();

		// Update Units
		for (GoapAgent goapAgent : this.agents) {

			// TODO: DEBUG INFO
			Display.showUnitTarget(Core.getInstance().getGame(),
					((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit(), new Color(0, 0, 255));

			goapAgent.update();
		}

		// Update buildings
		for (PlayerBuilding building : this.buildings) {
			building.update();
		}
		
		// Display all important information on the screen
		UnitControlDisplay.showImportantInformation(this.agents, this.buildings);
	}

	/**
	 * Function for adding new Units to the corresponding collections.
	 */
	private void addTrackedUnits() {
		while (!this.unitsToAdd.isEmpty()) {
			Unit unit = this.unitsToAdd.poll();

			try {
				// Differentiate between buildings and normal Units
				if (unit.getType().isBuilding()) {
					// TODO: Possible Change: Move to factory
					this.buildings.add(new PlayerBuilding(unit));
				} else {
					this.agents.add(GoapAgentFactory.createAgent(unit));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Function used for removing all queued Units from the corresponding
	 * collections.
	 */
	private void removeTrackedUnits() {
		while (!this.unitsToRemove.isEmpty()) {
			Unit unit = this.unitsToRemove.poll();

			// Differentiate between buildings and normal Units
			if (unit.getType().isBuilding()) {
				this.removeBuilding(unit);
			} else {
				this.removeUnit(unit);
			}
		}
	}

	/**
	 * Function for removing a building from the collection of tracked Units.
	 * 
	 * @param unit
	 *            the building (Unit) that is going to be removed.
	 */
	private void removeBuilding(Unit unit) {
		PlayerBuilding matchingObject = null;

		for (PlayerBuilding building : this.buildings) {
			if (building.getUnit() == unit) {
				matchingObject = building;

				break;
			}
		}

		if (matchingObject != null) {
			this.buildings.remove(matchingObject);
		}
	}

	/**
	 * Function for removing a Unit from the collection of tracked Units.
	 * 
	 * @param unit
	 *            the Unit that is going to be removed.
	 */
	private void removeUnit(Unit unit) {
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

		// Forward the UnitTrackerModule information
		PlayerUnit.setPlayerAirAttackTilePositions(utm.getPlayerAirAttackTilePositions());
		PlayerUnit.setPlayerGroundAttackTilePositions(utm.getPlayerGroundAttackTilePositions());
		PlayerUnit.setEnemyAirAttackTilePositions(utm.getEnemyAirAttackTilePositions());
		PlayerUnit.setEnemyGroundAttackTilePositions(utm.getEnemyGroundAttackTilePositions());
		PlayerUnit.setEnemyBuildings(utm.getEnemyBuildings());
		PlayerUnit.setEnemyUnits(utm.getEnemyUnits());

		// Forward the construction Queue
		while (!this.buildingQueue.isEmpty()) {
			PlayerUnitWorker.buildingQueue.add(this.buildingQueue.poll());
		}

		// Forward the currently constructed buildings
		for (Unit unit : this.buildingsBeingCreated) {
			PlayerUnitWorker.buildingsBeingCreated.add(unit);
		}
		this.buildingsBeingCreated.clear();

		// Forward all relevant building information
		while (!this.trainingQueue.isEmpty()) {
			PlayerBuilding.trainingQueue.add(this.trainingQueue.poll());
		}
		while (!this.addonQueue.isEmpty()) {
			PlayerBuilding.addonQueue.add(this.addonQueue.poll());
		}
		while (!this.upgradeQueue.isEmpty()) {
			PlayerBuilding.upgradeQueue.add(this.upgradeQueue.poll());
		}
		while (!this.researchQueue.isEmpty()) {
			PlayerBuilding.researchQueue.add(this.researchQueue.poll());
		}
	}

	/**
	 * Function for adding a Unit to the List of controllable Units.
	 * 
	 * @param unit
	 *            the Unit that is going to be controlled.
	 */
	public void addToUnitControl(Unit unit) {
		if (unit.getPlayer() == Core.getInstance().getPlayer()) {
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
	public void addToBuildingQueue(UnitType unitType) {
		if (unitType.isBuilding()) {
			this.buildingQueue.add(unitType);
		}
	}

	/**
	 * Adds a Unit to the HashSet of Units being built.
	 * 
	 * @param unit
	 *            the building that is being built.
	 */
	public void addToBuildingsBeingCreated(Unit unit) {
		if(unit.getType().isBuilding()) {
			this.buildingsBeingCreated.add(unit);
		}
	}

	/**
	 * Function for adding a UniType to the training Queue so that a Unit of
	 * that specific type will be trained at a building.
	 * 
	 * @param unitType
	 *            the type of Unit that is going to be trained.
	 */
	public void addToTrainingQueue(UnitType unitType) {
		if(!unitType.isBuilding() && !unitType.isAddon()) {
			this.trainingQueue.add(unitType);
		}
	}

	/**
	 * Function for adding a addon to the collection of addons being added to
	 * certain types of buildings.
	 * 
	 * @param unitType
	 *            the type of addon that is going to be created at one of the
	 *            buildings.
	 */
	public void addToAddonQueue(UnitType unitType) {
		if(unitType.isAddon()) {
			this.addonQueue.add(unitType);
		}
	}

	/**
	 * Function for adding a upgrade to the collection of upgrades being built
	 * at certain buildings.
	 * 
	 * @param upgrade
	 *            the upgrade that is going to be built.
	 */
	public void addToUpgradeQueue(UpgradeType upgrade) {
		this.upgradeQueue.add(upgrade);
	}

	/**
	 * Function for adding a technology to the collection of technology being
	 * researched at certain buildings.
	 * 
	 * @param tech
	 *            the technology that is going to be researched.
	 */
	public void addToResearchQueue(TechType tech) {
		this.researchQueue.add(tech);
	}
}
