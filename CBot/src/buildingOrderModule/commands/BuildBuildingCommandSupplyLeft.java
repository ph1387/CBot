package buildingOrderModule.commands;

import bwapi.Player;
import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommandSupplyLeft.java --- Command for constructing a building
 * based on the current supply count that is left.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommandSupplyLeft extends BuildBuildingCommand implements Command {

	public BuildBuildingCommandSupplyLeft(UnitType building, int supplyLeft) {
		super(building);

		this.assignedValue = supplyLeft;
	}

	@Override
	public boolean requirementMatched() {
		// If supply is needed and the difference in supply reached execute the
		// command
		// -> Halved because the BWAPI doubles it intern
		Player player = Core.getInstance().getPlayer();

		if (this.assignedValue >= (int) (player.supplyTotal() - player.supplyUsed()) / 2) {
			return true;
		} else {
			return false;
		}
	}

}
