package buildingModule;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import buildingOrderModule.BuildingOrderModule;
import buildingOrderModule.DistributeBuildingOrdersEventListener;
import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;
import display.Display;
import unitControlModule.UnitControlModule;
import unitControlModule.scoutCommandManager.ScoutCommandManager;
import unitControlModule.scoutCommandManager.ScoutingFinishedEventListener;

public class BuildingModule implements CBotBWEventListener, DistributeBuildingOrdersEventListener, ScoutingFinishedEventListener {
	private static BuildingModule instance;
	private static int MAX_BUILDING_SEARCH_RADIUS = 30;

	private Game game;
	private Player player;

	private List<Base> basesPlayer = new ArrayList<Base>();
	private Queue<UnitType> buildingBuildQueue = (Queue<UnitType>) new LinkedList<UnitType>();
	private Queue<UnitType> unitBuildQueue = (Queue<UnitType>) new LinkedList<UnitType>();

	private BuildingModule() {
		this.game = Core.getInstance().getGame();
		this.player = this.game.self();

		CBotBWEventDistributor.getInstance().addListener(this);
		BuildingOrderModule.getInstance().addBuildingOrdersEventListener(this);
		UnitControlModule.getInstance().getScoutCommandManager().addScoutingFinishedEventListener(this);
	}

	// -------------------- Functions

	// Singleton function
	public static BuildingModule getInstance() {
		if (instance == null) {
			instance = new BuildingModule();

			// Generate a base object based on the starting units
			generateFirstBase(instance.basesPlayer);
		}
		return instance;
	}

	// Find the first units at the beginning of a game
	private static void generateFirstBase(List<Base> basesPlayer) {
		Core core = Core.getInstance();
		Unit firstCommandCenter = null;

		Player player = core.getPlayer();
		List<Unit> unitsPlayer = player.getUnits();
		List<Unit> firstWorkers = new ArrayList<Unit>();

		// Find the first command center and fill the worker list with the first
		// workers found
		for (Unit unit : unitsPlayer) {
			if (unit.getType().isBuilding()) {
				firstCommandCenter = unit;
			} else if (unit.getType().isWorker()) {
				firstWorkers.add(unit);
			}
		}

		// Generate a base with the workers assigned
		Base firstBase = new Base(player.getStartLocation(), firstCommandCenter, true);
		for (Unit unit : firstWorkers) {
			firstBase.addWorker(unit);
		}

		// Push the base to the list of active playerbases
		basesPlayer.add(firstBase);
	}
	
	// Find a building that is currently free and can train a unit
	private void forwardUnitBuildingQueue() {
		if (!this.unitBuildQueue.isEmpty()) {
			for (Base base : this.basesPlayer) {
				for (Unit building : base.getBuildingList()) {
					if (!this.unitBuildQueue.isEmpty()) {
						if (building != null && !building.isTraining()
								&& building.canTrain(this.unitBuildQueue.peek())) {
							base.getUnitBuildingQueue().add(this.unitBuildQueue.poll());
						}
					}
				}
			}
		}
	}

	// Show Information regarding the bases on the map
	private void showBaseInformation() {
		for (Base base : this.basesPlayer) {
			// Show workers
			for (WorkerUnit worker : base.getWorkerList()) {
				Display.showUnitTile(this.game, worker.getUnit(), new Color(100, 100, 100));
				Display.showUnitTarget(this.game, worker.getUnit(), new Color(0, 255, 0));
				BuildingModuleDisplay.showWorkerJob(this.game, worker);
			}

			// Show buildings
			for (Unit unit : base.getBuildingList()) {
				Display.showUnitTile(this.game, unit, new Color(100, 100, 100));
			}
		}
	}
	
	// Pop the next building from the building queue and add it to the queue of
	// a base
	private void forwardBuildingQueue() {
		if (!this.buildingBuildQueue.isEmpty()) {
			UnitType nextBuilding = this.buildingBuildQueue.peek();

			// Main base has to build alls the buildings execpt for refineries
			if (nextBuilding != null && nextBuilding != UnitType.Terran_Command_Center && nextBuilding != UnitType.Terran_Refinery) {
				this.basesPlayer.get(0).getBuildingQueue().add(this.buildingBuildQueue.poll());
			} 
			// A new command center is build at a free location
			else if(nextBuilding == UnitType.Terran_Command_Center) {
				this.constructNewBase();
			}
			// Refineries are build at the different bases
			else if(nextBuilding == UnitType.Terran_Refinery) {
				boolean refineryAssigned = false;
				
				for (Base base : basesPlayer) {
					if(!refineryAssigned) {
						boolean hasRefinery = false;
						
						for (Unit building : base.getBuildingList()) {
							if(building.getType() == UnitType.Terran_Refinery) {
								hasRefinery = true;
							}
						}
						
						if(!hasRefinery) {
							base.getBuildingQueue().add(this.buildingBuildQueue.poll());
							refineryAssigned = true;
						}
					}
					
				}
			}
		}
	}
	
