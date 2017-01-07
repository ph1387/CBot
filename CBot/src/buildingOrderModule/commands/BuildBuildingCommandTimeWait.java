package buildingOrderModule.commands;

import bwapi.UnitType;
import core.Core;

public class BuildBuildingCommandTimeWait extends BuildBuildingCommand implements Requirement {

	// Gets set once the requirement was checked once
	private Integer pointTimerStart;

	public BuildBuildingCommandTimeWait(UnitType building, int wait) {
		super(building);

		this.assignedValue = wait;
	}

	@Override
	public boolean requirementMatched() {
		// Once the time gets checked the first time, set it to the elapsed time
		// to calculate the difference in later iterations
		if (this.pointTimerStart == null) {
			this.pointTimerStart = Core.getInstance().getGame().elapsedTime();
		}

		// If a certain of time has passed execute the command
		if (this.assignedValue <= Core.getInstance().getGame().elapsedTime() - this.pointTimerStart) {
			return true;
		} else {
			return false;
		}
	}

}
