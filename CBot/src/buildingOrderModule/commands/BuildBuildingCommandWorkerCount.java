package buildingOrderModule.commands;

import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import core.Core;

/**
 * BuildBuildingCommandWorkerCount.java --- Command for constructing a building
 * based on the current worker count.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildBuildingCommandWorkerCount extends BuildBuildingCommand implements Command {

	public BuildBuildingCommandWorkerCount(UnitType building, int workerCount) {
		super(building);

		this.assignedValue = workerCount;
	}

	@Override
	public boolean requirementMatched() {
		// If the desired worker count is reached, return true
		Player player = Core.getInstance().getPlayer();
		int workerCount = 0;

		for (Unit unit : player.getUnits()) {
			if (unit.getType().isWorker()) {
				workerCount++;
			}
		}

		if (this.assignedValue <= workerCount) {
			return true;
		} else {
			return false;
		}
	}

}
