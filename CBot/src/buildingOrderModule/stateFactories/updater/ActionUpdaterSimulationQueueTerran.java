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

// TODO: UML ADD
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
		Integer playerCenterCount = manager.getCurrentGameInformation().getCurrentUnits()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
		Integer playerRefineryCount = manager.getCurrentGameInformation().getCurrentUnits()
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
				case "UnitType_Terran_Command_Center":
					if (!usedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Command_Center)
							&& !forwardedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Command_Center)) {
						availableActionTypes.add(actionType);
					}
					break;
				case "UnitType_Terran_Refinery":
					// Only allow the construction of refineries when the center
					// count is larger than the refinery count.
					if (!usedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Refinery)
							&& !forwardedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Refinery)
							&& playerCenterCount != null && playerRefineryCount != null
							&& playerCenterCount > playerRefineryCount) {
						availableActionTypes.add(actionType);
					}
					break;
				case "TechType_Tank_Siege_Mode":
					if (!usedActionTypes.containsKey(TypeWrapper.TechType_Tank_Siege_Mode)
							&& !forwardedActionTypes.containsKey(TypeWrapper.TechType_Tank_Siege_Mode)) {
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

	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirectorTerran_Bio();
	}

}
