package unitControlModule.scoutCommandManager;

import bwapi.Unit;
import unitControlModule.scoutCommandManager.commands.Command;
import unitControlModule.scoutCommandManager.commands.CommandGoal;
import unitControlModule.scoutCommandManager.commands.ScoutCommandMove;

class Scout implements Command {

	private Unit unit;
	private Command command;
	
	public Scout(Unit unit) {
		this.unit = unit;
	}
	
	public Scout(Unit unit, Command command) {
		this(unit);
		
		this.command = command;
	}
	
	// -------------------- Functions

	@Override
	public void execute() {
		command.execute();
	}
	
	// ------------------------------ Getter / Setter
	
	public void setCommand(Command command) {
		this.command = command;
	}
	
	public Unit getUnit() {
		return this.unit;
	}
}
