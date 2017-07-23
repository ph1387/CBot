package buildingOrderModule.stateFactories.updater;

import java.util.HashMap;
import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import buildingOrderModule.scoringDirector.ScoringDirectorTerran_Bio;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.TypeWrapper;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueueTerran;
import buildingOrderModule.stateFactories.actions.executableActions.BuildAddonTerran_MachineShop;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionRefinery;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_SiegeMode;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SiegeTank;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;

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
	protected HashSet<ActionType> generateAllAvailableActionTypes() {
		HashSet<ActionType> availableActionTypes = new HashSet<>();

		// Get all the Types plus their amount that are currently produced and
		// inside the action Queue. Used to prevent i.e. building two centers.
		HashMap<TypeWrapper, Integer> usedActionTypes = this.extractAllProducedTypes();
		// Also extract the types that are currently inside the
		// InformationStorage Queues:
		HashMap<TypeWrapper, Integer> forwardedActionTypes = this.extractAllForwardedTypes();

		try {
			ActionType constructBarracks = new ConstrucActionTerran_Barracks(1);
			ActionType constructFactory = new ConstrucActionTerran_Factory(1);
			ActionType constructCenter = new ConstructActionCenter(1);
			ActionType constructRefinery = new ConstructActionRefinery(1);
			ActionType trainWorker = new TrainUnitActionWorker(1);
			ActionType trainMarine = new TrainUnitActionTerran_Marine(1);
			ActionType trainSiegeTank = new TrainUnitActionTerran_SiegeTank(1);

			ActionType constructMachineShop = new BuildAddonTerran_MachineShop(1);

			ActionType researchSiegeMode = new ResearchActionTerran_SiegeMode(1);

			if (!usedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Command_Center)
					&& !forwardedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Command_Center)) {
				availableActionTypes.add(constructCenter);
			}
			if (!usedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Refinery)
					&& !forwardedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Refinery)) {
				availableActionTypes.add(constructRefinery);
			}
			availableActionTypes.add(constructBarracks);
			availableActionTypes.add(constructFactory);
			availableActionTypes.add(trainWorker);
			availableActionTypes.add(trainMarine);
			availableActionTypes.add(trainSiegeTank);

			availableActionTypes.add(constructMachineShop);

			if (!usedActionTypes.containsKey(TypeWrapper.TechType_Tank_Siege_Mode)
					&& !forwardedActionTypes.containsKey(TypeWrapper.TechType_Tank_Siege_Mode)) {
				availableActionTypes.add(researchSiegeMode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return availableActionTypes;
	}

	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirectorTerran_Bio();
	}

}
