package unitControlModule.stateFactories.actions.executableActions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwapiMath.Vector;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

/**
 * GroupingAtPositionActionBaseEntrance.java --- Grouping Action with which a
 * executing Unit is grouping with other ones at the entrance of the base.
 * 
 * @author P H - 15.12.2017
 *
 */
public class GroupingAtPositionActionBaseEntrance extends GroupingAtPositionAction {

	/**
	 * RegionWrapper.java --- Wrapper Class for Regions. Used for storing the
	 * Region a ChokePoint is associated with. This ChokePoint resembles the
	 * outer border of the Players influence sphere whilst the Region is one
	 * where no center is yet build (-> Unknown territory). Therefore the Units
	 * should gather at a Point at the ChokePoint <b>in</b> the associated
	 * Region. Units grouping there will generate new Points to stand on <b>in
	 * this specific</b> Region, spreading towards the enemy and not towards the
	 * base itself. <br>
	 * This effectively minimizes the calculation amount later on since the
	 * direction in which a resulting Vector must point is known (-> In the
	 * direction of the referenced Region).
	 * 
	 * @author P H - 01.01.2018
	 *
	 */
	private class RegionWrapper {

		private Region region;
		private Chokepoint chokepoint;

		public RegionWrapper(Region region, Chokepoint chokepoint) {
			this.region = region;
			this.chokepoint = chokepoint;
		}

		public Region getRegion() {
			return region;
		}

		public Chokepoint getChokepoint() {
			return chokepoint;
		}

	}

	// The length of the Vector emerging from the middle of the chosen
	// ChokePoint.
	private double vectorGatherLength = 128.;

	/**
	 * @param target
	 *            type: Null (Is indirectly defined by Class itself)
	 */
	public GroupingAtPositionActionBaseEntrance(Object target) {
		super(target);
	}

	// -------------------- Functions

	@Override
	protected Position generateGroupingPosition(IGoapUnit goapUnit) {
		PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		Position groupingPosition = null;

		HashSet<Region> regionsWithCenters = this.extractRegionsWithCenters(playerUnit.getInformationStorage());
		HashSet<Chokepoint> chokePoints = this.extractChokePoints(regionsWithCenters);
		List<RegionWrapper> regionWrappers = this.extractPossibleRegionWrappers(chokePoints, regionsWithCenters);

		this.sortBasedOnDistance(regionWrappers, playerUnit);

		// Generate a Position at the ChokePoint inside the associated Region
		// for Units to gather at.
		if (!regionWrappers.isEmpty()) {
			groupingPosition = this.generateGroupingPosition(regionWrappers.get(0));
		}

		return groupingPosition;
	}

	/**
	 * Function for extracting all Regions from the map that are currently
	 * holding center-Units.
	 * 
	 * @param informationStorage
	 *            the instance providing access to all currently relevant game
	 *            information that is being shared by the different components.
	 * @return a HashSet of all Regions currently holding center-Units.
	 */
	private HashSet<Region> extractRegionsWithCenters(InformationStorage informationStorage) {
		HashSet<Unit> centers = informationStorage.getCurrentGameInformation().getCurrentUnits()
				.getOrDefault(Core.getInstance().getPlayer().getRace().getCenter(), new HashSet<Unit>());
		HashSet<Region> regionsWithCenters = new HashSet<>();

		for (Unit unit : centers) {
			regionsWithCenters.add(BWTA.getRegion(unit.getPosition()));
		}

		return regionsWithCenters;
	}

	/**
	 * Function for extracting ChokePoints from a provided HashSet of Regions.
	 * Since a Region can hold multiple ChokePoints, iterating over each one
	 * separately is necessary.
	 * 
	 * @param regions
	 *            the Region instances that are going to be used for accessing
	 *            all ChokePoints.
	 * @return a HashSet containing all ChokePoints that are part of the
	 *         provided Region HashSet.
	 */
	private HashSet<Chokepoint> extractChokePoints(HashSet<Region> regions) {
		HashSet<Chokepoint> chokePointsAtBorders = new HashSet<>();

		for (Region region : regions) {
			chokePointsAtBorders.addAll(region.getChokepoints());
		}

		return chokePointsAtBorders;
	}

