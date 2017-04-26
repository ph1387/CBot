package buildingOrderModule.commands;

import buildingOrderModule.CommandSender;
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
	protected CommandSender receiver;

	public BuildCommand(CommandSender receiver) {
		this.receiver = receiver;
	}

	public BuildCommand(UnitType assignedUnit, CommandSender receiver) {
		this(receiver);
		
		this.assignedUnit = assignedUnit;
	}

	public BuildCommand(UnitType assignedUnit, int assignedValue, CommandSender receiver) {
		this(assignedUnit, receiver);

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
