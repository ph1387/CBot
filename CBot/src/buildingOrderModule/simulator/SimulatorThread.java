package buildingOrderModule.simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import bwapi.Pair;

/**
 * SimulatorThread.java --- Own Thread for the Simulator to perform simulations.
 * This is necessary since these calculations can take a while.
 * 
 * @author P H - 11.07.2017
 *
 */
public class SimulatorThread extends Thread {

	private ConcurrentLinkedQueue<ArrayList<ActionType>> generatedActionTypeSequences;

	// Simulator itself:
	private Simulator simulator;

	// Simulation values:
	private int simulationFrameStep = 300;
	private int simulationStepAmount = 3;
	private TypeWrapper simulationWorkerType;
	private int simulationIdleScorePenalty = 10;
	private int simulationConsecutiveActionsBonus = 10;
	private boolean simulationAllowIdle = true;

	// Changing values:
	private HashMap<TypeWrapper, Integer> simulationUnitsFree;
	private HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking;
	private int currentMinerals;
	private int currentGas;
	private int currentFrameTimeStamp;

	public SimulatorThread(Simulator simulator,
			ConcurrentLinkedQueue<ArrayList<ActionType>> generatedActionTypeSequences, TypeWrapper simulationWorkerType,
			HashMap<TypeWrapper, Integer> simulationUnitsFree,
			HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, int currentMinerals,
			int currentGas, int currentFrameTimeStamp) {
		this.simulator = simulator;
		this.generatedActionTypeSequences = generatedActionTypeSequences;
		this.simulationWorkerType = simulationWorkerType;

		this.simulationUnitsFree = simulationUnitsFree;
		this.simulationUnitsWorking = simulationUnitsWorking;
		this.currentMinerals = currentMinerals;
		this.currentGas = currentGas;
		this.currentFrameTimeStamp = currentFrameTimeStamp;
	}

	// -------------------- Functions

	@Override
	public void run() {
		long start = System.nanoTime();

		ArrayList<ActionType> actions = this.simulator.simulate(this.currentFrameTimeStamp, this.simulationFrameStep,
				this.simulationStepAmount, this.currentMinerals, this.currentGas, this.simulationUnitsFree,
				this.simulationUnitsWorking, this.simulationWorkerType, this.simulationIdleScorePenalty,
				this.simulationConsecutiveActionsBonus, this.simulationAllowIdle);

		this.generatedActionTypeSequences.add(actions);

		// TODO: DEBUG INFO
		System.out.println("Time taken: " + ((double) (System.nanoTime() - start) / 1000000) + "ms");
	}

	// ------------------------------ Getter / Setter

}
