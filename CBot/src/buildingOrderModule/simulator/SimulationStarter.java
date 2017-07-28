package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * SimulationStarter.java --- Class used for starting a simulation with the
 * currently available and provided information. The results are then added
 * towards a given concurrent Queue.
 * 
 * @author P H - 15.07.2017
 *
 */
public class SimulationStarter {

	private Simulator simulator;
	private Thread simulationThread;

	private ConcurrentLinkedQueue<ArrayList<ActionType>> generatedActionTypeSequences;

	public SimulationStarter(ConcurrentLinkedQueue<ArrayList<ActionType>> generatedActionTypeSequences) {
		this.generatedActionTypeSequences = generatedActionTypeSequences;

		this.simulator = new Simulator(new HashSet<ActionType>());
	}

	// -------------------- Functions

	/**
	 * Function for testing if the simulation Thread is currently active /
	 * running.
	 * 
	 * @return true if the Thread is running, false if not.
	 */
	public boolean isRunning() {
		return this.simulationThread != null && this.simulationThread.isAlive();
	}

	/**
	 * Function for starting a new simulation Thread. This function only works /
	 * returns true if the previous Thread is finished or if none was started
	 * before.
	 * 
	 * @param actionTypes
	 *            the ActionTypes that are going to be used in the simulation.
	 * @param units
	 *            the Units that are going to be used in the simulation.
	 * @param currentMinerals
	 *            the current gas count. Defines the basis for all gas dependent
	 *            operations.
	 * @param currentGas
	 *            the current mineral count. Defines the basis for all mineral
	 *            dependent operations.
	 * @param workerUnitType
	 *            the UnitType that defines the worker. Different for all Races.
	 * @param currentFrameTimeStamp
	 *            the current time stamp in frames.
	 * @return true if a simulation Thread was started, false if not.
	 */
	public boolean runStarter(HashSet<ActionType> actionTypes, List<Unit> units, int currentMinerals, int currentGas,
			UnitType workerUnitType, int currentFrameTimeStamp) {
		boolean success = false;

		// The previous SimulatorThread must have finished before a new one can
		// be started.
		if (this.simulationThread == null || this.simulationThread.getState() == Thread.State.TERMINATED) {
			// Remove the Thread from the pool of Threads that must be waited
			// for at the end of the game.
			if (this.simulationThread != null) {
				core.CBot.getInstance().removeFromThreadFinishing(this.simulationThread);
			}

			this.simulator.setActionTypes(actionTypes);
			TypeWrapper workerType = TypeWrapper.generateFrom(workerUnitType);

			// Fill the HashMaps with the current information regarding the
			// Units etc.
			HashMap<TypeWrapper, Integer> simulationTypesFree = new HashMap<>();
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationTypesWorking = new HashMap<>();

			extractFreeAndWorkingTypeWrappers(units, simulationTypesFree, simulationTypesWorking,
					currentFrameTimeStamp);

			// TODO: Possible Change: Setter instead of creating new instances.
			// Start a new Thread with the provided information.
			this.simulationThread = new SimulatorThread(this.simulator, this.generatedActionTypeSequences, workerType,
					simulationTypesFree, simulationTypesWorking, currentMinerals, currentGas, currentFrameTimeStamp);
			this.simulationThread.start();

			// The Thread got started and no errors occurred.
			success = true;

			// Add the new Thread to the List of Threads that must be waited
			// for.
			core.CBot.getInstance().addToThreadFinishing(this.simulationThread);
		}

		return success;
	}