	// Assign a unit from the main base to build a new base
	private void constructNewBase() {
		try {
			WorkerUnit freeWorker = null;
			
			// Find a free worker
			for (WorkerUnit workerUnit : this.basesPlayer.get(0).getWorkerList()) {
				if(freeWorker == null && workerUnit.getJob() == WorkerUnit.Action.GATHERING_MINERALS) {
					freeWorker = workerUnit;
				}
			}
			
			// Get the nearest baselocation from the last build base
			BaseLocation newBaseLocation = null;
			Position playerBasePosition = this.basesPlayer.get(0).getTilePosition().toPosition();
			
			for (BaseLocation baselocation : BWTA.getBaseLocations()) {
				if(newBaseLocation == null || baselocation.getDistance(playerBasePosition) < newBaseLocation.getDistance(playerBasePosition)) {
					if(this.isBaseLocationFree(baselocation)) {
						newBaseLocation = baselocation;
					}
				}
			}
			
			freeWorker.setConstructionJob(new ConstructionJob(this.buildingBuildQueue.poll(), newBaseLocation.getTilePosition()));
		} catch (Exception e) {
			System.out.println("---COMMANDCENTERCONSTRUCTION: error---");
		}
	}
	
	// Test if there are any buildings at the baselocation to find one where the bot has not yet build
	private boolean isBaseLocationFree(BaseLocation baselocation) {
		boolean locationFree = true;
		
		for(int i = 0; i < MAX_BUILDING_SEARCH_RADIUS && locationFree; i++) {
			for (int j = 0; j < MAX_BUILDING_SEARCH_RADIUS & locationFree; j++) {
				int tileposX = baselocation.getTilePosition().getX() - MAX_BUILDING_SEARCH_RADIUS / 2 + i;
				int tileposY = baselocation.getTilePosition().getY() - MAX_BUILDING_SEARCH_RADIUS / 2 + j;
				
				// Prevent out of bounds errors
				if(tileposX < 0) {
					tileposX = 0;
				}
				if(tileposY < 0) {
					tileposY = 0;
				}
				
				for (Base base : this.basesPlayer) {
					for (Unit unit : base.getBuildingList()) {
						if(unit.getTilePosition().equals(new TilePosition(tileposX, tileposY))) {
							locationFree = false;
						}
					}
				}
			}
		}
		return locationFree;
	}

	// ------------------------------ Getter / Setter

	public List<Base> getBasesPlayer() {
		return this.basesPlayer;
	}

	// -------------------- Eventlisteners

	// ------------------------------ Own CBotBWEventListener
	@Override
	public void onStart() {

	}

	@Override
	public void onFrame() {
		// Send elements from the building queues to the bases
		this.forwardBuildingQueue();
		this.forwardUnitBuildingQueue();

		// Display various information regarding the bases on the screen
		this.showBaseInformation();
	}

	@Override
	public void onUnitCreate(Unit unit) {
		
	}

	@Override
	public void onUnitComplete(Unit unit) {
		// Assign buildings and workers to the closest base they are build at
		if (!unit.getType().isNeutral()) {
			// If the new Unit is a base do not assign it to another base
			if (unit.getType() == UnitType.Terran_Command_Center) {
				if (this.basesPlayer.isEmpty()) {
					this.basesPlayer.add(new Base(unit.getTilePosition(), unit, true));
				} else {
					Base newBase = new Base(unit.getTilePosition(), unit, false);
					
					this.basesPlayer.add(newBase);
					 
					// Find the worker, who created the base and assign him to the new base. Also remove any construction jobs he had.
					for (Base base : this.basesPlayer) {
						for (WorkerUnit worker : base.getWorkerList()) {
							if(worker.getConstructionJob() != null && worker.getConstructionJob().getBuilding() == unit.getType()) {
								worker.setConstructionJob(null);
								worker.setCreatedBuilding(null);
								newBase.addWorker(worker);
							}
						}
					}
				}
			}
			// Find the closest base and assign the unit to it
			else {
				Base closestBase = null;

				for (Base base : this.basesPlayer) {
					if (closestBase == null || unit.getDistance(base.getTilePosition().toPosition()) < unit
							.getDistance(closestBase.getTilePosition().toPosition())) {
						closestBase = base;
					}
				}

				// If a base is found differentiate between buildings and
				// units
				if (closestBase != null) {
					if (unit.getType().isBuilding()) {
						closestBase.addBuilding(unit);
					} else if (unit.getType().isWorker()) {
						closestBase.addWorker(unit);
					}
				} else {
					System.out.println("---No Base for this unit found!---");
				}
			}
		}
	}

	@Override
	public void onUnitDestroy(Unit unit) {

	}

	// ------------------------------ Building orders module
	@Override
	public void onDistributeBuildingOrders(UnitType building) {
		// Add a building to the queue of buildings being build
		this.buildingBuildQueue.add(building);
	}

	@Override
	public void onDistributeUnitBuildingOrders(UnitType unit) {
		// Add the unit to the queue of units being build
		this.unitBuildQueue.add(unit);
	}

	// ------------------------------ Scout commander
	@Override
	public void onScoutingFinished(Unit unit) {
		// The main base receives the scout if it is a worker
		if(unit.getType().isWorker()) {
			this.basesPlayer.get(0).addWorker(unit);
		}
	}
}
