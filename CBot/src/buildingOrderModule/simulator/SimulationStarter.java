package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import bwapi.Pair;
import bwapi.Unit;
import bwapi.UnitType;

/**
 * SimulationStarter.java --- Class used for starting a simulation with the
 * currently available and provided information.
 * 
 * @author P H - 15.07.2017
 *
 */
public class SimulationStarter {

	private Simulator simulator = new Simulator(new HashSet<ActionType>());

	// Simulation values:
	private int simulationFrameStep = 300;
	private int simulationStepAmount = 3;
	private int simulationIdleScorePenalty = 10;
	private int simulationConsecutiveActionsBonus = 10;
	private boolean simulationAllowIdle = true;

	public SimulationStarter() {

	}

	// -------------------- Functions

	/**
	 * Function for starting a new simulation in the {@link Simulator} instance.
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
	 * @return the ArrayList that is the result of the {@link Simulator}
	 *         simulation.
	 */
	public ArrayList<ActionType> runStarter(HashSet<ActionType> actionTypes, List<Unit> units, int currentMinerals,
			int currentGas, UnitType workerUnitType, int currentFrameTimeStamp) {
		HashMap<TypeWrapper, Integer> simulationTypesFree = new HashMap<>();
		HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationTypesWorking = new HashMap<>();
		TypeWrapper workerType = TypeWrapper.generateFrom(workerUnitType);

		// Fill the HashMaps with the current information regarding the
		// Units etc.
		extractFreeAndWorkingTypeWrappers(units, simulationTypesFree, simulationTypesWorking, currentFrameTimeStamp);

		this.simulator.setActionTypes(actionTypes);

		return this.simulator.simulate(currentFrameTimeStamp, this.simulationFrameStep, this.simulationStepAmount,
				currentMinerals, currentGas, simulationTypesFree, simulationTypesWorking, workerType,
				this.simulationIdleScorePenalty, this.simulationConsecutiveActionsBonus, this.simulationAllowIdle);
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
