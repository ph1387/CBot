package buildingOrderModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import buildingOrderModule.buildActionManagers.BuildActionManagerFactory;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.Simulator;
import buildingOrderModule.simulator.SimulatorThread;
import buildingOrderModule.simulator.TypeWrapper;
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
			// Only allow a certain amount of elements in the building and training Queues.
			if (this.informationStorage.getConcurrentQueuedElementCount() < this.informationStorage
					.getMaxConcurrentElements()) {
				this.buildingAgent.update();
			}
			
			// React on any results of the simulation Thread and update it if necessary.
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
	
	
	
	
	
	
	
	
	// TODO: UML ADD JAVADOC
	private void actOnSimulationThreadResult() {
		
		
		// TODO: WIP
		System.out.println("\nACT ON RESULT:");
		
		ArrayList<ActionType> generatedResult = this.generatedActionTypeSequences.poll();
		
		for (ActionType actionType : generatedResult) {
			System.out.println("  - " + actionType.getClass().getSimpleName());
		}
		
		
	}
	
	// TODO: UML ADD JAVADOC
	private void updateSimulationThread() {
		// A certain time has to pass before a simulation is being started.
		if(this.lastSimulationTimeStampFrames == null || Core.getInstance().getGame().getFrameCount() >= this.lastSimulationTimeStampFrames + this.nextSimulationTimeStampDifferenceFrames) {
			// The previous SimulatorThread must have finished before a new one can be started.
			if(this.simulationThread == null || this.simulationThread.getState() == Thread.State.TERMINATED) {
				// Extract all currently relevant information.
				int currentMinerals = Core.getInstance().getPlayer().minerals();
				int currentGas = Core.getInstance().getPlayer().gas();
				TypeWrapper workerType = TypeWrapper.generateFrom(Core.getInstance().getPlayer().getRace().getWorker());
				int currentFrameTimeStamp = Core.getInstance().getGame().getFrameCount();
				this.lastSimulationTimeStampFrames = currentFrameTimeStamp;
				
				// Fill the HashMaps with the current information regarding the Units etc.
				HashMap<TypeWrapper, Integer> simulationTypesFree = new HashMap<>();
				HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationTypesWorking = new HashMap<>();
				
				extractFreeAndWorkingTypeWrappers(simulationTypesFree, simulationTypesWorking, currentFrameTimeStamp);
				
				// Start a new Thread with the provided information.
				this.simulationThread = new SimulatorThread(this.simulator, this.generatedActionTypeSequences, workerType, simulationTypesFree, simulationTypesWorking, currentMinerals, currentGas, currentFrameTimeStamp);
				this.simulationThread.start();
			}
		}
	}
	
	// TODO: UML ADD JAVADOC
	private static void extractFreeAndWorkingTypeWrappers(HashMap<TypeWrapper, Integer> simulationTypesFree, HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationTypesWorking, int currentFrameTimeStamp) {
		// Iterate through all Player Units and add the information towards the HashMaps.
		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			// Differentiate between building and other Units.
			if(unit.getType().isBuilding() && !unit.isBeingConstructed() && unit.getType() != Core.getInstance().getPlayer().getRace().getRefinery()) {
				if(unit.isTraining()) {
					addTypeWorking(simulationTypesWorking, unit.getType(), currentFrameTimeStamp + unit.getRemainingTrainTime());
				} else {
					addTypeFree(simulationTypesFree, unit.getType());
				}
			} else {
				// Differentiate between workers and other Units.
				if(unit.getType().isWorker()) {
					// TODO: Possible Change: Make non Terran specific.
					if(unit.isConstructing() && unit.getBuildUnit() != null) {
						addTypeWorking(simulationTypesWorking, unit.getType(), currentFrameTimeStamp + unit.getBuildUnit().getRemainingBuildTime());
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
		if(simulationUnitsFree.get(typeWrapper) == null) {
			simulationUnitsFree.put(typeWrapper, 1);
		} else {
			simulationUnitsFree.put(typeWrapper, simulationUnitsFree.get(typeWrapper) + 1);
		}
	}
	
	// TODO: UML ADD JAVADOC
	private static void addTypeWorking(HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, UnitType unitType, int finishingTimeStamp) {
		addTypeWorking(simulationUnitsWorking, TypeWrapper.generateFrom(unitType), finishingTimeStamp);
	}
	
	// TODO: UML ADD JAVADOC
	private static void addTypeWorking(HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, UpgradeType upgradeType, int finishingTimeStamp) {
		addTypeWorking(simulationUnitsWorking, TypeWrapper.generateFrom(upgradeType), finishingTimeStamp);
	}
	
	// TODO: UML ADD JAVADOC
	private static void addTypeWorking(HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, TechType techType, int finishingTimeStamp) {
		addTypeWorking(simulationUnitsWorking, TypeWrapper.generateFrom(techType), finishingTimeStamp);
	}
	
	// TODO: UML ADD JAVADOC
	private static void addTypeWorking(HashMap<TypeWrapper, ArrayList<Pair<TypeWrapper, Integer>>> simulationUnitsWorking, TypeWrapper typeWrapper, int finishingTimeStamp) {
		if(simulationUnitsWorking.get(typeWrapper) == null) {
			simulationUnitsWorking.put(typeWrapper, new ArrayList<Pair<TypeWrapper, Integer>>());
		}
		simulationUnitsWorking.get(typeWrapper).add(new Pair<>(typeWrapper, finishingTimeStamp));
	}
}
