package buildingOrderModule.commands;

import bwapi.UnitType;

/**
 * BuildCommand.java --- General building Command that provides all basic
 * functions.
 * 
 * @author P H - 25.03.2017
 *
 */
public abstract class BuildCommand implements Command {

	protected UnitType assignedUnit;
	protected int assignedValue = 0;

	public BuildCommand() {

	}

	public BuildCommand(UnitType assignedUnit) {
		this.assignedUnit = assignedUnit;
	}

	public BuildCommand(UnitType assignedUnit, int assignedValue) {
		this(assignedUnit);

		this.assignedValue = assignedValue;
	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

	public UnitType getAssignedUnit() {
		return this.assignedUnit;
	}

	public int getAssignedValue() {
		return this.assignedValue;
	}
}
