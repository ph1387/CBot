package buildingOrderModule.commands;

import buildingOrderModule.CommandSender;
import bwapi.UnitType;

/**
 * BuildBuildingCommand.java --- Superclass for all Commands that base
 * themselves on constructing a building.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommand extends BuildCommand {

	public BuildBuildingCommand(UnitType assignedUnit, CommandSender receiver) {
		super(assignedUnit, receiver);
	}

	// -------------------- Functions

	@Override
	public void execute() {
		this.receiver.buildBuilding(this.assignedUnit);
	}

	@Override
	public boolean requirementMatched() {
		return true;
	}
}
