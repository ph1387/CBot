package buildingOrderModule.commands;

import buildingOrderModule.BuildingOrderModule;
import bwapi.UnitType;

/**
 * BuildBuildingCommand.java --- Superclass for all Commands that base
 * themselves on constructing a building.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommand extends BuildCommand {

	public BuildBuildingCommand(UnitType assignedUnit) {
		super(assignedUnit);
	}

	// -------------------- Functions

	@Override
	public void execute() {
		BuildingOrderModule.getInstance().buildBuilding(this.assignedUnit);
	}

	@Override
	public boolean requirementMatched() {
		return true;
	}
}
