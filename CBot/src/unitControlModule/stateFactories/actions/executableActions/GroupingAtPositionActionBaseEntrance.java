package unitControlModule.stateFactories.actions.executableActions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import bwapi.Position;
import bwapi.Unit;
import bwta.BWTA;
import bwta.Chokepoint;
import bwta.Region;
import core.Core;
import javaGOAP.IGoapUnit;
import unitControlModule.unitWrappers.PlayerUnit;

// TODO: UML ADD
/**
 * GroupingAtPositionActionBaseEntrance.java --- Grouping Action with which a
 * executing Unit is grouping with other ones at the entrace of the base.
 * 
 * @author P H - 15.12.2017
 *
 */
public class GroupingAtPositionActionBaseEntrance extends GroupingAtPositionAction {

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
		final PlayerUnit playerUnit = (PlayerUnit) goapUnit;
		HashSet<Unit> centers = playerUnit.getInformationStorage().getCurrentGameInformation().getCurrentUnits()
				.getOrDefault(Core.getInstance().getPlayer().getRace().getCenter(), new HashSet<Unit>());
		HashSet<Region> regionsWithCenters = new HashSet<>();
		HashSet<Chokepoint> chokePointsAtBorders = new HashSet<>();
		List<Region> possibleGroupingRegions = new ArrayList<Region>();
		Position groupingPosition = null;

		// Get all Regions that need to be checked.
		for (Unit unit : centers) {
			regionsWithCenters.add(BWTA.getRegion(unit.getPosition()));
		}

		// Get all ChokePoints for each Region.
		for (Region region : regionsWithCenters) {
			chokePointsAtBorders.addAll(region.getChokepoints());
		}

		// Exclude all Regions that already have a Center in them.
		for (Chokepoint chokepoint : chokePointsAtBorders) {
			if (!regionsWithCenters.contains(chokepoint.getRegions().first)) {
				possibleGroupingRegions.add(chokepoint.getRegions().first);
			}
			if (!regionsWithCenters.contains(chokepoint.getRegions().second)) {
				possibleGroupingRegions.add(chokepoint.getRegions().second);
			}
		}

		// Get the Region whose center is the closest.
		possibleGroupingRegions.sort(new Comparator<Region>() {

			@Override
			public int compare(Region firstRegion, Region secondRegion) {
				return Integer.compare(playerUnit.getUnit().getDistance(firstRegion.getCenter()),
						playerUnit.getUnit().getDistance(secondRegion.getCenter()));
			}
		});

		if (possibleGroupingRegions.get(0) != null) {
			groupingPosition = possibleGroupingRegions.get(0).getCenter();
		}

		return groupingPosition;
	}

}
