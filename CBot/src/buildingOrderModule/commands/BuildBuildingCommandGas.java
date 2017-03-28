package buildingOrderModule.commands;

import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommandGas.java --- Command for constructing a building based on
 * the current gas count.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommandGas extends BuildBuildingCommand implements Command {

	public BuildBuildingCommandGas(UnitType building, int gas) {
		super(building);

		this.assignedValue = gas;
	}

	@Override
	public boolean requirementMatched() {
		// If a certain gas limit has been reached, execute the command
		if (this.assignedValue >= Core.getInstance().getPlayer().gas()) {
			return true;
		} else {
			return false;
		}
	}

}