	/**
	 * Function for extracting all possible Regions and ChokePoints in
	 * RegionWrapper instances. This function excludes Regions that contain a
	 * center-Unit, therefore only aiming at the border Regions / ChokePoints.
	 * 
	 * @param chokePointsAtBorders
	 *            the ChokePoints that are going to be used as reference to the
	 *            Regions that are going to be checked.
	 * @param regionsWithCenters
	 *            the excluded Regions.
	 * @return a List of RegionWrappers that contain all possible Regions and
	 *         ChokePoints which can be used for generating a grouping Position.
	 */
	private List<RegionWrapper> extractPossibleRegionWrappers(HashSet<Chokepoint> chokePointsAtBorders,
			HashSet<Region> regionsWithCenters) {
		// Temporary container for already checked Regions.
		HashSet<Region> possibleGroupingRegions = new HashSet<>();
		List<RegionWrapper> regionWrappers = new ArrayList<>();

		// Exclude all Regions that already have a Center in them.
		// -> Only border ones are valid!
		for (Chokepoint chokepoint : chokePointsAtBorders) {
			Region firstRegion = chokepoint.getRegions().first;
			Region secondRegion = chokepoint.getRegions().second;

			if (!regionsWithCenters.contains(firstRegion)) {
				possibleGroupingRegions.add(firstRegion);
				regionWrappers.add(new RegionWrapper(firstRegion, chokepoint));
			}
			if (!regionsWithCenters.contains(secondRegion)) {
				possibleGroupingRegions.add(secondRegion);
				regionWrappers.add(new RegionWrapper(secondRegion, chokepoint));
			}
		}

		return regionWrappers;
	}

	/**
	 * Function for sorting a List of RegionWrappers based on their ChokePoint
	 * distance towards a provided PlayerUnit.
	 * 
	 * @param regionWrappers
	 *            the List of RegionWrappers that is going to be sorted.
	 * @param playerUnit
	 *            the PlayerUnit which is used as reference point in the
	 *            distance calculation.
	 */
	private void sortBasedOnDistance(List<RegionWrapper> regionWrappers, final PlayerUnit playerUnit) {
		// Get the RegionWrapper whose associated ChokePoint is the closest one
		// towards the Unit.
		regionWrappers.sort(new Comparator<RegionWrapper>() {

			// Sort ascending (Closest one at 0).
			@Override
			public int compare(RegionWrapper firstWrapper, RegionWrapper secondWrapper) {
				return Integer.compare(playerUnit.getUnit().getDistance(firstWrapper.getChokepoint().getCenter()),
						playerUnit.getUnit().getDistance(secondWrapper.getChokepoint().getCenter()));
			}
		});
	}

	/**
	 * Function for generating a Position at which Units can group up. The
	 * provided RegionWrapper instance determines all necessary information:<br>
	 * <ul>
	 * <li>The ChokePoint determines the general location of the grouping
	 * Position</li>
	 * <li>The Region determines the side (= Region) of the ChokePoint at which
	 * the grouping will start at</li>
	 * </ul>
	 * 
	 * @param regionWrapper
	 *            the RegionWrapper that is going to be used for generating the
	 *            grouping Position.
	 * @return a Position at the RegionWrappers ChokePoint <b>inside</b> the
	 *         RegionWrappers referenced Region.
	 */
	private Position generateGroupingPosition(RegionWrapper regionWrapper) {
		Region targetRegion = regionWrapper.getRegion();
		Chokepoint chokePoint = regionWrapper.getChokepoint();

		// Generate a Vector emerging from the middle of the ChokePoint
		// pointing towards the referenced Region.
		Vector vectorToGatherPosition = new Vector(chokePoint.getCenter(), chokePoint.getSides().first);
		vectorToGatherPosition.setToLength(this.vectorGatherLength);

		// TODO: Possible Change: Possibility of not reaching the target Region
		// Make sure the Position is inside the desired Region.
		vectorToGatherPosition.rotateLeftDEG(90);
		Position vectorEndPosition = new Position(
				vectorToGatherPosition.getX() + (int) (vectorToGatherPosition.getDirX()),
				vectorToGatherPosition.getY() + (int) (vectorToGatherPosition.getDirY()));

		if (!BWTA.getRegion(vectorEndPosition).equals(targetRegion)) {
			vectorToGatherPosition.rotateLeftDEG(180);
			vectorEndPosition = new Position(vectorToGatherPosition.getX() + (int) (vectorToGatherPosition.getDirX()),
					vectorToGatherPosition.getY() + (int) (vectorToGatherPosition.getDirY()));
		}

		return vectorEndPosition;
	}
}
