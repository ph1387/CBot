package buildingOrderModule.commands;

import bwapi.UnitType;
import core.Core;

public class BuildBuildingCommandSupplyTotal extends BuildBuildingCommand implements Requirement {

	public BuildBuildingCommandSupplyTotal(UnitType building, int supply) {
		super(building);
		
		this.assignedValue = supply;
	}

	@Override
	public boolean requirementMatched() {
		// If a certain supply limit has been reached, execute the command
		// -> Halved because the BWAPI doubles it intern
		if(this.assignedValue <= Core.getInstance().getPlayer().supplyTotal() / 2) {
			return true;
		} else {
			return false;
		}
	}

}
