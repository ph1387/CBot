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
import informationStorage.InformationStorage;
import javaGOAP.GoapAgent;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.unitWrappers.PlayerBuilding;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.RemoveAgentEvent;

/**
 * UnitControlModule.java --- Module for controlling the Player's units.
 * 
 * @author P H - 29.01.2017
 *
 */
public class UnitControlModule implements RemoveAgentEvent {

	// The HashSet(s) is / are used for displaying the content whereas the
	// Queue(s) is / are used for updating. Not a perfect solution due to adding
	// and removing elements from multiple Collections but functional.
	private HashSet<GoapAgent> agents = new HashSet<GoapAgent>();
	private HashSet<PlayerBuilding> buildings = new HashSet<PlayerBuilding>();
	private Queue<GoapAgent> agentUpdateQueueCombatUnits = new LinkedList<GoapAgent>();
	private Queue<GoapAgent> agentUpdateQueueWorkers = new LinkedList<GoapAgent>();
	private Queue<PlayerBuilding> agentUpdateQueueBuildings = new LinkedList<PlayerBuilding>();

	private Queue<Unit> unitsToAdd = new LinkedList<Unit>();
	private Queue<Unit> unitsToRemove = new LinkedList<Unit>();

	private InformationStorage informationStorage;

	// The percentage of combat Units that are updated in one single iteration.
	// Higher values equal a stronger impact on the CPU.
	private double combatUnitUpdatePercentage = 0.2;

