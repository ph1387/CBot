package unitControlModule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bwapi.Color;
import bwapi.TechType;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;
import core.Display;
import core.TilePositionContenderFactory;
import informationStorage.InformationStorage;
import javaGOAP.GoapAgent;
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

	private GoapAgentFactory goapAgentFactory;

	// The UnitTypes that are not going to be added to the different update sets
	// and is therefore ignored. The GoapAgentFactory does not need to provide a
	// GoapAgent instance for Units of this UnitType. A UnitType should only be
	// added towards this List if it performs it's actions on it's own.
	private List<UnitType> ignoredUnitTypes = Arrays.asList(new UnitType[] { UnitType.Terran_Vulture_Spider_Mine });

	// Information regarding the updates of the different kinds of Units.
	// Workers and combat Units are updated in an alternating fashion.
	private enum UpdateCycle {
		WORKER, COMBAT_UNIT
	};

	private int consecutiveCombatUnitUpdates = 4;
	private int currentConsecutiveCombatUnitUpdates = 0;

	private UpdateCycle currentUpdateCycle = UpdateCycle.WORKER;

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

	public UnitControlModule(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
		this.goapAgentFactory = new GoapAgentFactory(informationStorage);
	}

	// -------------------- Functions

	/**
	 * Used for updating all Player Units and buildings (and their actions) in
	 * the game.
	 */
	public void update() {
		if (this.informationStorage.getiUnitControlModuleConfig().enableUnitControlModuleUpdates()) {
			this.addNewTrackedUnits();
			this.removeTrackedUnits();

			// Update the instances in the specified Queues.
			if (this.currentUpdateCycle == UpdateCycle.COMBAT_UNIT) {
				this.updateCombatUnitQueue();

				this.currentConsecutiveCombatUnitUpdates++;

				if (this.currentConsecutiveCombatUnitUpdates >= this.consecutiveCombatUnitUpdates) {
					this.currentUpdateCycle = UpdateCycle.WORKER;
					this.currentConsecutiveCombatUnitUpdates = 0;
				}
			} else {
				this.updateWorkerQueue();
				this.currentUpdateCycle = UpdateCycle.COMBAT_UNIT;
			}
			this.updateBuildingUnitQueue();

			// Display of various information on the screen and on the map.
			if (this.informationStorage.getiUnitControlModuleConfig().enableDisplayQueueInformation()) {
				UnitControlDisplay.showQueueInformation(this.agents, this.buildings, this.informationStorage);
			}
			if (this.informationStorage.getiUnitControlModuleConfig().enableDisplayUnitConfidence()) {
				UnitControlDisplay.showConfidence(this.agents);
			}

			// TODO: DEBUG INFO
			// Display the targets of all registered Units.
			for (GoapAgent goapAgent : this.agents) {
				Unit unit = ((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit();

				if (unit.isFollowing()) {
					Display.showUnitTile(unit, new bwapi.Color(255, 0, 0));
					Core.getInstance().getGame().drawLineMap(unit.getPosition(), unit.getTargetPosition(),
							new bwapi.Color(255, 0, 0));
				} else {
					Display.showUnitTarget(unit, new Color(0, 0, 255));
				}
			}
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
					GoapAgent agent = this.goapAgentFactory.createAgent(unit);

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
		// Update a single GoapAgent from the currently stored ones and place
		// him at the end of the Queue.
		GoapAgent currentAgent = this.agentUpdateQueueCombatUnits.poll();

		if (currentAgent != null) {
			this.updateGoapAgentProcedure(currentAgent);
			this.agentUpdateQueueCombatUnits.add(currentAgent);
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

		// If the building can construct addons, remove the contended
		// TilePositions that block any other buildings. This is only done with
		// buildings and not addons themselves since removing the contended
		// TilePositions when the addon is destroyed would allow workers to
		// construct other buildings on these tiles even though the main
		// building is still intact and could add a new addon itself.
		this.removeExtraAddonContendedTilePositions(unit);
	}

	/**
	 * Function for removing any contended TilePositions that were contended by
	 * default to ensure the construction of any future addons. This function is
	 * directed towards Terran structures like the Terran_Factory that are able
	 * to construct an addon to change their behavior. These TilePositions must
	 * be freed again after the building is destroyed.
	 * 
	 * @param unit
	 *            the building whose addon spots are going to be reserved. Must
	 *            be able to construct addons for this function to have any
	 *            effect at all.
	 */
	private void removeExtraAddonContendedTilePositions(Unit unit) {
		if (unit.canBuildAddon()) {
			HashSet<TilePosition> extraAddonSpace = new HashSet<>();
			TilePositionContenderFactory.addAdditionalAddonSpace(unit.getType(), unit.getTilePosition(),
					extraAddonSpace);

			this.informationStorage.getMapInfo().getTilePositionContenders().removeAll(extraAddonSpace);
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

			// Signal the agent that it is getting destroyed.
			((PlayerUnit) matchingAgent.getAssignedGoapUnit()).destroy();
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
	 * Function for adding a Unit to the List of controllable Units.
	 * 
	 * @param unit
	 *            the Unit that is going to be controlled.
	 */
	public void addToUnitControl(Unit unit) {
		if (unit.getPlayer() == Core.getInstance().getPlayer() && !this.ignoredUnitTypes.contains(unit.getType())) {
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
		if (!this.ignoredUnitTypes.contains(unit.getType())) {
			this.unitsToRemove.add(unit);
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
