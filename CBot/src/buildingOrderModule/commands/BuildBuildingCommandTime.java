package buildingOrderModule.commands;

import bwapi.UnitType;
import core.Core;

public class BuildBuildingCommandTime extends BuildBuildingCommand implements Requirement {

	public BuildBuildingCommandTime(UnitType building, int time) {
		super(building);
		
		this.assignedValue = time;
	}

	@Override
	public boolean requirementMatched() {
		// If a certain time (point) has been reached, execute the command
		if(this.assignedValue >= Core.getInstance().getGame().elapsedTime()) {
			return true;
		} else {
			return false;
		}
	}

}
