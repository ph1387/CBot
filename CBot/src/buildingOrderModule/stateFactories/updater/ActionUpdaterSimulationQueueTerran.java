package buildingOrderModule.stateFactories.updater;

import java.util.HashMap;
import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import buildingOrderModule.scoringDirector.ScoringDirectorTerran_Bio;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.TypeWrapper;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import core.Core;
import javaGOAP.GoapAction;

/**
 * ActionUpdaterSimulationQueueTerran.java --- Updater for updating a
 * {@link AvailableActionsSimulationQueueTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionUpdaterSimulationQueueTerran extends ActionUpdaterSimulationQueue {

	public ActionUpdaterSimulationQueueTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	protected HashSet<ActionType> generateAllAvailableActionTypes(BuildActionManager manager) {
		HashSet<ActionType> availableActionTypes = new HashSet<>();
		HashSet<GoapAction> availableActions = manager.getAvailableActions();
		Integer playerCenterCount = manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
		Integer playerRefineryCount = manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getRefinery());

		// Get all the Types plus their amount that are currently produced and
		// inside the action Queue. Used to prevent i.e. building two centers.
		HashMap<TypeWrapper, Integer> usedActionTypes = this.extractAllProducedTypes();
		// Also extract the types that are currently inside the
		// InformationStorage Queues:
		HashMap<TypeWrapper, Integer> forwardedActionTypes = this.extractAllForwardedTypes();

		// Transform each available Action into a ActionType.
		for (GoapAction goapAction : availableActions) {
			try {
				ActionType actionType = (ActionType) goapAction;

				// Some ActionTypes required special treatment regarding the
				// adding towards the available ActionTypes HashSet.
				switch (actionType.defineResultType().toString()) {
				case "Terran_Command_Center":
					if (!usedActionTypes.containsKey(actionType.defineResultType())
							&& !forwardedActionTypes.containsKey(actionType.defineResultType())) {
						availableActionTypes.add(actionType);
					}
					break;
				case "Terran_Refinery":
					// Only allow the construction of refineries when the center
					// count is larger than the refinery count.
					if (!usedActionTypes.containsKey(actionType.defineResultType())
							&& !forwardedActionTypes.containsKey(actionType.defineResultType())
							&& (playerRefineryCount == null || (playerCenterCount != null && playerRefineryCount != null
									&& playerCenterCount > playerRefineryCount))) {
						availableActionTypes.add(actionType);
					}
					break;
				case "Tank_Siege_Mode":
					if (!usedActionTypes.containsKey(actionType.defineResultType())
							&& !forwardedActionTypes.containsKey(actionType.defineResultType())) {
						availableActionTypes.add(actionType);
					}
					break;
				case "Terran_Machine_Shop":
					if (this.canAddMachineShop(manager, actionType, usedActionTypes, forwardedActionTypes)) {
						availableActionTypes.add(actionType);
					}
					break;
				default:
					availableActionTypes.add(actionType);
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return availableActionTypes;
	}

	// TODO: UML ADD
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
	 * @param usedActionTypes
	 *            the currently queued ActionTypes that were already forwarded.
	 * @param forwardedActionTypes
	 *            the Queue of ActionTypes that is going to be forwarded step by
	 *            step to the executing instance.
	 * @return true if fewer Machine_Shops are queued / already forwarded and
	 *         constructed than the total number of Terran_Factories, false
	 *         otherwise.
	 */
	private boolean canAddMachineShop(BuildActionManager manager, ActionType actionType,
			HashMap<TypeWrapper, Integer> usedActionTypes, HashMap<TypeWrapper, Integer> forwardedActionTypes) {
		Integer factoryCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.get(actionType.defineRequiredType().getUnitType());
		Integer machineShopCount = manager.getInformationStorage().getCurrentGameInformation().getCurrentUnitCounts()
				.get(actionType.defineResultType().getUnitType());
		Integer forwardedMachineShopCount = forwardedActionTypes.get(actionType.defineResultType());
		Integer queuedMachineShopCount = usedActionTypes.get(actionType.defineResultType());

		Integer totalMachineShopCount = 0;
		if (machineShopCount != null) {
			totalMachineShopCount += machineShopCount;
		}
		if (forwardedMachineShopCount != null) {
			totalMachineShopCount += forwardedMachineShopCount;
		}
		if (queuedMachineShopCount != null) {
			totalMachineShopCount += queuedMachineShopCount;
		}

		boolean factoriesExist = factoryCount != null && factoryCount > 0;
		boolean machineShopsExist = totalMachineShopCount > 0;
		boolean fewerMachineShopsThanFactories = (!machineShopsExist && factoriesExist)
				|| (factoriesExist && machineShopsExist && totalMachineShopCount < factoryCount);

		return fewerMachineShopsThanFactories;
	}

	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirectorTerran_Bio();
	}

}
