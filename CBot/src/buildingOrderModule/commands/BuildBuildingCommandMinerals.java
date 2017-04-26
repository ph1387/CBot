package buildingOrderModule.commands;

import buildingOrderModule.CommandSender;
import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommandMinerals.java --- Command for constructing a building based on
 * the current mineral count.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommandMinerals extends BuildBuildingCommand implements Command {

	public BuildBuildingCommandMinerals(UnitType building, int minerals, CommandSender receiver) {
		super(building, receiver);
		
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
