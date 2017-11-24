package buildingOrderModule.stateFactories.updater;

import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import buildingOrderModule.scoringDirector.ScoringDirectorTerranBio;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import bwapi.UnitType;
import core.Core;
import javaGOAP.GoapAction;

// TODO: UML ADD
/**
 * ActionUpdaterSimulationQueueTerranBio.java --- Updater for updating a
 * {@link AvailableActionsSimulationQueueTerran} instance matching a Terran-Bio
 * configuration, therefore further specifying the basic configuration generated
 * by the superclass.
 * 
 * @author P H - 18.11.2017
 *
 */
public class ActionUpdaterSimulationQueueTerranBio extends ActionUpdaterSimulationQueueTerranDefault {

	private static final int SECOND_ENGINEERINGBAY_FORBID_UNITL_SECONDS = 900;
	private static final int FACTORY_FORBID_UNTIL_SECONDS = 600;

	public ActionUpdaterSimulationQueueTerranBio(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	// TODO: UML ADD
	@Override
	protected HashSet<ActionType> generateAllAvailableActionTypes(BuildActionManager manager) {
		HashSet<ActionType> availableActionTypes = super.generateAllAvailableActionTypes(manager);
		HashSet<GoapAction> availableActions = manager.getAvailableActions();

		for (GoapAction goapAction : availableActions) {
			try {
				ActionType actionType = (ActionType) goapAction;

				if (actionType.defineResultType().isUnitType()
						&& this.doesRequiredUnitExist(manager, actionType.defineResultType().getUnitType())
						&& this.doesRequiredTechExist(manager, actionType.defineResultType().getUnitType())) {
					switch (actionType.defineResultType().toString()) {

					// ----- Buildings:
					case "Terran_Engineering_Bay":
						Integer playerEngineeringBayCount = manager.getInformationStorage().getCurrentGameInformation()
								.getCurrentUnitCounts().getOrDefault(UnitType.Terran_Engineering_Bay, 0);

						// A second Engineering_Bay can be build when a specific
						// number of seconds has passed.
						if (!this.wasForwardedOrQueued(actionType) && playerEngineeringBayCount < 2 && Core
								.getInstance().getGame().elapsedTime() >= SECOND_ENGINEERINGBAY_FORBID_UNITL_SECONDS) {
							availableActionTypes.add(actionType);
						}
						break;
					case "Terran_Armory":
						// Armories are not needed since the Bot will focus on
						// upgrading Bio-Units instead of machine ones.
						availableActionTypes.remove(actionType);
						break;
					case "Terran_Factory":
						// Forbid the construction of factories until a certain
						// point in the game to let the Bot focus on Bio-Units
						// since Terran_Factories generally have a higher score
						// than Terran_Barracks.
						if (Core.getInstance().getGame().elapsedTime() <= FACTORY_FORBID_UNTIL_SECONDS) {
							availableActionTypes.remove(actionType);
						}
						break;
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

	// TODO: UML ADD
	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirectorTerranBio(this.buildActionManager);
	}
}
