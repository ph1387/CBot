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

	// Due to the large tile range there should not be any trouble finding a
	// suitable building location.
	private static final int MAX_TILE_RANGE = 50;
	// Safety feature for the isDone function, since the Unit could take a while
	// to start constructing after executing the command, which gets executed at
	// least the given amount of times. This effectively forces the Unit to
	// build the given building or idle until the isDone function kicks in and
	// the building gets queued again.
	private static final int MIN_TRIES = 20;

	private static HashSet<TilePosition> tilePositionContenders = TilePositionContenderFactory
			.generateDefaultContendedTilePositions();

	private TilePosition tempBuildingLocationPrev;
	private TilePosition tempBuildingLocation;
	private HashSet<TilePosition> tempNeededTilePositions = new HashSet<>();
	private Unit constructingBuilding;
	private boolean triedConstructingOnce = false;
	private int counterTries = 0;

	/**
	 * @param target
	 *            type: ConstructionJob
	 */
	public ConstructBuildingAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "constructing", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// TODO: REMOVE DEBUG
		try {
			TilePositionContenderFactory.poly.drawOnMap(new Color(255, 255, 0), 3, true);
			for (TilePosition tile : TilePositionContenderFactory.covered) {
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
			tilePositionContenders.removeAll(this.tempNeededTilePositions);

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
				TilePosition generatedBuildLocation = this.generateBuildLocation(building, targetTilePosition,
						goapUnit);

				if (generatedBuildLocation != null) {
					this.tempBuildingLocation = generatedBuildLocation;
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
					HashSet<TilePosition> neededTilePositions = this.generateNeededTilePositions(building,
							targetTilePosition);
					invalid = this.arePlayerUnitsBlocking(neededTilePositions, goapUnit)
							&& this.areTilePositionsContended(neededTilePositions);

					if (invalid) {
						this.tempBuildingLocationPrev = this.tempBuildingLocation;
						this.tempBuildingLocation = this.generateBuildLocation(building, targetTilePosition, goapUnit);
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

	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition with a max range.
	 * 
	 * @param building
	 *            the UnitType of the building that is going to be built.
	 * @param targetTilePosition
	 *            the TilePosition the new TilePosition is going to be
	 *            calculated around.
	 * @param goapUnit
	 *            the IGoapUnit that is going to be constructing the building.
	 * @return a TilePosition at which the given building can be constructed or
	 *         null, if none is found.
	 */
	private TilePosition generateBuildLocation(UnitType building, TilePosition targetTilePosition, IGoapUnit goapUnit) {
		TilePosition buildLocation = null;
		int counter = 0;

		while (buildLocation == null && counter < MAX_TILE_RANGE) {
			// Prevent out of bounds calculations
			int minWidth = Math.max(targetTilePosition.getX() - counter, 0);
			int minHeight = Math.max(targetTilePosition.getY() - counter, 0);
			int maxWidth = Math.min(targetTilePosition.getX() + counter, Core.getInstance().getGame().mapWidth());
			int maxHeight = Math.min(targetTilePosition.getY() + counter, Core.getInstance().getGame().mapHeight());

			// TODO: Possible change: Optimize!
			// Generate new TilePositions around a specific target.
			for (int i = minWidth; i <= maxWidth && buildLocation == null; i++) {
				for (int j = minHeight; j <= maxHeight && buildLocation == null; j++) {
					TilePosition testPosition = new TilePosition(i, j);
					HashSet<TilePosition> neededTilePositions = this.generateNeededTilePositions(building,
							testPosition);

					// If the space is free, try changing the building's
					// location.
					if (Core.getInstance().getGame().canBuildHere(testPosition, building)
							&& !this.arePlayerUnitsBlocking(neededTilePositions, goapUnit)
							&& !this.areTilePositionsContended(neededTilePositions)) {
						buildLocation = testPosition;

						// Remove old contended entries and add new ones
						tilePositionContenders
								.removeAll(this.generateNeededTilePositions(building, targetTilePosition));
						tilePositionContenders.addAll(neededTilePositions);
						this.tempNeededTilePositions = neededTilePositions;
					}
				}
			}

			counter++;
		}
		return buildLocation;
	}

	/**
	 * Function for testing if a Player's Unit is blocking the desired
	 * TilePosition.
	 * 
	 * @param desiredTilePositions
	 *            the TilePositions that are going to be checked against all
	 *            Player Units.
	 * @param constructor
	 *            the IGoapUnit that is going to be building at the
	 *            TilePosition. Needed to exclude the constructor from the
	 *            blocking Units.
	 * @return true or false depending if a Player Unit is blocking the desired
	 *         TilePosition / a desired TilePosition.
	 */
	private boolean arePlayerUnitsBlocking(HashSet<TilePosition> desiredTilePositions, IGoapUnit constructor) {
		// Check each player Unit except the constructor itself
		for (Unit unit : Core.getInstance().getPlayer().getUnits()) {
			if (unit != ((PlayerUnitWorker) constructor).getUnit()) {
				HashSet<TilePosition> blockedTilePositions = new HashSet<TilePosition>();

				if (unit.getType().isBuilding()) {
					blockedTilePositions = this.generateNeededTilePositions(unit.getType(), unit.getTilePosition());
				} else {
					blockedTilePositions.add(unit.getTilePosition());
				}

				// Check the occupied TilePosition(s) of the currently tested
				// Unit
				for (TilePosition tilePosition : blockedTilePositions) {
					if (desiredTilePositions.contains(tilePosition)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Function for testing if one of the desired TilePositions is already
	 * contended.
	 * 
	 * @param desiredTilePositions
	 *            the TilePositions that are going to be checked against all
	 *            contended TilePositions.
	 * @return true or false depending if one of the desired TilePositions is
	 *         already contended.
	 */
	private boolean areTilePositionsContended(HashSet<TilePosition> desiredTilePositions) {
		for (TilePosition tilePosition : desiredTilePositions) {
			if (tilePositionContenders.contains(tilePosition)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Function for finding all required TilePositions of a building plus a
	 * additional row at the bottom, if the building can train Units.
	 * 
	 * @param unitType
	 *            the UnitType whose TilePositions are going to be calculated.
	 * @param targetTilePosition
	 *            the TilePosition the Unit is going to be constructed /
	 *            targeted at.
	 * @return a HashSet containing all TilePositions that the constructed Unit
	 *         would have if it was constructed at the targetTilePosition.
	 */
	private HashSet<TilePosition> generateNeededTilePositions(UnitType unitType, TilePosition targetTilePosition) {
		HashSet<TilePosition> neededTilePositions = new HashSet<TilePosition>();
		int bottomRowAddion = 0;

		if (unitType.canProduce()) {
			bottomRowAddion = 1;
		}

		for (int i = 0; i < unitType.tileWidth(); i++) {
			for (int j = 0; j < unitType.tileHeight() + bottomRowAddion; j++) {
				int targetX = targetTilePosition.getX() + i;
				int targetY = targetTilePosition.getY() + j;

				neededTilePositions.add(new TilePosition(targetX, targetY));
			}
		}
		return neededTilePositions;
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
}
