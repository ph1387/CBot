package unitControlModule.stateFactories.actions.executableActions.worker;

import java.util.HashSet;

import bwapi.Color;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
import core.Display;
import javaGOAP.GoapState;
import javaGOAP.IGoapUnit;
import unitControlModule.stateFactories.actions.executableActions.BaseAction;
import unitControlModule.unitWrappers.PlayerUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

/**
 * ConstructBuildingAction.java --- Constructing action for a PlayerUnitWorker
 * to construct a building.
 * 
 * @author P H - 02.04.2017
 *
 */
public class ConstructBuildingAction extends BaseAction {

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

	private BuildLocationFactory buildLocationFactory;

	/**
	 * @param target
	 *            type: ConstructionJob
	 */
	public ConstructBuildingAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "constructing", true));

		this.buildLocationFactory = new BuildLocationFactory();
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// TODO: REMOVE DEBUG
		try {
			TilePositionContenderFactory.debug_polygon.drawOnMap(new Color(255, 255, 0), 3, true);
			for (TilePosition tile : TilePositionContenderFactory.debug_polygon.getCoveredTilePositions()) {
				Display.drawTileFilled(Core.getInstance().getGame(), tile.getX(), tile.getY(), 1, 1,
						new Color(255, 255, 0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (this.tempBuildingLocationPrev != this.tempBuildingLocation) {
			this.tempBuildingLocationPrev = this.tempBuildingLocation;
			this.triedConstructingOnce = true;

			((PlayerUnitWorker) goapUnit).getUnit().build(((ConstructionJob) this.target).getBuilding(),
					((ConstructionJob) this.target).getTilePosition());
			PlayerUnitWorker.mappedBuildActions.put(((PlayerUnitWorker) goapUnit).getUnit(),
					((ConstructionJob) this.target).getBuilding());
		}

		return success;
	}

	@Override
	protected void resetSpecific() {
		try {
			// Enable reserved TilePositions again.
			this.buildLocationFactory.getTilePositionContenders().removeAll(this.tempNeededTilePositions);

			// Remove the mapping from the Unit. If the Unit did not get its
			// build flag set, set UnitType is inserted in the building Queue
			// again.
			PlayerUnitWorker.mappedBuildActions.remove(((PlayerUnitWorker) this.currentlyExecutingUnit).getUnit());
		} catch (Exception e) {
		}

		this.target = null;
		this.tempBuildingLocationPrev = null;
		this.tempBuildingLocation = null;
		this.tempNeededTilePositions = null;
		this.constructingBuilding = null;
		this.triedConstructingOnce = false;
		this.counterTries = 0;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		if (this.target != null) {
			UnitType building = ((ConstructionJob) this.target).getBuilding();
			boolean success = true;

			// Find the building that is being constructed.
			if (this.constructingBuilding == null) {
				this.constructingBuilding = this.findConstructingBuilding(goapUnit);

				if (this.constructingBuilding != null) {
					PlayerUnitWorker.buildingsBeingCreated.remove(this.constructingBuilding);
					((PlayerUnitWorker) goapUnit).setConstructingFlag(this.constructingBuilding);
				}
			}

			// Create a tempBuildingLocation if none is assigned.
			if (this.tempBuildingLocation == null) {
				TilePosition targetTilePosition = ((PlayerUnitWorker) goapUnit).getUnit().getTilePosition();
				TilePosition generatedBuildLocation = this.buildLocationFactory.generateBuildLocation(building,
						targetTilePosition, goapUnit);

				// Update all references and values related to the building
				// location!
				if (generatedBuildLocation != null) {
					this.tempBuildingLocation = generatedBuildLocation;
					this.tempNeededTilePositions = this.buildLocationFactory.generateNeededTilePositions(building,
							generatedBuildLocation);

					((ConstructionJob) this.target).setTilePosition(this.tempBuildingLocation);
				}
			}
			// Test the tempBuildingLocation for its validity
			else if (this.constructingBuilding == null) {
				TilePosition targetTilePosition = ((ConstructionJob) this.target).getTilePosition();
				boolean invalid = true;

				// TODO: Possible Change: Add maximum counter
				// Try finding a new TilePosition until a valid one is found
				while (invalid) {
					HashSet<TilePosition> neededTilePositions = this.buildLocationFactory
							.generateNeededTilePositions(building, targetTilePosition);
					invalid = this.buildLocationFactory.arePlayerUnitsBlocking(neededTilePositions, goapUnit)
							&& this.buildLocationFactory.areTilePositionsContended(neededTilePositions);

					if (invalid) {
						this.tempBuildingLocationPrev = this.tempBuildingLocation;
						this.tempBuildingLocation = this.buildLocationFactory.generateBuildLocation(building,
								targetTilePosition, goapUnit);
						((ConstructionJob) this.target).setTilePosition(this.tempBuildingLocation);
					}
				}
			}

			return success && this.tempBuildingLocation != null;
		} else {
			return false; // Target = null
		}
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

		for (Unit unit : PlayerUnitWorker.buildingsBeingCreated) {
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

		// The operation has to be tried at least for x cycles
		if (counterTries < MIN_TRIES) {
			counterTries++;
		} else {
			triesFullfilled = true;
		}

		return triesFullfilled && !((PlayerUnit) goapUnit).getUnit().isConstructing() && this.triedConstructingOnce
				&& ((PlayerUnitWorker) goapUnit)
						.getCurrentConstructionState() == PlayerUnitWorker.ConstructionState.CONFIRMED;
	}

	@Override
	protected boolean isInRange(IGoapUnit goapUnit) {
		return false;
	}

	@Override
	protected boolean requiresInRange(IGoapUnit goapUnit) {
		return false;
	}

	// ------------------------------ Getter / Setter

	public void setTempNeededTilePositions(HashSet<TilePosition> tempNeededTilePositions) {
		this.tempNeededTilePositions = tempNeededTilePositions;
	}

}