	public UnitControlModule(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	/**
	 * Used for updating all Player Units and buildings (and their actions) in
	 * the game.
	 */
	public void update() {
		this.addNewTrackedUnits();
		this.removeTrackedUnits();

		// TODO: DEBUG INFO
		// Display the targets of all registered Units.
		for (GoapAgent goapAgent : this.agents) {
			Display.showUnitTarget(((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit(), new Color(0, 0, 255));
		}

		// Update the instances in the specified Queues.
		this.updateCombatUnitQueue();
		this.updateWorkerQueue();
		this.updateBuildingUnitQueue();

		// Display of various information on the screen and on the map.
		if (this.informationStorage.getiUnitControlModuleConfig().enableDisplayQueueInformation()) {
			UnitControlDisplay.showQueueInformation(this.agents, this.buildings, this.informationStorage);
		}
		if (this.informationStorage.getiUnitControlModuleConfig().enableDisplayUnitConfidence()) {
			UnitControlDisplay.showConfidence(this.agents);
		}
	}

	/**
	 * Function for adding new Units to the corresponding collections.
	 */
	private void addNewTrackedUnits() {
		while (!this.unitsToAdd.isEmpty()) {
			Unit unit = this.unitsToAdd.poll();

			try {
				// Differentiate between buildings and normal Units.
				if (unit.getType().isBuilding()) {
					// TODO: Possible Change: Move to factory
					// TODO: Possible Change: Add Listener like below!
					PlayerBuilding building = new PlayerBuilding(unit, this.informationStorage);
					this.agentUpdateQueueBuildings.add(building);
					this.buildings.add(building);
				} else {
					GoapAgent agent = GoapAgentFactory.createAgent(unit, this.informationStorage);

					((PlayerUnit) agent.getAssignedGoapUnit()).addAgentRemoveListener(this);
					this.agents.add(agent);

					// Add the agent towards the appropriate update Queue.
					if (unit.getType().isWorker()) {
						this.agentUpdateQueueWorkers.add(agent);
					} else {
						this.agentUpdateQueueCombatUnits.add(agent);
					}
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

			// Differentiate between buildings and normal Units.
			if (unit.getType().isBuilding()) {
				this.removeBuilding(unit);
			} else {
				this.removeUnit(unit);
			}
		}
	}

	/**
	 * Function for updating an amount of combat Units.
	 */
	private void updateCombatUnitQueue() {
		int maxIterations = (int) (Math
				.ceil((double) (this.agentUpdateQueueCombatUnits.size()) * this.combatUnitUpdatePercentage));

		// Update a fixed percentage of combat Units and place them at the end
		// of the Queue afterwards.
		for (int i = 0; i < maxIterations; i++) {
			GoapAgent currentAgent = this.agentUpdateQueueCombatUnits.poll();

			if (currentAgent != null) {
				this.updateGoapAgentProcedure(currentAgent);
				this.agentUpdateQueueCombatUnits.add(currentAgent);
			}
		}
	}

	/**
	 * Function for updating an amount of worker Units.
	 */
	private void updateWorkerQueue() {
		// Update a single GoapAgent from the currently stored ones and place
		// him at the end of the Queue.
		GoapAgent currentAgent = this.agentUpdateQueueWorkers.poll();

		if (currentAgent != null) {
			this.updateGoapAgentProcedure(currentAgent);
			this.agentUpdateQueueWorkers.add(currentAgent);
		}
	}

	/**
	 * Function for performing a standard update action for a GoapAgent
	 * (Multiple steps => own function).
	 * 
	 * @param agent
	 *            the GoapAgent that is going to be updated.
	 */
	private void updateGoapAgentProcedure(GoapAgent agent) {
		agent.update();

		// Do a hollow update for the underlying FSM (Might be having a
		// Idle-State on top).
		((PlayerUnit) agent.getAssignedGoapUnit()).setHollowUpdatesEnabled(true);
		agent.update();
		((PlayerUnit) agent.getAssignedGoapUnit()).setHollowUpdatesEnabled(false);
	}

	/**
	 * Function for updating an amount of buildings.
	 */
	private void updateBuildingUnitQueue() {
		// Update a single PlayerBuilding from the currently stored ones and
		// place it at the end of the Queue.
		PlayerBuilding currentBuilding = this.agentUpdateQueueBuildings.poll();
		if (currentBuilding != null) {
			currentBuilding.update();
			this.agentUpdateQueueBuildings.add(currentBuilding);
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

		for (PlayerBuilding building : this.agentUpdateQueueBuildings) {
			// Reference of the Unit changes!
			// -> Unit reference here sometimes is not the saved reference.
			if (building.getUnit() == unit || building.getUnit().getPosition().equals(unit.getPosition())) {
				matchingObject = building;

				break;
			}
		}

		if (matchingObject != null) {
			this.agentUpdateQueueBuildings.remove(matchingObject);
			this.buildings.remove(matchingObject);
		}
		// TODO: REMOVE Safety feature since it is not clear if the Unit is
		// found.
		else {
			try {
				throw new Exception("No Matching Agent was found -> " + unit + " " + unit.getType());
			} catch (Exception e) {
				e.printStackTrace();
			}
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

		for (GoapAgent agent : agents) {
			Unit u = ((PlayerUnit) agent.getAssignedGoapUnit()).getUnit();

			// Reference of the Unit changes!
			// -> Unit reference here sometimes is not the saved reference.
			if (((PlayerUnit) agent.getAssignedGoapUnit()).getUnit() == unit
					|| u.getPosition().equals(unit.getPosition())) {
				matchingAgent = agent;

				break;
			}
		}

		// Remove any stored information regarding the Agent / Unit.
		if (matchingAgent != null) {
			this.agentUpdateQueueWorkers.remove(matchingAgent);
			this.agentUpdateQueueCombatUnits.remove(matchingAgent);
			this.agents.remove(matchingAgent);

			// Remove the Unit from any assigned groups.
			BaseAction.removeGroupAssociations(matchingAgent.getAssignedGoapUnit());

			// Remove any contended / assigned entries if the Unit is a worker.
			if (unit.getType().isWorker()) {
				this.removeAssignedWorkerEntries(unit);
				this.informationStorage.getWorkerConfig().decrementTotalWorkerCount();
			}
		}
		// TODO: REMOVE Safety feature since it is not clear if the Unit is
		// found.
		else {
			try {
				throw new Exception("No Matching Agent was found for -> " + unit + " " + unit.getType());
			} catch (Exception e) {
				e.printStackTrace();
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
		this.informationStorage.getWorkerConfig().getMappedAccessibleGatheringSources()
				.forEach(new BiConsumer<Unit, HashSet<Unit>>() {
					public void accept(Unit source, HashSet<Unit> units) {
						for (Unit mappedUnit : units) {
							if (mappedUnit.equals(mappedUnit)) {
								mappedSources.add(source);
							}
						}
					}
				});

		// Remove the Unit from the found sources.
		for (Unit source : mappedSources) {
			this.informationStorage.getWorkerConfig().getMappedAccessibleGatheringSources().get(source).remove(unit);
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
			this.informationStorage.getWorkerConfig().getBuildingQueue().add(unitType);
		}
	}

	/**
	 * Adds a Unit to the HashSet of Units being built.
	 * 
	 * @param unit
	 *            the building that is being built.
	 */
	public void addToBuildingsBeingCreated(Unit unit) {
		if (unit.getType().isBuilding()) {
			this.informationStorage.getWorkerConfig().getBuildingsBeingCreated().add(unit);
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
		if (!unitType.isBuilding() && !unitType.isAddon()) {
			this.informationStorage.getTrainingQueue().add(unitType);
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
		if (unitType.isAddon()) {
			this.informationStorage.getAddonQueue().add(unitType);
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
		this.informationStorage.getUpgradeQueue().add(upgrade);
	}

	/**
	 * Function for adding a technology to the collection of technology being
	 * researched at certain buildings.
	 * 
	 * @param tech
	 *            the technology that is going to be researched.
	 */
	public void addToResearchQueue(TechType tech) {
		this.informationStorage.getResearchQueue().add(tech);
	}

	// ------------------------------ Getter / Setter

	// ------------------------------ Eventlisteners

	@Override
	public void removeAgent(PlayerUnit sender) {
		for (GoapAgent goapAgent : this.agents) {
			if (((PlayerUnit) goapAgent.getAssignedGoapUnit()).equals(sender)) {
				this.removeUnitFromUnitControl(((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit());

				break;
			}
		}
	}
}
