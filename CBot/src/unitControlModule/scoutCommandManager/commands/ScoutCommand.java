package unitControlModule.scoutCommandManager.commands;

import bwapi.Unit;

abstract class ScoutCommand {

	protected Unit unit;
	
	public ScoutCommand(Unit unit) {
		this.unit = unit;
	}
	
	// -------------------- Functions
	
	// ------------------------------ Getter / Setter
	
	public Unit getUnit() {
		return this.unit;
	}
}
