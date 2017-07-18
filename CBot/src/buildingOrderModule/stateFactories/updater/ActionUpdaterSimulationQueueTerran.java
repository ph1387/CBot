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

		try {
			ActionType constructBarracks = new ConstrucActionTerran_Barracks(1);
			ActionType constructFactory = new ConstrucActionTerran_Factory(1);
			ActionType constructCenter = new ConstructActionCenter(1);
			ActionType trainWorker = new TrainUnitActionWorker(1);
			ActionType trainMarine = new TrainUnitActionTerran_Marine(1);
			ActionType trainSiegeTank = new TrainUnitActionTerran_SiegeTank(1);

			ActionType constructMachineShop = new BuildAddonTerran_MachineShop(1);

			ActionType researchSiegeMode = new ResearchActionTerran_SiegeMode(1);

			if(!usedActionTypes.containsKey(TypeWrapper.UnitType_Terran_Command_Center)) {
				availableActionTypes.add(constructCenter);
			}
			availableActionTypes.add(constructBarracks);
			availableActionTypes.add(constructFactory);
			availableActionTypes.add(trainWorker);
			availableActionTypes.add(trainMarine);
			availableActionTypes.add(trainSiegeTank);

			availableActionTypes.add(constructMachineShop);

			if(!usedActionTypes.containsKey(TypeWrapper.TechType_Tank_Siege_Mode)) {
				availableActionTypes.add(researchSiegeMode);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return availableActionTypes;
	}

	/**
	 * Function for extracting all currently produces Types (TypeWrappers) from
	 * the action Queue.
	 * 
	 * @return a HashMap containing all TypeWrappers that are produced in the
	 *         action Queue of the actionQueueSimulationResults action. </br>
	 * 		Key: The produces TypeWrapper.</br>
	 * 		Value: The number of times it was found inside the aciton Queue.
	 */
	private HashMap<TypeWrapper, Integer> extractAllProducedTypes() {
		HashMap<TypeWrapper, Integer> usedActionTypes = new HashMap<TypeWrapper, Integer>();

		if (this.actionQueueSimulationResults != null) {
			for (ActionType actionType : this.actionQueueSimulationResults.getActionQueue()) {
				if (usedActionTypes.containsKey(actionType.defineResultType())) {
					usedActionTypes.put(actionType.defineResultType(),
							usedActionTypes.get(actionType.defineResultType()) + 1);
				} else {
					usedActionTypes.put(actionType.defineResultType(), 1);
				}
			}
		}

		return usedActionTypes;
	}

	@Override
	protected ScoringDirector defineScoringDirector() {
		return new ScoringDirectorTerran_Bio();
	}

}
