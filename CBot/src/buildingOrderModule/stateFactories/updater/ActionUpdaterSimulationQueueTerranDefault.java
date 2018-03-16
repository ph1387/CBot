package buildingOrderModule.stateFactories.updater;

import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import bwapi.Player;
import bwapi.UnitType;
import core.Core;
import javaGOAP.GoapAction;

/**
 * ActionUpdaterSimulationQueueTerranDefault.java --- Updater for updating a
 * {@link AvailableActionsSimulationQueueTerran} instance. This Class only
 * covers the most basic, general Terran buildings, addons, etc.. Each subclass
 * can and should further define the allowed / forbidden actions based on the
 * playstyle of the Bot (I.e. Bio-Terran, Machine-Terran, etc.).
 * 
 * @author P H - 14.07.2017
 *
 */
public abstract class ActionUpdaterSimulationQueueTerranDefault extends ActionUpdaterSimulationQueue {

	private static final int STARPORT_FORBID_UNTIL_SECONDS = 900;

	public ActionUpdaterSimulationQueueTerranDefault(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	protected HashSet<ActionType> generateAllAvailableActionTypes(BuildActionManager manager) {
		HashSet<ActionType> availableActionTypes = new HashSet<>();
		HashSet<GoapAction> availableActions = manager.getAvailableActions();

		// Transform each available Action into a ActionType.
		for (GoapAction goapAction : availableActions) {
			try {
				ActionType actionType = (ActionType) goapAction;

				// Some ActionTypes required special treatment regarding the
				// adding towards the available ActionTypes HashSet.
				if (actionType.defineResultType().isTechType()) {
					if (this.canResearchTechnology(manager, actionType)) {
						availableActionTypes.add(actionType);
					}
				} else if (actionType.defineResultType().isUpgradeType()) {
					if (this.canUpgrade(manager, actionType)) {
						availableActionTypes.add(actionType);
					}
				}
				// Different UnitTypes require different kinds of other
				// UnitTypes and maybe TechTypes before they can be either
				// constructed or trained!
				else if (actionType.defineResultType().isUnitType()
						&& this.doesRequiredUnitExist(manager, actionType.defineResultType().getUnitType())
						&& this.doesRequiredTechExist(manager, actionType.defineResultType().getUnitType())) {
					switch (actionType.defineResultType().toString()) {

					// ----- Buildings:
					case "Terran_Command_Center":
						if (!this.wasForwardedOrQueued(actionType)) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Refinery":
						Player player = Core.getInstance().getPlayer();
						Integer playerCenterCount = manager.getCurrentGameInformation().getCurrentUnitCounts()
								.getOrDefault(player.getRace().getCenter(), 0);
						Integer playerRefineryCount = manager.getCurrentGameInformation().getCurrentUnitCounts()
								.getOrDefault(player.getRace().getRefinery(), 0);

						if (!this.wasForwardedOrQueued(actionType)
								&& (playerRefineryCount.equals(0) || (playerCenterCount > playerRefineryCount))) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Academy":
						Integer playerAcademyCount = manager.getInformationStorage().getCurrentGameInformation()
								.getCurrentUnitCounts().getOrDefault(UnitType.Terran_Academy, 0);

						if (!this.wasForwardedOrQueued(actionType) && playerAcademyCount.equals(0)) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Engineering_Bay":
						Integer playerEngineeringBayCount = manager.getInformationStorage().getCurrentGameInformation()
								.getCurrentUnitCounts().getOrDefault(UnitType.Terran_Engineering_Bay, 0);

						if (!this.wasForwardedOrQueued(actionType) && playerEngineeringBayCount.equals(0)) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Science_Facility":
						Integer playerScienceFacilityCount = manager.getInformationStorage().getCurrentGameInformation()
								.getCurrentUnitCounts().getOrDefault(UnitType.Terran_Science_Facility, 0);

						if (!this.wasForwardedOrQueued(actionType) && playerScienceFacilityCount.equals(0)) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Armory":
						Integer playerArmoryCount = manager.getInformationStorage().getCurrentGameInformation()
								.getCurrentUnitCounts().getOrDefault(UnitType.Terran_Armory, 0);

						if (!this.wasForwardedOrQueued(actionType) && playerArmoryCount.equals(0)) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Starport":
						// Must be forbidden until a later stage in the game.
						// This is due to the Bot building it too early
						// otherwise and therefore messing up the different
						// build orders.
						if (Core.getInstance().getGame().elapsedTime() >= STARPORT_FORBID_UNTIL_SECONDS) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Missile_Turret":
						if (this.canAddMissileTurret(manager, actionType)) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Bunker":
						if (!this.wasForwardedOrQueued(actionType) && this.canAddBunker(manager, actionType)) {
							availableActionTypes.add(actionType);
						}
						break;

					// ----- Addons:
					case "Terran_Machine_Shop":
						if (this.canAddMachineShop(manager, actionType)) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Control_Tower":
						Integer playerControlTowerCount = manager.getInformationStorage().getCurrentGameInformation()
								.getCurrentUnitCounts().getOrDefault(UnitType.Terran_Control_Tower, 0);

						if (!this.wasForwardedOrQueued(actionType) && playerControlTowerCount.equals(0)) {
							availableActionTypes.add(actionType);
						}
						break;

					default:
						availableActionTypes.add(actionType);
					}
				}
			}
			// Casting of action Queues and starting Queues: Conversion to
			// ActionType fails!
			catch (ClassCastException e) {
				// e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return availableActionTypes;
	}

	/**
	 * Function for determining if a Terran_Machine_Shop can be added towards
	 * the Queue of addons to construct. The function takes the already queued
	 * as well as forwarded Machine_Shops into account and returns a boolean
	 * based on the number of them in relation to the total number of factories
	 * since a Machine_Shop needs to be added towards one.
	 * 
	 * @param manager
	 *            the BuildActionManager for accessing the InformationStorage
	 *            which contains all the current game information.
	 * @param actionType
	 *            the ActionType that is going to result in a Machine_Shop and
	 *            requires a Terran_Factory.
	 * @return true if fewer Machine_Shops are queued / already forwarded and
	 *         constructed than the total number of Terran_Factories, false
	 *         otherwise.
	 */
	private boolean canAddMachineShop(BuildActionManager manager, ActionType actionType) {
		Integer factoryCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(actionType.defineRequiredType().getUnitType(), 0);
		Integer machineShopCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(actionType.defineResultType().getUnitType(), 0);
		Integer simulationResultMachineShopCount = this.simulationQueueResultActionTypes
				.getOrDefault(actionType.defineResultType(), 0);
		Integer queuedMachineShopCount = this.informationStorageQueuesActionTypes
				.getOrDefault(actionType.defineResultType(), 0);
		Integer totalMachineShopCount = machineShopCount + simulationResultMachineShopCount + queuedMachineShopCount;

		boolean factoriesExist = factoryCount > 0;
		boolean machineShopsExist = totalMachineShopCount > 0;
		boolean fewerMachineShopsThanFactories = (!machineShopsExist && factoriesExist)
				|| (factoriesExist && machineShopsExist && totalMachineShopCount < factoryCount);

		return fewerMachineShopsThanFactories;
	}

	// TODO: UML ADD
	/**
	 * Function for determining if a Terran_Missile_Turret can be added towards
	 * the construction Queue. This function takes (All) currently constructed
	 * buildings as well as existing Terran_Missile_Turrets in consideration in
	 * order to determine a maximum number of turrets.
	 * 
	 * @param manager
	 *            the BuildActionManager for accessing the InformationStorage
	 *            which contains all the current game information.
	 * @param actionType
	 *            the ActionType that is going to result in a Missile_Turret.
	 * @return true if a Missile_Turret can be constructed, false otherwise.
	 */
	private boolean canAddMissileTurret(BuildActionManager manager, ActionType actionType) {
		Integer missileTurretCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(actionType.defineResultType().getUnitType(), 0);
		Integer factoryCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Factory, 0);
		Integer centerCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Command_Center, 0);
		Integer barrackCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Barracks, 0);
		Integer starportCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Starport, 0);
		Integer supplyDepotCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Supply_Depot, 0);
		Integer refineryCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Refinery, 0);

		// 2 for each Command_Center, 0.5 for each training building, 0.25 for
		// each Supply_Depot or Refinery
		int totalPossibleNumber = centerCount * 2 + (factoryCount + barrackCount + starportCount) / 2
				+ (supplyDepotCount + refineryCount) / 4;

		return missileTurretCount < totalPossibleNumber;
	}

	// TODO: UML ADD
	/**
	 * Function for determining if a Terran_Bunker can be added towards the
	 * construction Queue.
	 * 
	 * @param manager
	 *            the BuildActionManager for accessing the InformationStorage
	 *            which contains all the current game information.
	 * @param actionType
	 *            the ActionType that is going to result in a Bunker.
	 * @return true if a Bunker can be constructed, false otherwise.
	 */
	private boolean canAddBunker(BuildActionManager manager, ActionType actionType) {
		Integer simulationResultBunkerCount = this.simulationQueueResultActionTypes
				.getOrDefault(actionType.defineResultType(), 0);
		Integer queuedBunkerCount = this.informationStorageQueuesActionTypes.getOrDefault(actionType.defineResultType(),
				0);
		Integer bunkerCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(actionType.defineResultType().getUnitType(), 0);
		Integer centerCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(UnitType.Terran_Command_Center, 0);

		return (simulationResultBunkerCount + queuedBunkerCount + bunkerCount) < centerCount;
	}

}
