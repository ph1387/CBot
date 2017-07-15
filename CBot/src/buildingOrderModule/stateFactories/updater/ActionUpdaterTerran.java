package buildingOrderModule.stateFactories.updater;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.SimulationStarter;
import buildingOrderModule.stateFactories.actions.AvailableActionsTerran;
import buildingOrderModule.stateFactories.actions.executableActions.BuildAddonTerran_MachineShop;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ManagerBaseAction;
import buildingOrderModule.stateFactories.actions.executableActions.ResearchActionTerran_SiegeMode;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SiegeTank;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;

// TODO: UML ADD
/**
 * ActionUpdaterTerran.java --- Updater for updating a
 * {@link AvailableActionsTerran} instance.
 * 
 * @author P H - 14.07.2017
 *
 */
public class ActionUpdaterTerran extends ActionUpdaterGeneral {

	private HashSet<ActionType> actionTypes = generateAllAvailableActionTypes();

	// Simulation frequency:
	private Integer lastSimulationTimeStampFrames = null;
	private int nextSimulationTimeStampDifferenceFrames = 1000;

	// Multithreading for the building order simulation:
	private ConcurrentLinkedQueue<ArrayList<ActionType>> generatedActionTypeSequences = new ConcurrentLinkedQueue<>();
	private SimulationStarter simulationActionStarter = new SimulationStarter(generatedActionTypeSequences);

	public ActionUpdaterTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
	}

	// -------------------- Functions

	/**
	 * Function for generating all the available ActionTypes that will be used
	 * inside the different simulations.
	 * 
	 * @return a HashSet containing all available ActionTypes for simulations.
	 */
	private static HashSet<ActionType> generateAllAvailableActionTypes() {
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
	public void update(BuildActionManager manager) {
		// React on any results of the simulation Thread and update it if
		// necessary.
		if (!this.generatedActionTypeSequences.isEmpty()) {
			this.actOnSimulationThreadResult();
		}
		this.updateSimulationStarter();
	}

	// TODO: UML ADD JAVADOC
	private void actOnSimulationThreadResult() {
		ActionQueueSimulationResults simulationAction = (ActionQueueSimulationResults) this
				.getActionFromInstance(ActionQueueSimulationResults.class);
		ArrayList<ActionType> generatedResult = this.generatedActionTypeSequences.poll();
		ArrayList<ManagerBaseAction> transformedResult = new ArrayList<>();

		// Transform the ActionTypes back into ManagerBaseActions.
		for (ActionType actionType : generatedResult) {
			transformedResult.add((ManagerBaseAction) actionType);
		}

		// Forward the transformed ActionTypes towards the Action.
		simulationAction.setActionQueue(transformedResult);

		// TODO: WIP REMOVE
		System.out.println("\nACT ON RESULT:");
		for (ActionType actionType : generatedResult) {
			System.out.println("  - " + actionType.getClass().getSimpleName());
		}
		System.out.println("\n");
	}

	// TODO: UML ADD JAVADOC
	private void updateSimulationStarter() {
		// A certain time has to pass before a simulation is being started.
		if (this.lastSimulationTimeStampFrames == null || Core.getInstance().getGame()
				.getFrameCount() >= this.lastSimulationTimeStampFrames + this.nextSimulationTimeStampDifferenceFrames) {
			// Extract all currently relevant information.
			int currentFrameTimeStamp = Core.getInstance().getGame().getFrameCount();
			int currentMinerals = Core.getInstance().getPlayer().minerals();
			int currentGas = Core.getInstance().getPlayer().gas();
			UnitType workerUnitType = Core.getInstance().getPlayer().getRace().getWorker();
			List<Unit> units = Core.getInstance().getPlayer().getUnits();

			// Try running a simulation. If successful change the time stamp of
			// the last simulation that was run.
			if (this.simulationActionStarter.runStarter(this.actionTypes, units, currentMinerals, currentGas,
					workerUnitType, currentFrameTimeStamp)) {
				this.lastSimulationTimeStampFrames = currentFrameTimeStamp;
			}
		}
	}

}
