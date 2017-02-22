package buildingOrderModule.commands;

import buildingOrderModule.BuildingOrderModule;
import bwapi.UnitType;

public class BuildBuildingCommand extends BuildCommand {
	
	public BuildBuildingCommand(UnitType assignedUnit) {
		super(assignedUnit);
	}
	
	// -------------------- Functions

	@Override
	public void execute() {
		// Dispatch a event with the assigned building
		BuildingOrderModule.getInstance().dispatchNewBuildingOrdersEvent(this.assignedUnit);
	}

	@Override
	public boolean requirementMatched() {
		return true;
	}
}
