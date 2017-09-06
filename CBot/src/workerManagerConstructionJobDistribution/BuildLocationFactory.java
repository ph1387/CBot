package workerManagerConstructionJobDistribution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import core.Core;
import core.TilePositionFactory;
import informationStorage.InformationStorage;
import unitTrackerModule.EnemyUnit;

/**
 * BuildLocationFinder.java --- Class for finding building locations on the map.
 * 
 * @author P H - 26.04.2017
 *
 */
public class BuildLocationFactory {

	private InformationStorage informationStorage;

	private int maxBuildingSearchTileRadius = 5;
	// Due to the large tile range there should not be any trouble finding a
	// suitable building location.
	private int maxTileRange = 50;
	// The maximum acceptable range for checking the distance between a free
	// geyser and a center building.
	private int maxDistanceGeysers = 320;

	public BuildLocationFactory(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
	}

	// -------------------- Functions

	// TODO: Possible Change: Move dependencies to implementing Class.
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
	public HashSet<TilePosition> generateNeededTilePositions(UnitType unitType, TilePosition targetTilePosition) {
		return TilePositionFactory.generateNeededTilePositions(unitType, targetTilePosition);
	}

	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition.
	 * 
	 * @param building
	 *            the UnitType of the building that is going to be built.
	 * @param targetTilePosition
	 *            the TilePosition the new TilePosition is going to be
	 *            calculated around.
	 * @param worker
	 *            the ConstructionWorker that is going to be constructing the
	 *            building.
	 * @return a TilePosition at which the given building can be constructed or
	 *         null, if none is found.
	 */
	public TilePosition generateBuildLocation(UnitType building, TilePosition targetTilePosition,
			ConstructionWorker worker) {
		TilePosition buildLocation = null;

		// If the building is a center then search specifically for a base
		// location.
		if (building == Core.getInstance().getPlayer().getRace().getCenter()) {
			buildLocation = this.findCenterBuildLocation();
		}
		// If the Building is a refinery then search specifically for vaspene
		// geysers.
		else if (building == Core.getInstance().getPlayer().getRace().getRefinery()) {
			buildLocation = this.findRefineryBuildLocation();
		}
		// If none of the above match the criteria just look for a standard
		// build location.
		else {
			buildLocation = this.findStandardBuildLocation(building, targetTilePosition, worker);
		}

		return buildLocation;
	}

	/**
	 * Function for finding a suitable building location for a center Unit.
	 * 
	 * @return a TilePosition at which a new center building can be constructed.
	 */
	private TilePosition findCenterBuildLocation() {
		BaseLocation newBaseLocation = null;
		List<BaseLocation> freeBaseLocations = this.extractFreeBaseLocations();

		// Gather information regarding the state of the game.
		TilePosition playerStartLocation = Core.getInstance().getPlayer().getStartLocation();
		EnemyUnit closestEnemyBuilding = this.extractClosestEnemyBuilding(playerStartLocation);

		// Sort the BaseLocations and set the new BaseLocation to the first
		// element of the sorted List.
		this.sortFreeBaseLocations(freeBaseLocations, playerStartLocation, closestEnemyBuilding);
		newBaseLocation = freeBaseLocations.iterator().next();

		return newBaseLocation.getTilePosition();
	}

