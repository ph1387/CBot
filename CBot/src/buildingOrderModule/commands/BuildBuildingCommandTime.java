package buildingOrderModule.commands;

import buildingOrderModule.CommandSender;
import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommandTime.java --- Command for constructing a building based on
 * the current time.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommandTime extends BuildBuildingCommand implements Command {

	public BuildBuildingCommandTime(UnitType building, int time, CommandSender receiver) {
		super(building, receiver);
		
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
