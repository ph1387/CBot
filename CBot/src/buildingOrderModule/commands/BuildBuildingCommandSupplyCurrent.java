package buildingOrderModule.commands;

import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommandSupplyCurrent.java --- Command for constructing a building based on
 * the current supply count.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommandSupplyCurrent extends BuildBuildingCommand implements Command {

	public BuildBuildingCommandSupplyCurrent(UnitType building, int supply) {
		super(building);
		
		this.assignedValue = supply;
	}

	@Override
	public boolean requirementMatched() {
		// If a certain supply limit has been reached, execute the command
		// -> Halved because the BWAPI doubles it intern
		if(this.assignedValue <= Core.getInstance().getPlayer().supplyUsed() / 2) {
			return true;
		} else {
			return false;
		}
	}

}
