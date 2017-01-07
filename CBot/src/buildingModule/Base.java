package buildingModule;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bwapi.*;
import bwta.BWTA;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;
import cBotBWEventDistributor.CBotBWEventDistributor;
import display.Display;
import unitControlModule.SeperateUnitEventListener;
import unitControlModule.UnitControlModule;

class Base implements CBotBWEventListener, SeperateUnitEventListener {
	protected static boolean mineralsBlocked = false;
	
	private TilePosition tilePosition;
	private Player player;
	private Game game;
	private boolean isMainBase;

	private Queue<UnitType> buildingBuildQueue = (Queue<UnitType>) new LinkedList<UnitType>();
	private List<UnitType> buildingAddonBuildList = new ArrayList<UnitType>();
	private Queue<UnitType> unitBuildQueue = (Queue<UnitType>) new LinkedList<UnitType>();
	private List<WorkerUnit> workerList;
	private List<Unit> buildingList;
	private List<Unit> mineralFields;
	private List<Unit> gasGeysers;

	private static final int WORKER_BASE_MINERAL_COUNT = 20;
	private static final int WORKER_BASE_GAS_COUNT = 3;
	private static final int WORKER_BASE_MAX_COUNT = WORKER_BASE_MINERAL_COUNT + WORKER_BASE_GAS_COUNT;
	private static final int WORKER_MIN_BEFORE_GATHERING_GAS = 5;

	public Base(TilePosition location, Unit base, boolean isMainBase) {
		this.tilePosition = location;
		this.player = Core.getInstance().getPlayer();
		this.game = Core.getInstance().getGame();
		this.isMainBase = isMainBase;

		this.workerList = new ArrayList<WorkerUnit>();
		this.buildingList = new ArrayList<Unit>();
		this.mineralFields = game.getMinerals();
		this.gasGeysers = game.getGeysers();

		this.buildingList.add(base);

		CBotBWEventDistributor.getInstance().addListener(this);
		UnitControlModule.getInstance().addSeperateUnitEventListener(this);
	}

	// -------------------- Functions

	// Gets called every frame to update actions
	public void update() {
		try {
			// Update accessible mineral fields and geysirs
			this.mineralFields = this.game.getMinerals();
			this.gasGeysers = this.game.getGeysers();

			// Find the refinery of the base
			Unit refinery = null;
			for (Unit building : this.buildingList) {
				// Gathering minerals is the only acceptable default action
				if (building.getType() == UnitType.Terran_Refinery) {
					refinery = building;
				}
			}

			// If there is a refinery, check if it is used correctly
			if (refinery != null && this.workerList.size() >= WORKER_MIN_BEFORE_GATHERING_GAS) {
				// Set units gathering gas if needed
				this.updateRefineries(refinery);
			}

			// Update workers and buildings
			this.updateWorkers();
			this.updateBuildings();
		} catch (Exception e) {
			System.out.println("---BASE: update failed---");
		}
	}

	// Set actions of all workers accordingly
	private void updateWorkers() {
		for (WorkerUnit worker : this.workerList) {
			worker.updateWorker();

			if (!this.buildingBuildQueue.isEmpty()) {
				// Set a worker to work on the building currently in the queue
				this.workOnBuildingQueue(worker);
			}
		}
	}

	// Set actions of all buildings accordingly
	private void updateBuildings() {
		if(!this.mineralsBlocked) {
			for (Unit building : buildingList) {
				// Train workers if the command center is currently free and workers
				// are needed
				if (this.buildingBuildQueue.isEmpty()
						&& building.getType() == UnitType.Terran_Command_Center
						&& this.workerList.size() < WORKER_BASE_MAX_COUNT && !building.isTraining()
						&& this.player.minerals() >= UnitType.Terran_SCV.mineralPrice()) {
					building.train(UnitType.Terran_SCV);
				}

				// Train a unit, that is currently in the unit building queue
				if (!this.unitBuildQueue.isEmpty() && !building.isTraining()
						&& building.canTrain(this.unitBuildQueue.peek())) {
					UnitType unitToTrain = this.unitBuildQueue.poll();
					building.train(unitToTrain);
				}

				// Try to build a addon to the current building if possible
				if (this.buildingBuildQueue.isEmpty() && building.canBuildAddon()) {
					boolean addonPossible = true;

					// Try to find a suitable addon for the building in the addon
					// list
					for (int i = 0; i < this.buildingAddonBuildList.size() && addonPossible; i++) {
						if (building.canBuildAddon(this.buildingAddonBuildList.get(i))) {
							UnitType buildingAddon = this.buildingAddonBuildList.get(i);
							building.buildAddon(buildingAddon);
							this.buildingAddonBuildList.remove(i);

							addonPossible = false;

							System.out.println("BUILDING ADDON: " + building + " - " + buildingAddon);
						}
					}
				}
			}
		}
	}

	// Update the workers regarding the refineries
	private void updateRefineries(Unit refinery) {
		int workerGatheringGasCount = 0;

		for (WorkerUnit worker : this.workerList) {
			if (worker.getJob() == WorkerUnit.Action.GATHERING_GAS) {
				workerGatheringGasCount++;
			}
		}

		// Set a fixed amount of workers gathering gas
		for (int i = 0; i < this.workerList.size() && workerGatheringGasCount < WORKER_BASE_GAS_COUNT; i++) {
			WorkerUnit worker = this.workerList.get(i);

			if (worker.getJob() == WorkerUnit.Action.GATHERING_MINERALS) {
				worker.gatherGas(refinery);
				workerGatheringGasCount++;
			}
		}
	}

