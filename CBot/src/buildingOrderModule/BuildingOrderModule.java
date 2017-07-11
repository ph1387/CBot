package buildingOrderModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import buildingOrderModule.buildActionManagers.BuildActionManagerFactory;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.Simulator;
import buildingOrderModule.simulator.SimulatorThread;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SiegeTank;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;
import bwapi.*;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.DefaultGoapAgent;
import javaGOAP.GoapAgent;

/**
 * BuildingOrderModule.java --- Module for controlling the Player's building
 * actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildingOrderModule {

	private CommandSender sender = new BuildingOrderSender();
	private GoapAgent buildingAgent;

	private int supplyDepotTimeStamp = 0;
	private int supplyDepotWaitTime = 60;
	private int supplyDepotBuildTriggerPoint = 2;

	private InformationStorage informationStorage;
	
	// TODO: UML ADD
	private Simulator simulator;
	// Simulation frequency:
	// TODO: UML ADD
	private Integer lastSimulationTimeStampFrames = null;
	// TODO: UML ADD
	private int nextSimulationTimeStampDifferenceFrames = 1000;

	// Multithreading for the building order simulation.
	// TODO: UML ADD
	private ConcurrentLinkedQueue<ArrayList<ActionType>> generatedActionTypeSequences = new ConcurrentLinkedQueue<>();
	// TODO: UML ADD
	private Thread simulationThread;
	
	public BuildingOrderModule(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
		this.buildingAgent = new DefaultGoapAgent(
				BuildActionManagerFactory.createManager(sender, this.informationStorage));

		
		
		
		
		// TODO: WIP
		try {
			// Transform all available actions into ActionTypes.
			HashSet<ActionType> actionTypes = new HashSet<>();
//			for (GoapAction action : this.buildingAgent.getAssignedGoapUnit().getAvailableActions()) {
//				for (ManagerBaseAction managerBaseAction : ((ActionQueueDefault) action).getActionQueue()) {
//					try {
//						actionTypes.add((ActionType) managerBaseAction);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
			ActionType constructBarracks = new ConstrucActionTerran_Barracks(null);
			ActionType constructFactory = new ConstrucActionTerran_Factory(null);
			ActionType constructCenter = new ConstructActionCenter(null);
			ActionType trainWorker = new TrainUnitActionWorker(null);
			ActionType trainMarine = new TrainUnitActionTerran_Marine(null);
			ActionType trainSiegeTank = new TrainUnitActionTerran_SiegeTank(null);
			
			actionTypes.add(constructBarracks);
			actionTypes.add(constructFactory);
			actionTypes.add(constructCenter);
			actionTypes.add(trainWorker);
			actionTypes.add(trainMarine);
			actionTypes.add(trainSiegeTank);
			
			this.simulator = new Simulator(actionTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------- Functions

	/**
	 * Function for updating all major functionalities of this module.
	 */
	public void update() {
		this.updateSupplyTriggerPoint();
		this.buildSupplyIfNeeded();

		try {
			if (this.informationStorage.getConcurrentQueuedElementCount() < this.informationStorage
					.getMaxConcurrentElements()) {
				this.buildingAgent.update();
			}
			
			
			// TODO: WIP
			if(!this.generatedActionTypeSequences.isEmpty()) {
				this.actOnSimulationThreadResult();
			}
			this.updateSimulationThread();
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function for updating the trigger point at which new supply providers are
	 * being needed. This function ensures a dynamic construction of these.
	 */
	private void updateSupplyTriggerPoint() {
		this.supplyDepotBuildTriggerPoint = (int) Math
				.round(3. / 20. * (Core.getInstance().getPlayer().supplyTotal() / 2.) + 1. / 2.);
	}

	/**
	 * Function for constructing supply providers if the current supply is below
	 * the previously calculated trigger point. It has a time stamp as a safety
	 * feature since buildings cannot be constructed immediately.
	 */
	private void buildSupplyIfNeeded() {
		Player player = Core.getInstance().getPlayer();
		Game game = Core.getInstance().getGame();

		if ((player.supplyTotal() - player.supplyUsed()) / 2 <= this.supplyDepotBuildTriggerPoint
				&& game.elapsedTime() - this.supplyDepotTimeStamp >= this.supplyDepotWaitTime) {
			this.supplyDepotTimeStamp = game.elapsedTime();
			this.sender.buildBuilding(player.getRace().getSupplyProvider());
		}
	}
	
	
	
	
	
	
	
	
	// TODO: UML ADD
	private void actOnSimulationThreadResult() {
		
		
		// TODO: WIP
		System.out.println("\nACT ON RESULT:");
		
		ArrayList<ActionType> generatedResult = this.generatedActionTypeSequences.poll();
		
		for (ActionType actionType : generatedResult) {
			System.out.println("  - " + actionType.getClass().getSimpleName());
		}
		
		
	}
	
	// TODO: UML ADD
	private void updateSimulationThread() {
		// A certain time has to pass before a simulation is being started.
		if(this.lastSimulationTimeStampFrames == null || Core.getInstance().getGame().getFrameCount() >= this.lastSimulationTimeStampFrames + this.nextSimulationTimeStampDifferenceFrames) {
			// The previous SimulatorThread must have finished before a new one can be started.
			if(this.simulationThread == null || this.simulationThread.getState() == Thread.State.TERMINATED) {
				// Extract all currently relevant information.
				int currentMinerals = Core.getInstance().getPlayer().minerals();
				int currentGas = Core.getInstance().getPlayer().gas();
				UnitType workerType = Core.getInstance().getPlayer().getRace().getWorker();
				int currentFrameTimeStamp = Core.getInstance().getGame().getFrameCount();
				this.lastSimulationTimeStampFrames = currentFrameTimeStamp;
				
				// Fill the HashMaps with the current information regarding the Units.
				HashMap<UnitType, Integer> simulationUnitsFree = new HashMap<>();
				HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> simulationUnitsWorking = new HashMap<>();
				
				extractFreeAndWorkingUnits(simulationUnitsFree, simulationUnitsWorking, currentFrameTimeStamp);
				
				// Start a new Thread with the provided information.
				this.simulationThread = new SimulatorThread(this.simulator, this.generatedActionTypeSequences, workerType, simulationUnitsFree, simulationUnitsWorking, currentMinerals, currentGas, currentFrameTimeStamp);
				this.simulationThread.start();
			}
		}
	}
	
	// TODO: UML ADD
	private static void extractFreeAndWorkingUnits(HashMap<UnitType, Integer> simulationUnitsFree, HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> simulationUnitsWorking, int currentFrameTimeStamp) {
		// Iterate through all Player Units and add the information towards the HashMaps.
		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			// Differentiate between building and other Units.
			if(unit.getType().isBuilding() && !unit.isBeingConstructed() && unit.getType() != Core.getInstance().getPlayer().getRace().getRefinery()) {
				if(unit.isTraining()) {
					addUnitWorking(simulationUnitsWorking, unit.getType(), currentFrameTimeStamp + unit.getRemainingTrainTime());
				} else {
					addUnitFree(simulationUnitsFree, unit.getType());
				}
			} else {
				// Differentiate between workers and other Units.
				if(unit.getType().isWorker()) {
					// TODO: Possible Change: Make non Terran specific.
					if(unit.isConstructing() && unit.getBuildUnit() != null) {
						addUnitWorking(simulationUnitsWorking, unit.getType(), currentFrameTimeStamp + unit.getBuildUnit().getRemainingBuildTime());
					} else {
						addUnitFree(simulationUnitsFree, unit.getType());
					}
				} else {
					addUnitFree(simulationUnitsFree, unit.getType());
				}
			}
		}
	}
	
	// TODO: UML ADD
	private static void addUnitFree(HashMap<UnitType, Integer> simulationUnitsFree, UnitType unitType) {
		if(simulationUnitsFree.get(unitType) == null) {
			simulationUnitsFree.put(unitType, 1);
		} else {
			simulationUnitsFree.put(unitType, simulationUnitsFree.get(unitType) + 1);
		}
	}
	
	// TODO: UML ADD
	private static void addUnitWorking(HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> simulationUnitsWorking, UnitType unitType, int finishingTimeStamp) {
		if(simulationUnitsWorking.get(unitType) == null) {
			simulationUnitsWorking.put(unitType, new ArrayList<Pair<UnitType, Integer>>());
		}
		simulationUnitsWorking.get(unitType).add(new Pair<>(unitType, finishingTimeStamp));
	}
}
