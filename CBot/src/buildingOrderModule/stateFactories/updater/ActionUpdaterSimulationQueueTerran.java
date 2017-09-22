package buildingOrderModule.stateFactories.updater;

import java.util.HashMap;
import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import buildingOrderModule.scoringDirector.ScoringDirectorTerranDefault;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.TypeWrapper;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.GoapAction;

/**
 * ActionUpdaterSimulationQueueTerran.java --- Updater for updating a
 * {@link AvailableActionsSimulationQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionUpdaterSimulationQueueTerran extends ActionUpdaterSimulationQueue {

	// TODO: UML ADD
	private HashMap<TypeWrapper, Integer> simulationQueueResultActionTypes;
	// TODO: UML ADD
	private HashMap<TypeWrapper, Integer> informationStorageQueuesActionTypes;

	public ActionUpdaterSimulationQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	protected HashSet<ActionType> generateAllAvailableActionTypes(BuildActionManager manager) {
		HashSet<ActionType> availableActionTypes = new HashSet<>();
		HashSet<GoapAction> availableActions = manager.getAvailableActions();

		// Get all the Types plus their amount that are currently produced and
		// inside the action Queue. Used to prevent i.e. building two centers.
		this.simulationQueueResultActionTypes = this.extractAllProducedTypes();
		// Also extract the types that are currently inside the
		// InformationStorage Queues:
		this.informationStorageQueuesActionTypes = this.extractAllForwardedTypes();

		// Transform each available Action into a ActionType.
		for (GoapAction goapAction : availableActions) {
			try {
				ActionType actionType = (ActionType) goapAction;

				// Some ActionTypes required special treatment regarding the
				// adding towards the available ActionTypes HashSet.
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
				// ----- Addons:
				case "Terran_Machine_Shop":
					if (this.canAddMachineShop(manager, actionType)) {
						availableActionTypes.add(actionType);
					}
					break;
				// ----- Technologies:
				case "Tank_Siege_Mode":
					if (this.canResearchTechnology(manager, actionType)) {
						availableActionTypes.add(actionType);
					}
					break;
				case "Stim_Packs":
					if (this.canResearchTechnology(manager, actionType)) {
						availableActionTypes.add(actionType);
					}
					break;
				// ----- Upgrades:
				case "U_238_Shells":
					if (this.canUpgrade(manager, actionType)) {
						availableActionTypes.add(actionType);
					}
					break;
				default:
					availableActionTypes.add(actionType);
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

	// TODO: UML ADD
	/**
	 * Function for determining if a technology can be researched or not. The
	 * provided ActionType <b>must</b> result in a TechType. If this is not the
	 * case, the function will fail. The function tests if the Player has
	 * researched the resulting TechType of the provided {@link ActionType} and
	 * if he is currently not researching it. Also other checks are done:
	 * <ul>
	 * <li>Test if the required UnitType (Research facility) exists at least
	 * once.</li>
	 * <li>Test if the {@link ActionType} was not already forwarded or is
	 * currently in the research Queue.</li>
	 * <li>Test if an idling, required research facility exists.</li>
	 * </ul>
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Unit counts
	 *            as well as the difference Queues.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if all criteria regarding the research of the TechType of
	 *         the provided {@link ActionType} are matched, false if not.
	 */
	private boolean canResearchTechnology(BuildActionManager manager, ActionType actionType) {
		Player player = Core.getInstance().getPlayer();
		boolean notResearched = !player.hasResearched(actionType.defineResultType().getTechType());
		boolean notResearching = !player.isResearching(actionType.defineResultType().getTechType());

		boolean requiredTypeExist = this.doesRequiredTypeExist(manager, actionType);
		boolean notForwardedOrQueued = !this.wasForwardedOrQueued(actionType);
		boolean idleFacilitiyExists = this.isOneProducingFacilityIdle(manager, actionType);

		return notResearched && notResearching && requiredTypeExist && notForwardedOrQueued && idleFacilitiyExists;
	}

	// TODO: UML ADD
	/**
	 * Function for determining if a technology can be researched or not. The
	 * provided ActionType <b>must</b> result in a UpgradeType. If this is not
	 * the case, the function will fail. The function tests if the Player has
	 * not yet reached the maximum upgrade level of the resulting UpgradeType of
	 * the provided {@link ActionType} and if he is currently not performing it.
	 * Also other checks are done:
	 * <ul>
	 * <li>Test if the required UnitType (Upgrade facility) exists at least
	 * once.</li>
	 * <li>Test if the {@link ActionType} was not already forwarded or is
	 * currently in the upgrade Queue.</li>
	 * <li>Test if an idling, required upgrade facility exists.</li>
	 * </ul>
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Unit counts
	 *            as well as the difference Queues.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if all criteria regarding the research of the TechType of
	 *         the provided {@link ActionType} are matched, false if not.
	 */
	private boolean canUpgrade(BuildActionManager manager, ActionType actionType) {
		Player player = Core.getInstance().getPlayer();
		boolean maxLevelNotReached = player.getUpgradeLevel(actionType.defineResultType().getUpgradeType()) < player
				.getMaxUpgradeLevel(actionType.defineResultType().getUpgradeType());
		boolean notUpgrading = !player.isUpgrading(actionType.defineResultType().getUpgradeType());

		boolean requiredTypeExist = this.doesRequiredTypeExist(manager, actionType);
		boolean notForwardedOrQueued = !this.wasForwardedOrQueued(actionType);
		boolean idleFacilitiyExists = this.isOneProducingFacilityIdle(manager, actionType);

		return maxLevelNotReached && notUpgrading && requiredTypeExist && notForwardedOrQueued && idleFacilitiyExists;
	}

	// TODO: UML ADD
	/**
	 * Function for testing if the required <b>UnitType</b> of the provided
	 * {@link ActionType} exists at least once. This function will fail if the
	 * required type is not a UnitType!
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Unit counts.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if the required UnitType exists at least once, false if not.
	 */
	private boolean doesRequiredTypeExist(BuildActionManager manager, ActionType actionType) {
		return manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.getOrDefault(actionType.defineRequiredType().getUnitType(), 0) > 0;
	}

	// TODO: UML ADD
	/**
	 * Function for testing if the provided {@link ActionType} is going to be
	 * forwarded or is currently in one of the executing / waiting Queues of the
	 * Bot. These include i.e. the upgrade, training, research or addon Queue.
	 * 
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if the {@link ActionType} was forwarded into one of the
	 *         {@link InformationStorage}'s Queues or if it is still going to be
	 *         forwarded to them. Otherwise this function returns false.
	 */
	private boolean wasForwardedOrQueued(ActionType actionType) {
		return this.simulationQueueResultActionTypes.containsKey(actionType.defineResultType())
				|| this.informationStorageQueuesActionTypes.containsKey(actionType.defineResultType());
	}

	// TODO: UML ADD
	/**
	 * Function for testing if one completed and idle facility whose UnitType
	 * was defined in the provided {@link ActionType} exists. The
	 * {@link ActionType} of this function <b>must</b> define a UnitType as
	 * required type. Otherwise this function returns false.
	 * 
	 * @param manager
	 *            the manager which provides access to the
	 *            {@link InformationStorage} containing the current Units.
	 * @param actionType
	 *            the {@link ActionType} (Resulting in a TechType) that is going
	 *            to be checked.
	 * @return true if one (Or more) idling and completed facilities that are
	 *         defined as the required UnitType of the {@link ActionType}
	 *         exist(s). Otherwise this function returns false, if either the
	 *         required type is not a UnitType or no completed and idling Unit
	 *         is found.
	 */
	private boolean isOneProducingFacilityIdle(BuildActionManager manager, ActionType actionType) {
		boolean prodcuingFacilityIdles = false;

		// Find a required, idle and completed facility for the ActionType.
		for (Unit unit : manager.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
				.getOrDefault(actionType.defineRequiredType().getUnitType(), new HashSet<Unit>())) {
			if (unit.isIdle() && unit.isCompleted()) {
				prodcuingFacilityIdles = true;

				break;
			}
		}

		return prodcuingFacilityIdles;
	}

	// TODO: UML PARAMS
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

	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirectorTerranDefault(this.buildActionManager);
	}

}