	// Work on the Building queue of the base
	private void workOnBuildingQueue(WorkerUnit worker) {
		// If there are elements in the queue, assign one construction jobs to a
		// single worker. If the next building is a addon, push it into the
		// building addon list
		if (!buildingBuildQueue.isEmpty()) {
			UnitType nextBuilding = this.buildingBuildQueue.peek();

			if (nextBuilding.isAddon()) {
				this.buildingAddonBuildList.add(this.buildingBuildQueue.poll());
			} else if (this.player.minerals() >= nextBuilding.mineralPrice()
					&& this.player.gas() >= nextBuilding.gasPrice()) {
				// Worker must be collecting minerals at the moment since this
				// is the default action
				if (worker.getJob() == WorkerUnit.Action.GATHERING_MINERALS) {
					// Block minerals if a base is being build
					if(nextBuilding == UnitType.Terran_Command_Center) {
						mineralsBlocked = true;
					}
					
					worker.generateConstructionJob(this.getBuildingQueue().poll());
				}
			}
		}
	}

	// ------------------------------ List functions

	// Add a worker to the worker list
	public void addWorker(WorkerUnit worker) {
		this.workerList.add(worker);
	}

	public void addWorker(Unit unit) {
		this.workerList.add(new WorkerUnit(unit, this));
	}

	// Remove a worker from the worker list
	public void removeWorker(WorkerUnit worker) {
		try {
			this.workerList.remove(worker);
		} catch (Exception e) {
			System.out.println("---BASE: remove worker failed---");
		}
	}

	// Add a building to the building list
	public void addBuilding(Unit building) {
		this.buildingList.add(building);
	}

	// Remove a building from the building list
	public void removeBuilding(Unit building) {
		try {
			this.buildingList.remove(building);
		} catch (Exception e) {
			System.out.println("---BASE: remove building failed---");
		}
	}

	// ------------------------------ Getter / Setter

	public Queue<UnitType> getBuildingQueue() {
		return buildingBuildQueue;
	}

	public List<Unit> getBuildingList() {
		return buildingList;
	}

	public Queue<UnitType> getUnitBuildingQueue() {
		return this.unitBuildQueue;
	}

	public List<WorkerUnit> getWorkerList() {
		return workerList;
	}

	public List<Unit> getMineralFields() {
		return this.mineralFields;
	}

	public List<Unit> getGasGeysers() {
		return this.gasGeysers;
	}

	public TilePosition getTilePosition() {
		return this.tilePosition;
	}

	// -------------------- Eventlisteners

	// ------------------------------ Seperating a unit from a base
	@Override
	public void onSeperateUnit(Unit unit) {
		for (WorkerUnit worker : this.workerList) {
			if(worker.getUnit() == unit) {
				this.workerList.remove(worker);
			}
		}
	}
	
	// ------------------------------ Own CBotBWEventListener
	@Override
	public void onUnitDestroy(Unit unit) {
		// If a unit had a assigned construction job and got destroyed order a
		// random other worker to finish it
		// -> rightclick

		// If a building got destroyed while a unit was building it, build it
		// again
		// -> not yet in building list

		// If a building got destroyed, remove and rebuild it. Especially if the
		// unit was a command center!
		// -> is in building list

		// If unit was a worker, remove and rebuild it
		// -> If unit was gathering gas, there is no need to reduce the counter
		// since it updates itself

		// HAS YET TO BE IMPLEMENTED
	}

	@Override
	public void onUnitCreate(Unit unit) {
		// Signal that the construction of a building has been started
		if (unit.getType().isBuilding()) {
			// Search the own workers for the one that is construction the
			// building. If he is in the list, the event is relevant for
			// this base.
			for (WorkerUnit worker : this.workerList) {
				if (unit == worker.getUnit().getOrderTarget()) {
					// Remove object from building queue and add a reference to
					// the worker
					this.buildingBuildQueue.remove(unit.getType());
					worker.setCreatedBuilding(unit);
				}
			}
			
			// If the unit is a command center, remove the mineral lock from all bases
			if(unit.getType() == UnitType.Terran_Command_Center) {
				mineralsBlocked = false;
			}
		}
	}

	@Override
	public void onUnitComplete(Unit unit) {
		// Signal that the construction of a building has been finished
		for (WorkerUnit worker : workerList) {
			boolean workerMissing = true;
			
			// Find the worker, which constructed the building and remove the
			// building from the units construction job.
			// -> Refinery is a "special" case...
			if (worker.getCreatedBuilding() == unit) {
				worker.setConstructionJob(null);
				worker.setCreatedBuilding(null);
				workerMissing = false;
			} else if(unit.getType() == UnitType.Terran_Refinery && worker.getConstructionJob().getBuilding() == unit.getType()) {
				worker.setConstructionJob(null);
				worker.setCreatedBuilding(null);
				workerMissing = false;
			}
			
			if(!workerMissing) {
				System.out.println("CONSTRUCTION END: " + worker.getUnit() + " - " + unit.getTilePosition() + " - "
						+ unit.getType());
			}
		}
	}

	@Override
	public void onFrame() {
		this.update();
	}

	@Override
	public void onStart() {

	}
}
