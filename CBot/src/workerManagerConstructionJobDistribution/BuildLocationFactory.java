package workerManagerConstructionJobDistribution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Region;
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

	// TODO: UML REMOVE
	// Due to the large tile range there should not be any trouble finding a
	// suitable building location.
	// private int maxTileRange = 50;
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

	// TODO: UML PARAMS WORKER
	/**
	 * Function for finding a suitable building location around a given
	 * TilePosition.
	 * 
	 * @param building
	 *            the UnitType of the building that is going to be built.
	 * @param targetTilePosition
	 *            the TilePosition the new TilePosition is going to be
	 *            calculated around.
	 * @return a TilePosition at which the given building can be constructed or
	 *         null, if none is found.
	 */
	public TilePosition generateBuildLocation(UnitType building, TilePosition targetTilePosition) {
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
			buildLocation = this.findStandardBuildLocation(building, targetTilePosition);
		}

		return buildLocation;
	}

	/**
	 * Function for finding a suitable building location for a center Unit.
	 * 
	 * @return a TilePosition at which a new center building can be constructed.
	 */
	private TilePosition findCenterBuildLocation() {
		TilePosition playerStartingLocation = Core.getInstance().getPlayer().getStartLocation();
		EnemyUnit closestEnemyBuilding = this.extractClosestEnemyBuilding(playerStartingLocation);
		Region playerBaseRegion = BWTA.getStartLocation(Core.getInstance().getPlayer()).getRegion();

		// The Lists that are being cycled through.
		Queue<Region> regionsToCheck = new LinkedList<>();
		List<BaseLocation> possibleBaseLocations = new ArrayList<>();
		regionsToCheck.add(playerBaseRegion);

		// Cycle through the Queue until all necessary Regions are checked.
		while (!regionsToCheck.isEmpty()) {
			Region currentRegion = regionsToCheck.poll();
			List<BaseLocation> freeBaseLocations = new ArrayList<>();

			// Check each BaseLocation in the Region.
			for (BaseLocation baseLocation : currentRegion.getBaseLocations()) {
				if (this.isBaseLocationFree(baseLocation)) {
					freeBaseLocations.add(baseLocation);
				}
			}

			// No free BaseLocations in the current Region => Check adjacent
			// ones.
			if (freeBaseLocations.isEmpty()) {
				HashSet<Region> regionsToAdd = this.informationStorage.getMapInfo().getRegionAccessOrder()
						.get(currentRegion);

				if (regionsToAdd != null) {
					regionsToCheck.addAll(regionsToAdd);
				}
			} else {
				possibleBaseLocations.addAll(freeBaseLocations);
			}
		}

		// Sort the BaseLocations and set the new BaseLocation to the first
		// element of the sorted List.
		this.sortFreeBaseLocations(possibleBaseLocations, playerStartingLocation, closestEnemyBuilding);

		return possibleBaseLocations.iterator().next().getTilePosition();
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
		HashSet<Unit> centers = this.informationStorage.getCurrentGameInformation().getCurrentUnits()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
		boolean locationFree = true;

		for (Unit unit : centers) {
			if (BWTA.getRegion(unit.getPosition()).equals(baselocation.getRegion())) {
				locationFree = false;

				break;
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
	 * Function for sorting the provided BaseLocations based on their distance
	 * towards the closest enemy buildings known. The first element in the
	 * resulting List is the one with the greatest distance towards this
	 * EnemyUnit. The closest enemy building can be null and is then not
	 * considered in the sorting of the List. In this case only the distance to
	 * the Player's starting location matters.
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
	private void sortFreeBaseLocations(List<BaseLocation> baseLocations, final TilePosition playerStartLocation,
			final EnemyUnit closestEnemyBuilding) {
		// No enemy building => Smallest distance towards the starting location.
		if (closestEnemyBuilding == null) {
			baseLocations.sort(new Comparator<BaseLocation>() {

				@Override
				public int compare(BaseLocation baseLocationOne, BaseLocation baseLocationTwo) {
					return Double.compare(baseLocationOne.getTilePosition().getDistance(playerStartLocation),
							baseLocationTwo.getTilePosition().getDistance(playerStartLocation));
				}
			});
		}
		// Enemy known => Greatest distance towards the closest enemy building.
		else {
			baseLocations.sort(new Comparator<BaseLocation>() {

				@Override
				public int compare(BaseLocation baseLocationOne, BaseLocation baseLocationTwo) {
					return Double.compare(
							baseLocationTwo.getTilePosition()
									.getDistance(closestEnemyBuilding.getLastSeenTilePosition()),
							baseLocationOne.getTilePosition()
									.getDistance(closestEnemyBuilding.getLastSeenTilePosition()));
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

	// TODO: UML PARAMS WORKER
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
	 * @return a TilePosition at which the given building can be constructed or
	 *         null, if none is found.
	 */
	private TilePosition findStandardBuildLocation(UnitType building, TilePosition targetTilePosition) {
		HashSet<TilePosition> alreadyCheckedTilePositions = new HashSet<>();
		HashSet<Region> alreadyCheckedRegions = new HashSet<>();
		Queue<TilePosition> tilePositionsToCheck = new LinkedList<>();
		Queue<Region> regionsToCheck = new LinkedList<>();
		TilePosition currentTilePosition = null;
		Region currentRegion = null;
		HashSet<TilePosition> currentRegionUncontendedTilePositions = null;
		HashSet<TilePosition> currentRegionTilePositions = null;

		TilePosition foundTilePosition = null;
		int maxIterations = 100000;
		int iterationCounter = 0;

		while (iterationCounter < maxIterations && foundTilePosition == null) {
			if (tilePositionsToCheck.isEmpty()) {
				// First initialization.
				if (iterationCounter == 0) {
					currentRegion = BWTA.getRegion(targetTilePosition.toPosition());
					currentRegionUncontendedTilePositions = this.extractUncontendedRegionTilePositions(currentRegion);
					currentRegionTilePositions = this.extractRegionTilePositions(currentRegion);

					// TODO: WIP Needed Change: Check TilePositions outside of
					// Regions!

					tilePositionsToCheck.add(targetTilePosition);
					alreadyCheckedRegions.add(currentRegion);
				}
				// Otherwise get a TilePosition from a new Region.
				else {
					this.addUncheckedRegions(currentRegion, alreadyCheckedRegions, regionsToCheck);

					// Get a new Region and find the closest TilePosition from
					// the previous one's center.
					Position prevRegionCenter = currentRegion.getCenter();
					currentRegion = regionsToCheck.poll();

					currentRegionUncontendedTilePositions = this.extractUncontendedRegionTilePositions(currentRegion);
					currentRegionTilePositions = this.extractRegionTilePositions(currentRegion);
					TilePosition closestNextTilePosition = this.getClosestTilePosition(prevRegionCenter,
							currentRegionTilePositions);

					tilePositionsToCheck.add(closestNextTilePosition);
					alreadyCheckedRegions.add(currentRegion);
				}
			}

			currentTilePosition = tilePositionsToCheck.poll();

			if (this.isTilePositionValidConstructionSpot(building, currentTilePosition, currentRegionTilePositions,
					currentRegionUncontendedTilePositions)) {
				foundTilePosition = currentTilePosition;
			}

			// Add all free adjacent TilePositions to the Queue for checking.
			if (foundTilePosition == null) {
				HashSet<TilePosition> possibleAdjacentTilePositions = this.generatePossibleAdjacentTilePositions(
						currentTilePosition, currentRegionTilePositions, alreadyCheckedTilePositions);

				tilePositionsToCheck.addAll(possibleAdjacentTilePositions);
				alreadyCheckedTilePositions.add(currentTilePosition);
				// Added here since another failed iteration would otherwise add
				// the same elements to the Queue.
				alreadyCheckedTilePositions.addAll(possibleAdjacentTilePositions);
			}

			iterationCounter++;
		}

		return foundTilePosition;
	}

	// TODO: UML ADD
	/**
	 * Function for retrieving the TilePositions inside a specific Region.
	 * 
	 * @param region
	 *            the Region instance whose TilePositions are going to be
	 *            returned.
	 * @return a HashSet containing all TilePositions belonging to the provided
	 *         Region.
	 */
	private HashSet<TilePosition> extractRegionTilePositions(Region region) {
		return this.informationStorage.getMapInfo().getPrecomputedRegionTilePositions().get(region);
	}

	// TODO: UML ADD
	/**
	 * Function for retrieving the <b>uncontended</b> TilePositions inside a
	 * specific Region.
	 * 
	 * @param region
	 *            the Region instance whose uncontended TilePositions are going
	 *            to be returned.
	 * @return a HashSet containing all uncontended TilePositions belonging to
	 *         the provided Region.
	 */
	private HashSet<TilePosition> extractUncontendedRegionTilePositions(Region region) {
		HashSet<TilePosition> regionTilePositions = this.extractRegionTilePositions(region);
		HashSet<TilePosition> contendedTilePositions = this.informationStorage.getMapInfo().getTilePositionContenders();
		HashSet<TilePosition> regionTilePositionsCopy = new HashSet<>(regionTilePositions);

		// Extract all uncontended TilePositions from the current selected
		// Region.
		regionTilePositionsCopy.removeAll(contendedTilePositions);
		return regionTilePositionsCopy;
	}

	// TODO: UML ADD
	/**
	 * Function for adding all immediately accessible Regions from the provided
	 * one that are not already checked (= Inside the provided HashSet) to the
	 * given Queue of Regions to check.
	 * 
	 * @param currentRegion
	 *            the Region whose neighbour-Regions are going to be added
	 *            towards the Queue.
	 * @param alreadyCheckedRegions
	 *            the Regions that are going to be ignored.
	 * @param regionsToCheck
	 *            the Queue which the neighbour Regions are going to be added
	 *            to.
	 */
	private void addUncheckedRegions(Region currentRegion, HashSet<Region> alreadyCheckedRegions,
			Queue<Region> regionsToCheck) {
		HashMap<Region, HashSet<Region>> regionAccessOrder = this.informationStorage.getMapInfo()
				.getPrecomputedRegionAcccessOrders().get(currentRegion);
		HashSet<Region> currentAccessibleRegions = regionAccessOrder.get(currentRegion);

		// Add all adjacent Regions to the Queue. They must NOT be already
		// checked!
		for (Region region : currentAccessibleRegions) {
			if (!alreadyCheckedRegions.contains(region)) {
				regionsToCheck.add(region);
			}
		}
	}

	// TODO: UML ADD
	/**
	 * Function for extracting the closest TilePosition to a given Position from
	 * a HashSet of TilePosition instances.
	 * 
	 * @param target
	 *            the target Position instance to which the distance will be
	 *            calculated.
	 * @param tilePositions
	 *            the HashSet of TilePositions which the function will iterate
	 *            over.
	 * @return either the closest TilePosition from the provided HashSet of
	 *         TilePositions to the given Position or null if the HashSet is
	 *         empty.
	 */
	private TilePosition getClosestTilePosition(Position target, HashSet<TilePosition> tilePositions) {
		TilePosition closestTilePosition = null;
		double closestTilePositionDistance = -1;

		for (TilePosition tilePosition : tilePositions) {
			double distance = target.getDistance(tilePosition.toPosition());

			if (closestTilePosition == null || distance < closestTilePositionDistance) {
				closestTilePosition = tilePosition;
				closestTilePositionDistance = distance;
			}
		}

		return closestTilePosition;
	}

	// TODO: UML ADD
	/**
	 * Function for generating the TilePositions that are the direct neighbours
	 * of a provided TilePosition instance. This includes the TilePositions
	 * matching the following orientations:
	 * <ul>
	 * <li>Top-Left</li>
	 * <li>Top</li>
	 * <li>Top-Right</li>
	 * <li>Left</li>
	 * <li>Right</li>
	 * <li>Bottom-Left</li>
	 * <li>Bottom</li>
	 * <li>Bottom-Right</li>
	 * </ul>
	 * The function then checks if the generated TilePositions are inside the
	 * provided HashSet of possible Region TilePositions and <b>not</b> inside
	 * the checked one! Only TilePositions matching both criteria are being
	 * returned in the resulting HashSet.
	 * 
	 * @param tilePosition
	 *            the TilePosition whose neighbours are being generated.
	 * @param regionTilePositions
	 *            a HashSet containing all possible TilePositions that can be
	 *            returned.
	 * @param checkedTilePositions
	 *            a HashSet containing all TilePositions that must not be
	 *            returned.
	 * @return a HashSet containing the neighbour TilePositions from the
	 *         provided TilePosition instance matching the criteria mentioned
	 *         above.
	 */
	private HashSet<TilePosition> generatePossibleAdjacentTilePositions(TilePosition tilePosition,
			HashSet<TilePosition> regionTilePositions, HashSet<TilePosition> checkedTilePositions) {
		int x = tilePosition.getX();
		int y = tilePosition.getY();

		TilePosition topLeft = new TilePosition(x - 1, y - 1);
		TilePosition top = new TilePosition(x, y - 1);
		TilePosition topRight = new TilePosition(x + 1, y - 1);
		TilePosition left = new TilePosition(x - 1, y);
		TilePosition right = new TilePosition(x + 1, y);
		TilePosition bottomLeft = new TilePosition(x - 1, y + 1);
		TilePosition bottom = new TilePosition(x, y + 1);
		TilePosition bottomRight = new TilePosition(x + 1, y + 1);

		// Shortened in order to process inline.
		HashSet<TilePosition> rTp = regionTilePositions;
		HashSet<TilePosition> cTp = checkedTilePositions;
		HashSet<TilePosition> newTilePositions = new HashSet<>();

		if (this.isInRegionAndUnchecked(topLeft, rTp, cTp))
			newTilePositions.add(topLeft);
		if (this.isInRegionAndUnchecked(top, rTp, cTp))
			newTilePositions.add(top);
		if (this.isInRegionAndUnchecked(topRight, rTp, cTp))
			newTilePositions.add(topRight);
		if (this.isInRegionAndUnchecked(left, rTp, cTp))
			newTilePositions.add(left);
		if (this.isInRegionAndUnchecked(right, rTp, cTp))
			newTilePositions.add(right);
		if (this.isInRegionAndUnchecked(bottomLeft, rTp, cTp))
			newTilePositions.add(bottomLeft);
		if (this.isInRegionAndUnchecked(bottom, rTp, cTp))
			newTilePositions.add(bottom);
		if (this.isInRegionAndUnchecked(bottomRight, rTp, cTp))
			newTilePositions.add(bottomRight);

		return newTilePositions;
	}

	// TODO: UML ADD
	/**
	 * Function for checking if a TilePosition instance is inside the HashSet of
	 * Region TilePositions and not inside the ones marking the already checked
	 * ones.
	 * 
	 * @param tilePosition
	 *            the TilePosition instance that is going to be checked.
	 * @param regionTilePositions
	 *            the HashSet of Region TilePositions.
	 * @param checkedTilePositions
	 *            the HashSet of TilePositions that are forbidden / already
	 *            marked.
	 * @return true if the Region TilePosition HashSet contains the provided
	 *         TilePosition while the checked TilePosition HashSet does not.
	 */
	private boolean isInRegionAndUnchecked(TilePosition tilePosition, HashSet<TilePosition> regionTilePositions,
			HashSet<TilePosition> checkedTilePositions) {
		return regionTilePositions.contains(tilePosition) && !checkedTilePositions.contains(tilePosition);
	}

	// TODO: UML ADD
	/**
	 * Function for checking if a provided TilePosition can be used as a
	 * construction spot. This takes the dimensions of the building and the
	 * possible + uncontended TilePositions of the Region into account.
	 * 
	 * @param building
	 *            the type of building that is going to be constructed.
	 * @param possibleTilePosition
	 *            the TilePosition that is being tested as a possible
	 *            construction spot.
	 * @param currentRegionTilePositions
	 *            the possible TilePositions of the Region.
	 * @param currentRegionUncontendedTilePositions
	 *            the free TilePositions of the Region.
	 * @return true if the building can be constructed on the provided
	 *         TilePosition, false if not.
	 */
	private boolean isTilePositionValidConstructionSpot(UnitType building, TilePosition possibleTilePosition,
			HashSet<TilePosition> currentRegionTilePositions,
			HashSet<TilePosition> currentRegionUncontendedTilePositions) {
		// Check for availability. Differentiate between "inRegion" and
		// "uncontended" since both bear different meaning!
		HashSet<TilePosition> estimatedTilePositions = TilePositionFactory.generateNeededTilePositions(building,
				possibleTilePosition);
		boolean areInRegion = currentRegionTilePositions.containsAll(estimatedTilePositions);
		boolean areUncontended = currentRegionUncontendedTilePositions.containsAll(estimatedTilePositions);

		return areInRegion && areUncontended;
	}

	// TODO: UML REMOVE
	// protected boolean arePlayerUnitsBlocking(HashSet<TilePosition>
	// desiredTilePositions, Unit constructor) {

	// TOOD: UML REMOVE
	// protected boolean areTilePositionsContended(HashSet<TilePosition>
	// desiredTilePositions,
	// HashSet<TilePosition> tilePositionContenders) {

	// TODO: UML REMOVE
	// protected boolean isTargetTilePositionInValidRegion(TilePosition
	// testTilePosition, Region baseRegion) {

}
