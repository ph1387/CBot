package buildingOrderModule.stateFactories.updater;

import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoringDirector;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.stateFactories.actions.AvailableActionsTerran;
import buildingOrderModule.stateFactories.actions.executableActions.BuildAddonTerran_MachineShop;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_SiegeMode;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SiegeTank;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;

// TODO: UML ADD
/**
 * ActionUpdaterTerran.java --- Updater for updating a
 * {@link AvailableActionsTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionUpdaterTerran extends ActionUpdaterSimulationQueue {

	public ActionUpdaterTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	@Override
	protected HashSet<ActionType> generateAllAvailableActionTypes() {
		HashSet<ActionType> availableActionTypes = new HashSet<>();

		try {
			ActionType constructBarracks = new ConstrucActionTerran_Barracks(1);
			ActionType constructFactory = new ConstrucActionTerran_Factory(1);
			ActionType constructCenter = new ConstructActionCenter(1);
			ActionType trainWorker = new TrainUnitActionWorker(1);
			ActionType trainMarine = new TrainUnitActionTerran_Marine(1);
			ActionType trainSiegeTank = new TrainUnitActionTerran_SiegeTank(1);

			ActionType constructMachineShop = new BuildAddonTerran_MachineShop(1);

			ActionType researchSiegeMode = new ResearchActionTerran_SiegeMode(1);

			availableActionTypes.add(constructBarracks);
			availableActionTypes.add(constructFactory);
			availableActionTypes.add(constructCenter);
			availableActionTypes.add(trainWorker);
			availableActionTypes.add(trainMarine);
			availableActionTypes.add(trainSiegeTank);

			availableActionTypes.add(constructMachineShop);

			availableActionTypes.add(researchSiegeMode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return availableActionTypes;
	}

	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirector();
	}

}
