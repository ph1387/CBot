package buildingOrderModule.commands;

import bwapi.UnitType;
import core.Core;

public class BuildBuildingCommandGas extends BuildBuildingCommand implements Requirement {

	public BuildBuildingCommandGas(UnitType building, int gas) {
		super(building);
		
		this.assignedValue = gas;
	}

	@Override
	public boolean requirementMatched() {
		// If a certain gas limit has been reached, execute the command
		if(this.assignedValue >= Core.getInstance().getPlayer().gas()) {
			return true;
		} else {
			return false;
		}
	}

}
