package buildingModule;

import java.util.ArrayList;
import java.util.List;

import bwapi.Color;
import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;

class WorkerUnit {
//
//	public enum Action {
//		IDLE, GATHERING_MINERALS, GATHERING_GAS, CONSTRUCTING
//	}
//
//	private static final int MAX_TIME_UPDATE_WAIT = 2;
//
//	private Unit unit;
//	private Unit createdBuilding = null;
//	private Action job = Action.IDLE;
//	private ConstructionJob constructionJob = null;
//	private Integer lastUpdateTimestamp = null;
//	private int customTimeUpdateWaitAddition = (int) Math.random() * MAX_TIME_UPDATE_WAIT;
//
//	private Base assignedBase;
//
//	public WorkerUnit(Unit unit, Base assignedBase) {
//		this.unit = unit;
//		this.assignedBase = assignedBase;
//	}
//
//	// -------------------- Functions
//
//	// Update the action of a worker
//	public void updateWorker() {
//		// Wait a certain amount before updating the actions of the worker
//		if (this.lastUpdateTimestamp == null || Core.getInstance().getGame().elapsedTime()
//				- this.lastUpdateTimestamp >= (MAX_TIME_UPDATE_WAIT + customTimeUpdateWaitAddition)) {
//			this.lastUpdateTimestamp = Core.getInstance().getGame().elapsedTime();
//
//			this.updateJob();
//			
//			// Try to execute the given action
//			switch (this.job) {
//			case CONSTRUCTING:
//					// Check if the unit is carrying something before trying to
//					// build the assigned building
//					if (!this.unit.isConstructing() && !this.unit.build(this.constructionJob.getBuilding(),
//							this.constructionJob.getTilePosition())) {
//						// Recalculate the position of the building since the
//						// building could not be build
//						TilePosition newBuildingLocation = this.getBuildLocation(this.constructionJob.getBuilding(),
//								this.constructionJob.getTilePosition());
//						if(newBuildingLocation != null) {
//							this.constructionJob.setTilePosition(newBuildingLocation);
//						} 
//					}
//				break;
//			case GATHERING_MINERALS:
//				break;
//			case GATHERING_GAS:
//				break;
//			case IDLE:
//				// Idle workers are set to mine minerals
//				this.mineMinerals(this.unit);
//				this.job = Action.GATHERING_MINERALS;
//				break;
//			}
//		}
//	}
//
//	// Update the current job of a worker
//	private void updateJob() {
//		if (this.unit.isConstructing() || this.constructionJob != null) {
//			this.job = Action.CONSTRUCTING;
//		} else if (this.unit.isGatheringMinerals()) {
//			this.job = Action.GATHERING_MINERALS;
//		} else if (this.unit.isGatheringGas()) {
//			this.job = Action.GATHERING_GAS;
//		} else {
//			this.job = Action.IDLE;
//		}
//	}
//
//	// Set unit to mine minerals
//	private void mineMinerals(Unit unit) {
//		Unit closestMineralField = null;
//
//		// Find the closest mineral field to mine from
//		for (Unit mineralField : this.assignedBase.getMineralFields()) {
//			if (mineralField.getType().isMineralField()) {
//				if (closestMineralField == null
//						|| unit.getDistance(mineralField) < unit.getDistance(closestMineralField)) {
//					closestMineralField = mineralField;
//				}
//			}
//		}
//
//		if (closestMineralField != null) {
//			unit.gather(closestMineralField, true);
//		}
//	}
//
//	// Set unit to gather gas
//	public void gatherGas(Unit refinery) {
//		this.unit.gather(refinery);
//	}
//
//	// Inform the worker, that a new building has to be build. The location is
//	// calculated at the worker
//	public void generateConstructionJob(UnitType building) {
//		TilePosition buildLocation = null;
//
//		// If the Building is a refinery then there is no special
//		// location found
//		if (building == UnitType.Terran_Refinery) {
//			Unit closestGeyser = null;
//
//			// Find the closest geyser
//			for (Unit geyser : this.assignedBase.getGasGeysers()) {
//				if (closestGeyser == null
//						|| geyser.getDistance(this.assignedBase.getTilePosition().toPosition()) < closestGeyser
//								.getDistance(this.assignedBase.getTilePosition().toPosition())) {
//					closestGeyser = geyser;
//				}
//			}
//
//			buildLocation = closestGeyser.getTilePosition();
//		} else {
//			buildLocation = this.getBuildLocation(building, this.assignedBase.getTilePosition());
//		}
//
//		// If a location is found, assign the construction job to this worker
//		if (buildLocation != null) {
//			this.setConstructionJob(new ConstructionJob(building, buildLocation));
//		} else {
//			// TODO: REMOVE System.out
//			System.out.println("---CONSTRUCTION SET: fail, build location was null---");
//		}
//	}
//
//	// Find a suitable building location around a given tile with a max range
//	private TilePosition getBuildLocation(UnitType building, TilePosition targetTilePosition) {
//		TilePosition buildLocation = null;
//		int counter = 0;
//		int counterMax = 50;
//
//		while (buildLocation == null && counter < counterMax) {
//			for (int i = targetTilePosition.getX() - counter; i <= targetTilePosition.getX() + counter
//					&& buildLocation == null; i++) {
//				for (int j = targetTilePosition.getY() - counter; j <= targetTilePosition.getY() + counter
//						&& buildLocation == null; j++) {
//					try {
//						TilePosition testPosition = new TilePosition(i, j);
//
//						// Is there space at the desired location
//						if (Core.getInstance().getGame().canBuildHere(testPosition, building)) {
//							List<TilePosition> neededTilePositions = this.findBlockedTilePositions(building,
//									testPosition);
//
//							// Test if each unit of the player is not inside the
//							// desired location
//							if (this.areUnitsBlocking(neededTilePositions, Core.getInstance().getPlayer().getUnits())) {
//								buildLocation = testPosition;
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//
//			counter++;
//		}
//		
//		// TODO: REMOVE System.out
//		if(buildLocation == null) {
//			System.out.println("---BUILDINGLOCATION: No building location found! " + building + " ---");
//		} else if(counter == counterMax) {
//			System.out.println("---BUILDINGLOCATION: Counter has reached maximum!---");
//		}
//		
//		return buildLocation;
//	}
//
//	// Find all required tilepositions plus a additional row at the bottom, if the building can train units to allow them to move.
//	private List<TilePosition> findBlockedTilePositions(UnitType unit, TilePosition targetPosition) {
//		ArrayList<TilePosition> neededTilePositions = new ArrayList<TilePosition>();
//		int bottomRowAddion = 0;
//		
//		if(unit.canProduce()) {
//			bottomRowAddion = 1;
//		}
//		
//		for (int i = 0; i < unit.tileWidth(); i++) {
//			for (int j = 0; j < unit.tileHeight() + bottomRowAddion; j++) {
//				int targetX = targetPosition.getX() + i;
//				int targetY = targetPosition.getY() + j;
//
//				neededTilePositions.add(new TilePosition(targetX, targetY));
//			}
//		}
//		return neededTilePositions;
//	}
//
//	// Test if a unit is blocking the location
//	private boolean areUnitsBlocking(List<TilePosition> desiredTilePositions, List<Unit> playerUnits) {
//		boolean spaceFree = true;
//
//		for (int i = 0; i < playerUnits.size() && spaceFree; i++) {
//			List<TilePosition> blockedTilePositions = new ArrayList<TilePosition>();
//			
//			// If the unti is a building, test all blocked tilepositions
//			if(playerUnits.get(i).getType().isBuilding()) {
//				blockedTilePositions = this.findBlockedTilePositions(playerUnits.get(i).getType(), playerUnits.get(i).getTilePosition());
//			} else {
//				blockedTilePositions.add(playerUnits.get(i).getTilePosition());
//			}
//			
//			for (TilePosition tilePosition : blockedTilePositions) {
//				if (desiredTilePositions.contains(tilePosition)) {
//					spaceFree = false;
//				}
//			}
//		}
//		return spaceFree;
//	}
//	
//	// ------------------------------ Getter / Setter
//
//	public Unit getUnit() {
//		return this.unit;
//	}
//
//	public Action getJob() {
//		return this.job;
//	}
//
//	public ConstructionJob getConstructionJob() {
//		return constructionJob;
//	}
//
//	public void setConstructionJob(ConstructionJob constructionJob) {
//		this.constructionJob = constructionJob;
//	}
//
//	public Unit getCreatedBuilding() {
//		return createdBuilding;
//	}
//
//	public void setCreatedBuilding(Unit createdBuilding) {
//		this.createdBuilding = createdBuilding;
//	}

}
