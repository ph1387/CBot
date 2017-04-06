package unitControlModule.stateFactories.actions.executableActions.worker;

import java.util.HashSet;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;
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

	private HashSet<TilePosition> tilePositionContenders = new HashSet<>();
	private TilePosition tempBuildingLocation;
	private HashSet<TilePosition> tempNeededTilePositions = new HashSet<>();
	private boolean executedOnce = false;
	private boolean enableResources = false;
	private Unit constructingBuilding;

	/**
	 * @param target
	 *            type: UnitType
	 */
	public ConstructBuildingAction(Object target) {
		super(target);

		this.addEffect(new GoapState(0, "constructing", true));
	}

	// -------------------- Functions

	@Override
	protected boolean performSpecificAction(IGoapUnit goapUnit) {
		boolean success = true;

		// Find the building that is being constructed.
		if (this.constructingBuilding == null) {
			for (Unit unit : PlayerUnitWorker.buildingsBeingCreated) {
				if (unit.getType() == (((ConstructionJob) this.target).getBuilding())) {
					this.constructingBuilding = unit;
					this.enableResources = true;
					break;
				}
			}

			if (this.constructingBuilding != null) {
				PlayerUnitWorker.buildingsBeingCreated.remove(this.constructingBuilding);
			}
		}

		// Execute the building command.
		if (this.actionChangeTrigger) {
			((ConstructionJob) this.target).setTilePosition(this.tempBuildingLocation);
			this.executedOnce = true;

			success = ((PlayerUnitWorker) goapUnit).getUnit().build(((ConstructionJob) this.target).getBuilding(),
					((ConstructionJob) this.target).getTilePosition());
		}
		return success;
	}

	@Override
	protected void resetSpecific() {
		try {
			// Enable reserved TilePositions again.
			tilePositionContenders.removeAll(this.tempNeededTilePositions);

			// Remove the mapping from the Unit.
			PlayerUnitWorker.mappedBuildActions.remove(((PlayerUnitWorker) this.currentlyExecutingUnit).getUnit());

			// Only reset the assigned Unit if a building was actually
			// constructed / started being constructed. This causes the
			// PlayerUnitWorker to add it to the building Queue again if the
			// building type is still assigned.
			if (this.constructingBuilding != null) {
				((PlayerUnitWorker) this.currentlyExecutingUnit).resetAssignedBuildingType();
			}
		} catch (Exception e) {
		}

		try {
			// Enable the resources again.
			if (this.enableResources) {
				this.enableResources = false;

				PlayerUnitWorker.reservedBuildingMinerals -= ((ConstructionJob) this.target).getBuilding()
						.mineralPrice();
				PlayerUnitWorker.reservedBuildingGas -= ((ConstructionJob) this.target).getBuilding().gasPrice();
			}
		} catch (Exception e) {
		}

		this.target = null;
		this.tempBuildingLocation = null;
		this.tempNeededTilePositions = null;
		this.executedOnce = false;
		this.constructingBuilding = null;
	}

	@Override
	protected boolean checkProceduralPrecondition(IGoapUnit goapUnit) {
		boolean success = true;

		if (this.target != null && ((ConstructionJob) this.target).getBuilding() != null) {
			if (this.tempBuildingLocation == null) {
				ConstructionJob constructionJob = (ConstructionJob) this.target;
				TilePosition newBuildingLocation = this.generateBuildLocation(constructionJob.getBuilding(),
						constructionJob.getTilePosition(), goapUnit);

				// A building location has to be found first to execute this
				// action.
				if (newBuildingLocation != null) {
					this.tempBuildingLocation = newBuildingLocation;
				} else {
					// Causes the Unit to remove the building type from its
					// assigned property and insert it into the Queue again.
					PlayerUnitWorker.mappedBuildActions.remove(((PlayerUnitWorker) goapUnit).getUnit());
					success = false;
				}
			}
		}
		return this.target != null && ((ConstructionJob) this.target).getBuilding() != null && success;
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
			// TODO: Optimize!
			// Generate new TilePositions around a specific target.
			for (int i = targetTilePosition.getX() - counter; i <= targetTilePosition.getX() + counter
					&& buildLocation == null; i++) {
				for (int j = targetTilePosition.getY() - counter; j <= targetTilePosition.getY() + counter
						&& buildLocation == null; j++) {
					TilePosition testPosition = new TilePosition(i, j);
					HashSet<TilePosition> neededTilePositions = this.generateNeededTilePositions(building,
							testPosition);

					// If the space is free, try changing the building's
					// location.
					if (Core.getInstance().getGame().canBuildHere(testPosition, building)
							&& !this.arePlayerUnitsBlocking(neededTilePositions, goapUnit)) {
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
	 *            the TilePositions that are going to be checked with all Player
	 *            Units.
	 * @param constructor
	 *            the IGoapUnit that is going to be building at the
	 *            TilePosition. Needed to exclude the constructor from the
	 *            blocking Units.
	 * @return true or false depending if a Player Unit is blocking the desired
	 *         TilePosition / a desired TilePosition.
	 */
	private boolean arePlayerUnitsBlocking(HashSet<TilePosition> desiredTilePositions, IGoapUnit constructor) {
		// Check each player Unit but the constructor itself
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
				// and the contended ones.
				for (TilePosition tilePosition : blockedTilePositions) {
					if (desiredTilePositions.contains(tilePosition) || tilePositionContenders.contains(tilePosition)) {
						return true;
					}
				}
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
		// 1 since it should not be randomly added to other action Queues.
		return 1;
	}

	@Override
	protected float generateCostRelativeToTarget(IGoapUnit goapUnit) {
		return 0;
	}

	@Override
	protected boolean isDone(IGoapUnit goapUnit) {
		boolean success = this.executedOnce && !((PlayerUnit) goapUnit).getUnit().isConstructing();

		if (success) {
			this.enableResources = true;
		}

		return success;
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
