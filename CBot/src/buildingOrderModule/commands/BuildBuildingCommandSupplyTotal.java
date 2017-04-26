package buildingOrderModule.commands;

import buildingOrderModule.CommandSender;
import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommandSupplyTotal.java --- Command for constructing a building
 * based on the total supply count.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommandSupplyTotal extends BuildBuildingCommand implements Command {

	public BuildBuildingCommandSupplyTotal(UnitType building, int supply, CommandSender receiver) {
		super(building, receiver);

		this.assignedValue = supply;
	}

	@Override
	public boolean requirementMatched() {
		// If a certain supply limit has been reached, execute the command
		// -> Halved because the BWAPI doubles it intern
		if (this.assignedValue <= Core.getInstance().getPlayer().supplyTotal() / 2) {
			return true;
		} else {
			return false;
		}
	}

}
