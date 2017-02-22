package buildingOrderModule.commands;

import bwapi.UnitType;

public abstract class BuildCommand implements Command, Requirement {
	// Which building gets constructed and what value does a subclass assign the condition
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
