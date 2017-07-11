package buildingOrderModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManagerFactory;
import buildingOrderModule.simulator.ActionType;
import buildingOrderModule.simulator.Node;
import buildingOrderModule.simulator.Simulator;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Barracks;
import buildingOrderModule.stateFactories.actions.executableActions.ConstrucActionTerran_Factory;
import buildingOrderModule.stateFactories.actions.executableActions.ConstructActionCenter;
import buildingOrderModule.stateFactories.actions.executableActions.ManagerBaseAction;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_Marine;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionTerran_SiegeTank;
import buildingOrderModule.stateFactories.actions.executableActions.TrainUnitActionWorker;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueDefault;
import bwapi.*;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.DefaultGoapAgent;
import javaGOAP.GoapAction;
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
	
	
	
	
	// TODO: WIP
	// TODO: UML ADD
	private Simulator simulator;
	// Simulation frequency:
	private Integer lastSimulationTimeStampFrames = null;
	private int nextSimulationTimeStampDifferenceFrames = 1000;
	// Simulation values:
	private int simulationFrameStep = 300;
	private int simulationStepAmount = 5;
	private UnitType simulationWorkerType = Core.getInstance().getPlayer().getRace().getWorker();
	private int simulationIdleScorePenalty = 10;
	private int simulationConsecutiveActionsBonus = 10;
	private boolean simulationAllowIdle = true;
	private HashMap<UnitType, Integer> simulationUnitsFree;
	private HashMap<UnitType, ArrayList<Pair<UnitType, Integer>>> simulationUnitsWorking;
	
	
	
	
	
	

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
			if(this.lastSimulationTimeStampFrames == null || Core.getInstance().getGame().getFrameCount() >= this.lastSimulationTimeStampFrames + this.nextSimulationTimeStampDifferenceFrames) {
				int currentMinerals = Core.getInstance().getPlayer().minerals();
				int currentGas = Core.getInstance().getPlayer().gas();
				int currentFrameTimeStamp = Core.getInstance().getGame().getFrameCount();
				this.lastSimulationTimeStampFrames = currentFrameTimeStamp;
				
				// Fill the HashMaps with the current information regarding the Units.
				this.simulationUnitsFree = new HashMap<>();
				this.simulationUnitsWorking = new HashMap<>();
				
				
				long start = System.nanoTime();
				
				
				// Iterate through all Player Units and add the information towards the HashMaps.
				for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
					// Differentiate between building and other Units.
					if(unit.getType().isBuilding() && !unit.isBeingConstructed() && unit.getType() != Core.getInstance().getPlayer().getRace().getRefinery()) {
						if(unit.isTraining()) {
							this.addUnitWorking(unit.getType(), currentFrameTimeStamp + unit.getRemainingTrainTime());
						} else {
							this.addUnitFree(unit.getType());
						}
					} else {
						// Differentiate between workers and other Units.
						if(unit.getType().isWorker()) {
							// TODO: Possible Change: Make non Terran specific.
							if(unit.isConstructing() && unit.getBuildUnit() != null) {
								this.addUnitWorking(unit.getType(), currentFrameTimeStamp + unit.getBuildUnit().getRemainingBuildTime());
							} else {
								this.addUnitFree(unit.getType());
							}
						} else {
							this.addUnitFree(unit.getType());
						}
					}
				}
				
				
				// TODO: WIP
				ArrayList<ActionType> actions = this.simulator.simulate(currentFrameTimeStamp, this.simulationFrameStep, this.simulationStepAmount, currentMinerals, currentGas, this.simulationUnitsFree, this.simulationUnitsWorking, this.simulationWorkerType, this.simulationIdleScorePenalty, this.simulationConsecutiveActionsBonus, this.simulationAllowIdle);
				
				// TODO: WIP REMOVE
				System.out.println("Time taken: " + ((double) (System.nanoTime() - start) / 1000000) + "ms");
			}
			
			
			
			
			
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
	private void addUnitFree(UnitType unitType) {
		if(this.simulationUnitsFree.get(unitType) == null) {
			this.simulationUnitsFree.put(unitType, 1);
		} else {
			this.simulationUnitsFree.put(unitType, this.simulationUnitsFree.get(unitType) + 1);
		}
	}
	
	private void addUnitWorking(UnitType unitType, int finishingTimeStamp) {
		if(this.simulationUnitsWorking.get(unitType) == null) {
			this.simulationUnitsWorking.put(unitType, new ArrayList<Pair<UnitType, Integer>>());
		}
		this.simulationUnitsWorking.get(unitType).add(new Pair<>(unitType, finishingTimeStamp));
	}
}
