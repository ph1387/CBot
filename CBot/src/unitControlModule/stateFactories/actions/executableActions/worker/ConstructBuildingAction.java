package unitControlModule.stateFactories.actions.executableActions.worker;

import java.util.HashSet;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

/**
 * ConstructBuildingAction.java --- Constructing action for a PlayerUnitWorker
 * to construct a building.
 * 
 * @author P H - 02.04.2017
 *
 */
public class ConstructBuildingAction extends WorkerAction {

	// Safety feature for the isDone function, since the Unit could take a while
	// to start constructing after executing the command, which gets executed at
	// least the given amount of times. This effectively forces the Unit to
	// build the given building or idle until the isDone function kicks in and
	// the building gets queued again.
	private static final int MIN_TRIES = 20;

	private TilePosition tempBuildingLocationPrev;
	private TilePosition tempBuildingLocation;
	private HashSet<TilePosition> tempNeededTilePositions = new HashSet<>();
	private Unit constructingBuilding;
	private boolean triedConstructingOnce = false;
	private int counterTries = 0;

	// TODO: UML ADD
	private boolean isMovingToConstructionSite = false;

	private BuildLocationFactory buildLocationFactory;

	/**
	 * @param target
	 *            type: ConstructionJob
	 */
	public ConstructBuildingAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "constructing", true));
		this.addPrecondition(new GoapState(0, "canMove", true));
		this.addPrecondition(new GoapState(0, "isCarryingMinerals", false));
		this.addPrecondition(new GoapState(0, "isCarryingGas", false));

		this.buildLocationFactory = new BuildLocationFactory();
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		UnitType building = ((ConstructionJob) this.target).getBuilding();
		boolean success = true;

		// TODO: Possible Change: Split in own functions.
		// Find the building that is being constructed.
		if (this.constructingBuilding == null) {
			this.constructingBuilding = this.findConstructingBuilding(goapUnit);

			if (this.constructingBuilding != null) {
				// Mark stuff in the worker instance
				((PlayerUnitWorker) goapUnit).getInformationStorage().getWorkerConfig().getBuildingsBeingCreated()
						.remove(this.constructingBuilding);
				((PlayerUnitWorker) goapUnit).setConstructingFlag(this.constructingBuilding);

				// Remove all previously contended spots that might be left in
				// the HashMap to prevent any miss matches in it.
				((PlayerUnitWorker) goapUnit).getInformationStorage().getMapInfo().getTilePositionContenders()
						.removeAll(this.tempNeededTilePositions);

				// Set values in this instance and contend the construction
				// spot.
				this.tempBuildingLocation = this.constructingBuilding.getTilePosition();
				this.tempNeededTilePositions = this.buildLocationFactory.generateNeededTilePositions(building,
						this.constructingBuilding.getTilePosition());
				((PlayerUnitWorker) goapUnit).getInformationStorage().getMapInfo().getTilePositionContenders()
						.addAll(this.tempNeededTilePositions);
				((ConstructionJob) this.target).setTilePosition(this.constructingBuilding.getTilePosition());
			}
		}

		// TODO: Possible Change: Split in own functions.
		// Create a tempBuildingLocation if none is assigned.
		if (this.tempBuildingLocation == null) {
			TilePosition targetTilePosition = ((PlayerUnitWorker) goapUnit).getUnit().getTilePosition();

			// Force the generation of a valid build location.
			while (this.tempBuildingLocation == null) {
				this.tempBuildingLocation = this.buildLocationFactory.generateBuildLocation(building,
						targetTilePosition, goapUnit);
				((ConstructionJob) this.target).setTilePosition(this.tempBuildingLocation);
			}

			// Reserve the newly found TilePositions.
			this.tempNeededTilePositions = this.buildLocationFactory.generateNeededTilePositions(building,
					this.tempBuildingLocation);
			((PlayerUnitWorker) goapUnit).getInformationStorage().getMapInfo().getTilePositionContenders()
					.addAll(this.tempNeededTilePositions);
		}
		// Only validate the build location if the constructed building was not
		// yet found!
		// -> Ignore any refineries, since they require special treatment and
		// can only be placed in certain spots.
		else if (this.constructingBuilding == null && ((ConstructionJob) this.target).getBuilding() != Core
				.getInstance().getPlayer().getRace().getRefinery()) {
			boolean invalid = true;

			// TODO: Possible Change: Add maximum counter.
			// Try generating a new TilePosition until a valid one is found.
			while (invalid) {
				invalid = this.buildLocationFactory.arePlayerUnitsBlocking(this.tempNeededTilePositions, goapUnit);

				if (invalid) {
					// Mark the action to be performed.
					this.tempBuildingLocationPrev = this.tempBuildingLocation;

					// Remove old contended entries.
					((PlayerUnitWorker) goapUnit).getInformationStorage().getMapInfo().getTilePositionContenders()
							.removeAll(this.tempNeededTilePositions);

					// Find a new build location.
					this.tempBuildingLocation = this.buildLocationFactory.generateBuildLocation(building,
							this.tempBuildingLocation, goapUnit);
					((ConstructionJob) this.target).setTilePosition(this.tempBuildingLocation);

					// Reserve the newly found TilePositions
					this.tempNeededTilePositions = this.buildLocationFactory.generateNeededTilePositions(building,
							this.tempBuildingLocation);
					((PlayerUnitWorker) goapUnit).getInformationStorage().getMapInfo().getTilePositionContenders()
							.addAll(this.tempNeededTilePositions);
				}
			}
		}

		// If the construction site is not already explored, move towards it.
		if (!Core.getInstance().getGame().isExplored(((ConstructionJob) this.target).getTilePosition())) {
			((PlayerUnitWorker) goapUnit).getUnit()
					.move(((ConstructionJob) this.target).getTilePosition().toPosition());

			this.isMovingToConstructionSite = true;
		} else {
			this.isMovingToConstructionSite = false;

			// Only perform a construction action if the temporary build
			// location
			// changed.
			if (this.tempBuildingLocationPrev != this.tempBuildingLocation) {
				this.tempBuildingLocationPrev = this.tempBuildingLocation;
				this.triedConstructingOnce = true;

				((PlayerUnitWorker) goapUnit).getUnit().build(((ConstructionJob) this.target).getBuilding(),
						((ConstructionJob) this.target).getTilePosition());
				((PlayerUnitWorker) goapUnit).getInformationStorage().getWorkerConfig().getMappedBuildActions()
						.put(((PlayerUnitWorker) goapUnit).getUnit(), ((ConstructionJob) this.target).getBuilding());
			}
		}

		return success;
	}

	@Override
	protected void resetSpecific() {
		try {
			// Enable reserved TilePositions again.
			((PlayerUnitWorker) this.currentlyExecutingUnit).getInformationStorage().getMapInfo()
					.getTilePositionContenders().removeAll(this.tempNeededTilePositions);

			// Remove the mapping from the Unit. If the Unit did not get its
			// build flag set, set UnitType is inserted in the building Queue
			// again.
			((PlayerUnitWorker) this.currentlyExecutingUnit).getInformationStorage().getWorkerConfig()
					.getMappedBuildActions().remove(((PlayerUnitWorker) this.currentlyExecutingUnit).getUnit());
		} catch (Exception e) {
		}

		this.target = null;
		this.tempBuildingLocationPrev = null;
		this.tempBuildingLocation = null;
		this.tempNeededTilePositions = null;
		this.constructingBuilding = null;
		this.triedConstructingOnce = false;
		this.counterTries = 0;

		this.isMovingToConstructionSite = false;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		return this.target != null;
	}

	/**
	 * Function for finding a building that is constructed at the moment that
	 * has the same UnitType as the assigned ConstructionJob one.
	 * 
	 * @param goapUnit
	 *            the unit that
	 * @return
	 */
	private Unit findConstructingBuilding(IGoapUnit goapUnit) {
		Unit constructingBuilding = null;

		for (Unit unit : ((PlayerUnitWorker) goapUnit).getInformationStorage().getWorkerConfig()
				.getBuildingsBeingCreated()) {
			if (unit.getType() == (((ConstructionJob) this.target).getBuilding())) {
				constructingBuilding = unit;
				break;
			}
		}
		return constructingBuilding;
	}

	@Override
	protected float generateBaseCost(IGoapUnit goapUnit) {
		// 100 since it should not be randomly added to other action Queues.
		return 100;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean triesFullfilled = false;

		if (!((PlayerUnit) goapUnit).getUnit().isConstructing() && !this.isMovingToConstructionSite) {
			// The operation has to be tried at least for x cycles
			if (this.counterTries < MIN_TRIES) {
				this.counterTries++;
			} else {
				triesFullfilled = true;
			}
		}

		return triesFullfilled || (!((PlayerUnit) goapUnit).getUnit().isConstructing() && this.triedConstructingOnce
				&& ((PlayerUnitWorker) goapUnit)
						.getCurrentConstructionState() == PlayerUnitWorker.ConstructionState.CONFIRMED);
	}

	// ------------------------------ Getter / Setter

	public void setTempNeededTilePositions(HashSet<TilePosition> tempNeededTilePositions) {
		this.tempNeededTilePositions = tempNeededTilePositions;
	}

}
