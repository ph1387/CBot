package buildingOrderModule.commands;

import bwapi.UnitType;
import core.Core;

public class BuildBuildingCommandMinerals extends BuildBuildingCommand implements Requirement {

	public BuildBuildingCommandMinerals(UnitType building, int minerals) {
		super(building);
		
		this.assignedValue = minerals;
	}

	@Override
	public boolean requirementMatched() {
		// If a certain mineral limit has been reached, execute the command
		if(this.assignedValue >= Core.getInstance().getPlayer().minerals()) {
			return true;
		} else {
			return false;
		}
	}

}
