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
import unitControlModule.unitWrappers.PlayerBuilding;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * UnitControlModule.java --- Module for controlling the Player's units.
 * 
 * @author P H - 29.01.2017
 *
 */
public class UnitControlModule {

	private HashSet<GoapAgent> agents = new HashSet<GoapAgent>();
	private HashSet<PlayerBuilding> buildings = new HashSet<PlayerBuilding>();
	private Queue<Unit> unitsToAdd = new LinkedList<Unit>();
	private Queue<Unit> unitsToRemove = new LinkedList<Unit>();

	private InformationStorage informationStorage;

	public UnitControlModule(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	/**
	 * Used for updating all Player Units and buildings (and their actions) in
	 * the game.
	 */
	public void update() {
		this.addTrackedUnits();
		this.removeTrackedUnits();

		// Update Units
		for (GoapAgent goapAgent : this.agents) {

			// TODO: DEBUG INFO
			Display.showUnitTarget(Core.getInstance().getGame(),
					((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit(), new Color(0, 0, 255));

			try {
				if (((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit().exists()) {
					goapAgent.update();
				} else {
					this.unitsToRemove.add(((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit());
				}
			} catch (Exception e) {
				System.out.println("An Agent failed to update properly: " + goapAgent + " - Unit: "
						+ ((PlayerUnit) goapAgent.getAssignedGoapUnit()).getUnit());
				e.printStackTrace();
			}
		}

		// Update buildings
		for (PlayerBuilding building : this.buildings) {
			try {
				if (building.getUnit().exists()) {
					building.update();
				} else {
					this.unitsToRemove.add(building.getUnit());
				}
			} catch (Exception e) {
				System.out.println(
						"A Building failed to update properly: " + building + " - Unit: " + building.getUnit());
				e.printStackTrace();
			}
		}

		// Display all important information on the screen
		UnitControlDisplay.showImportantInformation(this.agents, this.buildings, this.informationStorage);
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
					this.buildings.add(new PlayerBuilding(unit, this.informationStorage));
				} else {
					this.agents.add(GoapAgentFactory.createAgent(unit, this.informationStorage));
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
			// Reference of the Unit changes!
			// -> Unit reference here sometimes is not the saved reference
			if (building.getUnit() == unit || building.getUnit().getPosition().equals(unit.getPosition())) {
				matchingObject = building;

				break;
			}
		}

		if (matchingObject != null) {
			this.buildings.remove(matchingObject);
		}
		// TODO: REMOVE Safety feature since it is not clear if the Unit is
		// found
		else {
			try {
				throw new Exception("No Matching building Unit was found!");
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

		for (GoapAgent agent : this.agents) {
			Unit u = ((PlayerUnit) agent.getAssignedGoapUnit()).getUnit();

			// Reference of the Unit changes!
			// -> Unit reference here sometimes is not the saved reference
			if (((PlayerUnit) agent.getAssignedGoapUnit()).getUnit() == unit
					|| u.getPosition().equals(unit.getPosition())) {
				matchingAgent = agent;

				break;
			}
		}

		if (matchingAgent != null) {
			this.agents.remove(matchingAgent);

			if (unit.getType().isWorker()) {
				this.removeAssignedWorkerEntries(unit);
				this.informationStorage.getWorkerConfig().decrementTotalWorkerCount();
			}
		}
		// TODO: REMOVE Safety feature since it is not clear if the Unit is
		// found
		else {
			try {
				throw new Exception("No Matching building Unit was found!");
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
				.forEach(new BiConsumer<Unit, ArrayList<Unit>>() {
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

}
