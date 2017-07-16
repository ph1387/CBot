package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import bwapi.Pair;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD
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

	// TODO: UML ADD JAVADOC
	public boolean isRunning() {
		return this.simulationThread != null && this.simulationThread.isAlive();
	}

	// TODO: UML ADD JAVADOC
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

	// TODO: UML ADD JAVADOC
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

	// TODO: UML ADD JAVADOC
	private static void addTypeFree(HashMap<TypeWrapper, Integer> simulationUnitsFree, UnitType unitType) {
		addTypeFree(simulationUnitsFree, TypeWrapper.generateFrom(unitType));
	}

	// TODO: UML ADD JAVADOC
	private static void addTypeFree(HashMap<TypeWrapper, Integer> simulationUnitsFree, UpgradeType upgradeType) {
		addTypeFree(simulationUnitsFree, TypeWrapper.generateFrom(upgradeType));
	}

	// TODO: UML ADD JAVADOC
	private static void addTypeFree(HashMap<TypeWrapper, Integer> simulationUnitsFree, TechType techType) {
		addTypeFree(simulationUnitsFree, TypeWrapper.generateFrom(techType));
	}

	// TODO: UML ADD JAVADOC
	private static void addTypeFree(HashMap<TypeWrapper, Integer> simulationUnitsFree, TypeWrapper typeWrapper) {
		if (simulationUnitsFree.get(typeWrapper) == null) {
			simulationUnitsFree.put(typeWrapper, 1);
		} else {
			simulationUnitsFree.put(typeWrapper, simulationUnitsFree.get(typeWrapper) + 1);
		}
	}

	// TODO: UML ADD JAVADOC
	private static void addTypeWorking(
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, UnitType unitType,
			int finishingTimeStamp) {
		addTypeWorking(simulationUnitsWorking, TypeWrapper.generateFrom(unitType), finishingTimeStamp);
	}

	// TODO: UML ADD JAVADOC
	private static void addTypeWorking(
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, UpgradeType upgradeType,
			int finishingTimeStamp) {
		addTypeWorking(simulationUnitsWorking, TypeWrapper.generateFrom(upgradeType), finishingTimeStamp);
	}

	// TODO: UML ADD JAVADOC
	private static void addTypeWorking(
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, TechType techType,
			int finishingTimeStamp) {
		addTypeWorking(simulationUnitsWorking, TypeWrapper.generateFrom(techType), finishingTimeStamp);
	}

	// TODO: UML ADD JAVADOC
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
