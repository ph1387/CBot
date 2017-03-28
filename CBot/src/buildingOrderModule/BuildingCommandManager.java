package buildingOrderModule;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.commands.Command;

/**
 * BuildingCommandManager.java --- A CommandManager for storing a List of
 * building commands that will be executed one after another.
 * 
 * @author P H - 25.03.2017
 *
 */
class BuildingCommandManager {

	protected int stateCounter = 0;
	protected List<Command> commandList = new ArrayList<Command>();

	public BuildingCommandManager() {

	}

	public BuildingCommandManager(List<Command> commandList) {
		this.commandList = commandList;
	}

	// -------------------- Functions

	public void addCommand(Command command) {
		this.commandList.add(command);
	}

	public void removeCommand(Command command) {
		int index = this.commandList.indexOf(command);

		this.removeCommandAt(index);
	}

	public void removeCommandAt(int index) {
		this.commandList.remove(index);
	}

	// Insert a command at the current position
	public void insertCommand(Command command) {
		this.insertCommandAt(this.stateCounter, command);
	}

	// Insert a command at a index
	public void insertCommandAt(int index, Command command) {
		try {
			this.commandList.add(index, command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void runCommands() {
		// If the current requirement is met, execute the stored command
		// and increment the counter for the next loop
		try {
			if (stateCounter < commandList.size() && commandList.get(stateCounter).requirementMatched()) {
				commandList.get(stateCounter).execute();
				stateCounter++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ------------------------------ Getter / Setter

	public List<Command> getCommandList() {
		return this.commandList;
	}

	public int getStateCounter() {
		return this.stateCounter;
	}
}
