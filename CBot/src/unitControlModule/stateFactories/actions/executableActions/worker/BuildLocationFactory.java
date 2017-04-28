package unitControlModule.stateFactories.actions.executableActions.worker;

import java.util.HashSet;
import java.util.List;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnitWorker;

/**
 * BuildLocationFinder.java --- Class for finding building locations on the map
 * and storing all special TilePositions.
 * 
 * @author P H - 26.04.2017
 *
 */
public class BuildLocationFactory {

	private static HashSet<TilePosition> tilePositionContenders = TilePositionContenderFactory
			.generateDefaultContendedTilePositions();

	private int maxBuildingSearchRadius = 5;
	// Due to the large tile range there should not be any trouble finding a
	// suitable building location.
	private int maxTileRange = 50;

	public BuildLocationFactory() {

	}

	// -------------------- Functions

	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition.
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
	public TilePosition generateBuildLocation(UnitType building, TilePosition targetTilePosition, IGoapUnit goapUnit) {
		TilePosition buildLocation = null;

		// If the building is a center then search specifically for a base
		// location.
		if (building == Core.getInstance().getPlayer().getRace().getCenter()) {
			buildLocation = this.findCenterBuildLocation(building, targetTilePosition, goapUnit);
		}
		// If the Building is a refinery then search specifically for vaspene
		// geysers.
		else if (building == Core.getInstance().getPlayer().getRace().getRefinery()) {
			buildLocation = this.findRefineryBuildLocation(building, targetTilePosition, goapUnit);
		}
		// If none of the above match the criteria just look for a standard
		// build location.
		else {
			buildLocation = this.findStandardBuildLocation(building, targetTilePosition, goapUnit);
		}

		return buildLocation;
	}

	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition with a max range for a center building (command center,
	 * nexus, ...).
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
	private TilePosition findCenterBuildLocation(UnitType building, TilePosition targetTilePosition,
			IGoapUnit goapUnit) {
		BaseLocation newBaseLocation = null;

		for (BaseLocation baselocation : BWTA.getBaseLocations()) {
			if ((newBaseLocation == null || baselocation.getDistance(targetTilePosition.toPosition()) < newBaseLocation
					.getDistance(targetTilePosition.toPosition())) && this.isBaseLocationFree(baselocation)) {
				newBaseLocation = baselocation;
			}
		}
		return newBaseLocation.getTilePosition();
	}

	/**
	 * Function for testing if a base location is already occupied or if it is
	 * free. It does this by scanning a radius around the provided BaseLocation
	 * and checking if a Unit is on the checked TilePosition.
	 * 
	 * @param baselocation
	 *            the BaseLocation that is going to be checked.
	 * @return true if no Units are found at the provided BaseLocation or false
	 *         if one is found.
	 */
	private boolean isBaseLocationFree(BaseLocation baselocation) {
		boolean locationFree = true;

		for (int i = -this.maxBuildingSearchRadius; i <= this.maxBuildingSearchRadius && locationFree; i++) {
			for (int j = -this.maxBuildingSearchRadius; j <= this.maxBuildingSearchRadius & locationFree; j++) {
				int tileposX = baselocation.getTilePosition().getX() + i;
				int tileposY = baselocation.getTilePosition().getY() + j;

				if (tileposX <= Core.getInstance().getGame().mapWidth() && tileposX >= 0
						&& tileposY <= Core.getInstance().getGame().mapHeight() && tileposY >= 0) {
					locationFree = Core.getInstance().getGame().getUnitsOnTile(new TilePosition(tileposX, tileposY))
							.isEmpty();
				}
			}
		}
		return locationFree;
	}

	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition with a max range for a refinery. This needs to be a special
	 * function since these buildings are constructed on top of a vaspene
	 * geyser, which has to be checked individually.
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
	private TilePosition findRefineryBuildLocation(UnitType building, TilePosition targetTilePosition,
			IGoapUnit goapUnit) {
		TilePosition buildLocation = null;
		Unit closestGeyser = null;

		// Find the closest geyser and check if no refinery is build on top of
		// it.
		for (Unit geyser : Core.getInstance().getGame().getGeysers()) {
			List<Unit> unitsOnGeyserTile = Core.getInstance().getGame().getUnitsOnTile(geyser.getTilePosition());
			boolean allowedToBuildOnTile = true;

			// Test all Units on the TilePosition if they are a refinery.
			for (Unit unit : unitsOnGeyserTile) {
				if (unit.getType() == Core.getInstance().getPlayer().getRace().getRefinery()) {
					allowedToBuildOnTile = false;
					break;
				}
			}

			// Check distance and the allowance to build on the TilePosition.
			if (allowedToBuildOnTile
					&& (closestGeyser == null || geyser.getDistance(targetTilePosition.toPosition()) < closestGeyser
							.getDistance(targetTilePosition.toPosition()))) {
				closestGeyser = geyser;
			}
		}

		if (closestGeyser != null) {
			buildLocation = closestGeyser.getTilePosition();
		}
		return buildLocation;
	}

	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition with a max range for a standard building. This includes all
	 * buildings except refineries and center buildings.
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
	private TilePosition findStandardBuildLocation(UnitType building, TilePosition targetTilePosition,
			IGoapUnit goapUnit) {
		TilePosition buildLocation = null;
		int counter = 0;

		while (buildLocation == null && counter < this.maxTileRange) {
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
	protected boolean arePlayerUnitsBlocking(HashSet<TilePosition> desiredTilePositions, IGoapUnit constructor) {
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
	protected boolean areTilePositionsContended(HashSet<TilePosition> desiredTilePositions) {
		for (TilePosition tilePosition : desiredTilePositions) {
			if (tilePositionContenders.contains(tilePosition)) {
				return true;
			}
		}
		return false;
	}

	// TODO: Needed Change: Superclass together with
	// TilePositionContenderFactory
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
	protected HashSet<TilePosition> generateNeededTilePositions(UnitType unitType, TilePosition targetTilePosition) {
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

	// ------------------------------ Getter / Setter

	protected HashSet<TilePosition> getTilePositionContenders() {
		return tilePositionContenders;
	}
}