	/**
	 * Function for splitting a List of given Units in different categories
	 * (Free and working) as well as counting the specific types.
	 * 
	 * @param units
	 *            the Units that are going to be categorized.
	 * @param simulationTypesFree
	 *            the HashMap to which all free Units are going to be added to.
	 * @param simulationTypesWorking
	 *            the HashMap to which all working Units are going to be added
	 *            to (As well as their result type and the time stamp).
	 * @param currentFrameTimeStamp
	 *            the current time stamp in frames.
	 */
	private static void extractFreeAndWorkingTypeWrappers(List<Unit> units,
			HashMap<TypeWrapper, Integer> simulationTypesFree,
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationTypesWorking,
			int currentFrameTimeStamp) {
		// Iterate through all Player Units and add the information towards the
		// HashMaps.
		for (Unit unit : units) {
			// Differentiate between building and other Units.
			if (unit.getType().isBuilding() && !unit.isBeingConstructed()) {
				if (unit.isTraining()) {
					addTypeWorking(simulationTypesWorking, unit.getType(),
							currentFrameTimeStamp + unit.getRemainingTrainTime());
				} else {
					addTypeFree(simulationTypesFree, unit.getType());
				}
			} else {
				// Differentiate between workers and other Units.
				if (unit.getType().isWorker()) {
					// TODO: Possible Change: Make non Terran specific.
					if (unit.isConstructing() && unit.getBuildUnit() != null) {
						addTypeWorking(simulationTypesWorking, unit.getType(),
								currentFrameTimeStamp + unit.getBuildUnit().getRemainingBuildTime());
					} else {
						addTypeFree(simulationTypesFree, unit.getType());
					}
				} else {
					addTypeFree(simulationTypesFree, unit.getType());
				}
			}
		}
	}

	/**
	 * Convenience function.
	 * 
	 * @param simulationUnitsFree
	 *            the Collection to which the type is going to be added to.
	 * @param unitType
	 *            the type that is going to be added to a Collection.
	 */
	private static void addTypeFree(HashMap<TypeWrapper, Integer> simulationUnitsFree, UnitType unitType) {
		addTypeFree(simulationUnitsFree, TypeWrapper.generateFrom(unitType));
	}

	/**
	 * Function for adding a type towards a HashMap of existing types. If the
	 * type is not already present, it is initialized with 1. If it is present,
	 * the curent value is increased by 1.
	 * 
	 * @param simulationUnitsFree
	 *            the Collection to which the type is going to be added to.
	 * @param typeWrapper
	 *            the type that is going to be added to a Collection.
	 */
	private static void addTypeFree(HashMap<TypeWrapper, Integer> simulationUnitsFree, TypeWrapper typeWrapper) {
		if (simulationUnitsFree.get(typeWrapper) == null) {
			simulationUnitsFree.put(typeWrapper, 1);
		} else {
			simulationUnitsFree.put(typeWrapper, simulationUnitsFree.get(typeWrapper) + 1);
		}
	}

	/**
	 * Convenience function.
	 * 
	 * @param simulationUnitsWorking
	 *            the Collection to which the type is going to be added to.
	 * @param unitType
	 *            the type that is going to be added to a Collection.
	 * @param finishingTimeStamp
	 *            the time stamp in frames at which the type finishes its work.
	 */
	private static void addTypeWorking(
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, UnitType unitType,
			int finishingTimeStamp) {
		addTypeWorking(simulationUnitsWorking, TypeWrapper.generateFrom(unitType), finishingTimeStamp);
	}

	/**
	 * Function for adding a type towards a HashMap of existing types. Each type
	 * is handled separately in its own Pair (First: Result type, Second: Time
	 * stamp of completion).
	 * 
	 * @param simulationUnitsWorking
	 *            the Collection to which the type is going to be added to.
	 * @param typeWrapper
	 *            the type that is going to be added to a Collection.
	 * @param finishingTimeStamp
	 *            the time stamp in frames at which the type finishes it's work.
	 */
	private static void addTypeWorking(
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, TypeWrapper typeWrapper,
			int finishingTimeStamp) {
		if (simulationUnitsWorking.get(typeWrapper) == null) {
			simulationUnitsWorking.put(typeWrapper, new ArrayList<Pair<TypeWrapper, Integer>>());
		}
		simulationUnitsWorking.get(typeWrapper).add(new Pair<>(typeWrapper, finishingTimeStamp));
	}

	// ------------------------------ Getter / Setter

}
