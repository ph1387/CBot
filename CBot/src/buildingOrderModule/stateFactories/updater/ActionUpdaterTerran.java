package buildingOrderModule.stateFactories.updater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.Simulator;
import buildingOrderModule.simulator.SimulatorThread;
import buildingOrderModule.simulator.TypeWrapper;
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
import bwapi.Pair;
import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;
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

	private Simulator simulator;
	// Simulation frequency:
	private Integer lastSimulationTimeStampFrames = null;
	private int nextSimulationTimeStampDifferenceFrames = 1000;

	// Multithreading for the building order simulation.
	private ConcurrentLinkedQueue<ArrayList<ActionType>> generatedActionTypeSequences = new ConcurrentLinkedQueue<>();
	private Thread simulationThread;
	
	public ActionUpdaterTerran(BuildActionManager buildActionManager) {
		super(buildActionManager);
		
		
		
		
		
		
		
		
		
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
			ActionType constructBarracks = new ConstrucActionTerran_Barracks(1);
			ActionType constructFactory = new ConstrucActionTerran_Factory(1);
			ActionType constructCenter = new ConstructActionCenter(1);
			ActionType trainWorker = new TrainUnitActionWorker(1);
			ActionType trainMarine = new TrainUnitActionTerran_Marine(1);
			ActionType trainSiegeTank = new TrainUnitActionTerran_SiegeTank(1);
			
			ActionType constructMachineShop = new BuildAddonTerran_MachineShop(1);
			
			ActionType researchSiegeMode = new ResearchActionTerran_SiegeMode(1);
			
			actionTypes.add(constructBarracks);
			actionTypes.add(constructFactory);
			actionTypes.add(constructCenter);
			actionTypes.add(trainWorker);
			actionTypes.add(trainMarine);
			actionTypes.add(trainSiegeTank);
			
			actionTypes.add(constructMachineShop);
			
			actionTypes.add(researchSiegeMode);
			
			this.simulator = new Simulator(actionTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// -------------------- Functions

	@Override
	public void update(BuildActionManager manager) {
		// React on any results of the simulation Thread and update it if necessary.
		if(!this.generatedActionTypeSequences.isEmpty()) {
			this.actOnSimulationThreadResult();
		}
		this.updateSimulationThread();
	}
	
	// TODO: UML ADD JAVADOC
	private void actOnSimulationThreadResult() {
		ActionQueueSimulationResults simulationAction = (ActionQueueSimulationResults) this.getActionFromInstance(ActionQueueSimulationResults.class);
		ArrayList<ActionType> generatedResult = this.generatedActionTypeSequences.poll();
		ArrayList<ManagerBaseAction> transformedResult = new ArrayList<>();
		
		// Transform the ActionTypes back into ManagerBaseActions.
		for (ActionType actionType : generatedResult) {
			transformedResult.add((ManagerBaseAction) actionType);
		}
		
		// Forward the transformed ActionTypes towards the Action.
		simulationAction.setActionQueue(transformedResult);
		
		
		
		
		
		
		// TODO: WIP REMOVE
//		SIMULATOR CONNECTION AUSLAGERN ZUM BERECHNEN DER SCORES -> NUR DIREKT BEVOR SIMULATOR ANGEWORFEN WIRD -> SYNCHRONIZED
		
		
		
		
		
		
		
		
		// TODO: WIP
		System.out.println("\nACT ON RESULT:");
		for (ActionType actionType : generatedResult) {
			System.out.println("  - " + actionType.getClass().getSimpleName());
		}
		System.out.println("\n");
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