	/**
	 * Function for extracting all BaseLocations at which a new center Unit can
	 * be constructed.
	 * 
	 * @return a List containing all BaseLocations that are considered a valid
	 *         construction site and therefore can be used for constructing a
	 *         center Unit onto.
	 */
	private List<BaseLocation> extractFreeBaseLocations() {
		List<BaseLocation> freeBaseLocations = new ArrayList<>();

		for (BaseLocation baselocation : BWTA.getBaseLocations()) {
			if (this.isBaseLocationFree(baselocation)) {
				freeBaseLocations.add(baselocation);
			}
		}
		return freeBaseLocations;
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

		for (int i = -this.maxBuildingSearchTileRadius; i <= this.maxBuildingSearchTileRadius && locationFree; i++) {
			for (int j = -this.maxBuildingSearchTileRadius; j <= this.maxBuildingSearchTileRadius
					&& locationFree; j++) {
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
	 * Function for extracting the closest EnemyUnit (Building) from the
	 * currently known ones.
	 * 
	 * @param playerStartLocation
	 *            the TilePosition the Player started at.
	 * @return the closest enemy building to the Player's starting location.
	 */
	private EnemyUnit extractClosestEnemyBuilding(TilePosition playerStartLocation) {
		EnemyUnit closestEnemyBuilding = null;
		Double closestEnemyBuildingDistance = null;

		// Get the closest known enemy building.
		for (EnemyUnit enemyBuilding : this.informationStorage.getTrackerInfo().getEnemyBuildings()) {
			double referenceDistance = enemyBuilding.getLastSeenTilePosition().getDistance(playerStartLocation);

			if (closestEnemyBuilding == null || referenceDistance < closestEnemyBuildingDistance) {
				closestEnemyBuilding = enemyBuilding;
				closestEnemyBuildingDistance = referenceDistance;
			}
		}
		return closestEnemyBuilding;
	}

	/**
	 * Function for sorting a List of BaseLocations based on two factors:
	 * <ul>
	 * <li>The Player's starting location.</li>
	 * <li>The closest known enemy building.</li>
	 * </ul>
	 * At a minimum the Player's starting location is needed for this function
	 * to properly work. The closest enemy building can be null and is then not
	 * considered in the sorting of the List. The function sorts the
	 * BaseLocations on their distance to the former and latter, with the goal
	 * of a minimum distance to the starting location and a maximum distance to
	 * the closest enemy building. Therfore sorting the List from the "best" to
	 * the "worst" locations to build a center Unit.
	 * 
	 * @param freeBaseLocations
	 *            the List of free BaseLocations that is going to be sorted.
	 * @param playerStartLocation
	 *            the Player's starting location which is necessary for sorting
	 *            the List of free BaseLocations.
	 * @param closestEnemyBuilding
	 *            the closest enemy building. Can be null and is then not
	 *            considered in the sorting of the List.
	 */
	private void sortFreeBaseLocations(List<BaseLocation> freeBaseLocations, final TilePosition playerStartLocation,
			final EnemyUnit closestEnemyBuilding) {
		// Sort the free BaseLocations either based on their distance to the
		// Player's starting location if no enemy building is known of.
		if (closestEnemyBuilding == null) {
			freeBaseLocations.sort(new Comparator<BaseLocation>() {

				@Override
				public int compare(BaseLocation baseLocationOne, BaseLocation baseLocationTwo) {
					return Double.compare(playerStartLocation.getDistance(baseLocationOne.getTilePosition()),
							playerStartLocation.getDistance(baseLocationTwo.getTilePosition()));
				}
			});
		}
		// OR take the closest enemy building in consideration when constructing
		// the center.
		else {
			freeBaseLocations.sort(new Comparator<BaseLocation>() {

				@Override
				public int compare(BaseLocation baseLocationOne, BaseLocation baseLocationTwo) {
					return Double.compare(
							playerStartLocation.getDistance(baseLocationOne.getTilePosition()) - baseLocationOne
									.getTilePosition().getDistance(closestEnemyBuilding.getLastSeenTilePosition()),
							playerStartLocation.getDistance(baseLocationTwo.getTilePosition()) - baseLocationTwo
									.getTilePosition().getDistance(closestEnemyBuilding.getLastSeenTilePosition()));
				}
			});
		}
	}

	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition with a max range for a refinery. This needs to be a special
	 * function since these buildings are constructed on top of a vaspene
	 * geyser, which has to be checked individually.
	 *
	 * @return a TilePosition at which the given building can be constructed or
	 *         null, if none is found.
	 */
	private TilePosition findRefineryBuildLocation() {
		TilePosition buildLocation = null;
		HashSet<Unit> freeGeysers = this.extractFreeGeysers();
		Unit foundGeyser = this.extractGeyserNearExistingCenter(freeGeysers);

		// In case of no geyser being found in the first iteration, check the
		// next center build location for a refinery build location.
		// NOTE:
		// The result is the closest FREE one near the next location!
		if (foundGeyser == null) {
			foundGeyser = this.extractGeyserNearNextCenterLocation(freeGeysers);
		}

		if (foundGeyser != null) {
			buildLocation = foundGeyser.getTilePosition();
		}
		return buildLocation;
	}

	/**
	 * Function for extracting a geyser from the provided HashSet of free
	 * geysers that is near an already existing center building. This should be
	 * the preferred check for suitable building locations since constructing a
	 * refinery by itself is counter productive.
	 * 
	 * @param freeGeysers
	 *            the HashSet of geysers without any refinery on them.
	 * @return a geyser of the provided HashSet near an already existing center
	 *         Unit that has not yet been improved.
	 */
	private Unit extractGeyserNearExistingCenter(HashSet<Unit> freeGeysers) {
		// Iterate through all currently active center buildings and compare the
		// distances to the free geysers until (hopefully) a matching one is
		// found.
		for (Unit center : this.informationStorage.getCurrentGameInformation().getCurrentUnits()
				.get(Core.getInstance().getPlayer().getRace().getCenter())) {
			for (Unit geyser : freeGeysers) {
				if (center.getDistance(geyser) <= this.maxDistanceGeysers) {
					return geyser;
				}
			}
		}
		return null;
	}

	/**
	 * Function for extracting a geyser from the provided HashSet of free
	 * geysers that is near the next generated center build location.
	 * 
	 * @param freeGeysers
	 *            the HashSet of geysers without any refinery on them.
	 * @return a geyser of the provided HashSet that is the closest one to the
	 *         next generated center build location.
	 */
	private Unit extractGeyserNearNextCenterLocation(HashSet<Unit> freeGeysers) {
		TilePosition nextCenterBuildLocation = this.findCenterBuildLocation();
		Unit closestGeyser = null;

		// Find the closest geyser to the next center's TilePosition.
		for (Unit geyser : freeGeysers) {
			if ((closestGeyser == null || geyser.getDistance(nextCenterBuildLocation.toPosition()) < closestGeyser
					.getDistance(nextCenterBuildLocation.toPosition()))) {
				closestGeyser = geyser;
			}
		}
		return closestGeyser;
	}

	/**
	 * Function for extracting all geysers on the map without any refinery on
	 * them.
	 * 
	 * @return a HashSet containing all geysers on the map without a refinery of
	 *         the Bot's Race on them.
	 */
	private HashSet<Unit> extractFreeGeysers() {
		HashSet<Unit> freeGeysers = new HashSet<>();

		// Extract all free geysers without any refinery on them.
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

			if (allowedToBuildOnTile) {
				freeGeysers.add(geyser);
			}
		}
		return freeGeysers;
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
	 * @param worker
	 *            the ConstructionWorker that is going to be constructing the
	 *            building.
	 * @return a TilePosition at which the given building can be constructed or
	 *         null, if none is found.
	 */
	private TilePosition findStandardBuildLocation(UnitType building, TilePosition targetTilePosition,
			ConstructionWorker worker) {
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
					HashSet<TilePosition> neededTilePositions = TilePositionFactory
							.generateNeededTilePositions(building, testPosition);

					// If the space is free, try changing the building's
					// location.
					if (Core.getInstance().getGame().canBuildHere(testPosition, building)
							&& !this.arePlayerUnitsBlocking(neededTilePositions, worker.getUnit())
							&& !this.areTilePositionsContended(neededTilePositions,
									this.informationStorage.getMapInfo().getTilePositionContenders())) {
						buildLocation = testPosition;
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
	 *            the Unit that is going to be building at the TilePosition.
	 *            Needed to exclude the constructor from the blocking Units.
	 * @return true or false depending if a Player Unit is blocking the desired
	 *         TilePosition / a desired TilePosition.
	 */
	protected boolean arePlayerUnitsBlocking(HashSet<TilePosition> desiredTilePositions, Unit constructor) {
		// Check each player Unit except the constructor itself
		for (TilePosition tilePosition : desiredTilePositions) {
			for (Unit unit : Core.getInstance().getGame().getUnitsOnTile(tilePosition)) {
				if (unit != constructor) {
					return true;
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
	 * @param tilePositionContenders
	 *            the HashSet that stores all currently contended TilePositions.
	 * @return true or false depending if one of the desired TilePositions is
	 *         already contended.
	 */
	protected boolean areTilePositionsContended(HashSet<TilePosition> desiredTilePositions,
			HashSet<TilePosition> tilePositionContenders) {
		for (TilePosition tilePosition : desiredTilePositions) {
			if (tilePositionContenders.contains(tilePosition)) {
				return true;
			}
		}
		return false;
	}

}
