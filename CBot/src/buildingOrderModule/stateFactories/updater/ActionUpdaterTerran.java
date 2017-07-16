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
	// The max difference of the index and the size of the action Queue. When
	// the difference is less or equal this value a new simulation Thread is
	// started.
	private int maxActionQueueIndexOffsetTilEnd = 2;
	// Time stamp of the last check of the action Queue.
	private Integer lastSimulationTimeStampFrames = null;
	// Time difference between the checking if the action Queue was being worked
	// on.
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

	// TODO: UML ADD
	/**
	 * Function for acting upon a generated action Queue by the SimulatorThread
	 * itself. The ActionTypes are transformed into ManagerBaseActions and then
	 * added to the existing action Queue of the ActionQueueSimulationResults
	 * Action.
	 */
	private void actOnSimulationThreadResult() {
		ActionQueueSimulationResults simulationAction = (ActionQueueSimulationResults) this
				.getActionFromInstance(ActionQueueSimulationResults.class);
		ArrayList<ActionType> generatedResult = this.generatedActionTypeSequences.poll();
		ArrayList<ManagerBaseAction> transformedResult = new ArrayList<>();

		// Transform the ActionTypes back into ManagerBaseActions.
		for (ActionType actionType : generatedResult) {
			transformedResult.add((ManagerBaseAction) actionType);
		}

		// Forward the transformed ActionTypes towards the Action itself.
		simulationAction.addToActionQueue(transformedResult);

		// TODO: WIP REMOVE
		System.out.println("\nACT ON RESULT:");
		for (ActionType actionType : generatedResult) {
			System.out.println("  - " + actionType.getClass().getSimpleName());
		}
		System.out.println("\n");
	}

	// TODO: UML ADD
	/**
	 * Function for updating all information regarding the SimulationStarter.
	 * Either resetting existing action Queues of the
	 * ActionqueueSimulationResults instance itself when no progress is being
	 * made (fast enough) or starting a new SimulatorThread when the existing
	 * action Queue is near its end and a new Queue is needed to further
	 * progress into the game.
	 */
	private void updateSimulationStarter() {
		// Check in predefined time differences if any changes in the action
		// Queues index occurred. If this is not the case the Bot is unable to
		// execute the Actions defined in it and therefore the action Queue must
		// be reseted.
		if (this.lastSimulationTimeStampFrames == null || Core.getInstance().getGame().getFrameCount()
				- this.lastSimulationTimeStampFrames >= this.nextSimulationTimeStampDifferenceFrames) {
			ActionQueueSimulationResults action = (ActionQueueSimulationResults) this
					.getActionFromInstance(ActionQueueSimulationResults.class);
			this.lastSimulationTimeStampFrames = Core.getInstance().getGame().getFrameCount();

			// If changes occurred reset the flag.
			if (action.didChangesOccurr()) {
				action.resetChangesFlag();
			}
			// If no changes occurred first set the index to the maximum
			// possible index and then call the reset function. This removes all
			// stored actions and resets the index to 0.
			else {
				action.setIndex(action.getActionQueue().size() - 1);
				action.reset();
			}
		}

		// Check if the index of the action Queue is nearly at the end of the
		// Queue. If it is, start a new simulation.
		if (this.isActionQueueNearlyFinished() && !this.simulationActionStarter.isRunning()) {
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

	// TODO: UML ADD
	/**
	 * Function for testing if the index of the action Queue of the
	 * ActionQueueSimulationResults is nearly at the end of the action Queue.
	 * 
	 * @return true if the max difference of the index and the size of the
	 *         action Queue is being met (Else false).
	 */
	private boolean isActionQueueNearlyFinished() {
		ActionQueueSimulationResults action = (ActionQueueSimulationResults) this
				.getActionFromInstance(ActionQueueSimulationResults.class);

		// The index must nearly be at the end for the function to return true.
		return action.getActionQueue().size() - action.getIndex() <= this.maxActionQueueIndexOffsetTilEnd;
	}

}
