package buildingOrderModule;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.commands.Command;
import buildingOrderModule.commands.Requirement;
import bwapi.Unit;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;

class BuildingCommandManager {

	// Counts and keeps the index of the current command / requirement
	private int stateCounter = 0;

	// Commandlist, which the object is working on
	private List<Command> commandList = new ArrayList<Command>();

	// Requirementlist, which the object has to check before executing a command
	private List<Requirement> requirementList = new ArrayList<Requirement>();

	public BuildingCommandManager() {
		
	}

	public BuildingCommandManager(List<Command> commandList, List<Requirement> requirementList) {
		this.commandList = commandList;
		this.requirementList = requirementList;
	}
	
	public BuildingCommandManager(List<Command> commandList) {
		if(this.isEveryObjectImplementingRequirement(commandList)){
			for (Command command : commandList) {
				this.addCommand(command);
			}
		}
	}

	// -------------------- Functions

	// Add a command
	public void addCommand(Command command, Requirement requirement) {
		this.commandList.add(command);
		this.requirementList.add(requirement);
	}
	
	public void addCommand(Command command) {
		// Test if the class implements both interfaces
		if(command instanceof Requirement) {
			this.commandList.add(command);
			this.requirementList.add((Requirement)command);
		}
	}

	// Remove a command
	public void removeCommand(Command command) {
		int index = this.commandList.indexOf(command);

		this.removeCommandAt(index);
	}

	// Remove a command at a given index
	public void removeCommandAt(int index) {
		this.commandList.remove(index);
		this.requirementList.remove(index);
	}
	
	// Insert a command at the current position
	public void insertCommand(Command command) {
		if(command instanceof Requirement) {
			this.insertCommand(command, (Requirement) command);
		}
	}
	
	public void insertCommand(Command command, Requirement requirement) {
		this.insertCommandAt(this.stateCounter, command, requirement);
	}
	
	// Insert a command at a index
	public void insertCommandAt(int index, Command command, Requirement requirement) {
		try {
			this.commandList.add(index, command);
			this.requirementList.add(index, requirement);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void runCommands() {
		// If the current requirement is met, execute the stored command
		// and increment the counter for the next loop
		try {
			if (stateCounter < requirementList.size() && requirementList.get(stateCounter).requirementMatched()) {
				commandList.get(stateCounter).execute();
				stateCounter++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Test if every list element implements the Requirement interface
	private <T> boolean isEveryObjectImplementingRequirement(List<T> objectList) {
		boolean isValid = true;
		
		for (T element : objectList) {
			if(element instanceof Requirement) {
				
			} else {
				isValid = false;
			}
		}
		return isValid;
	}
	
	// ------------------------------ Getter / Setter
	
	public List<Command> getCommandList() {
		return this.commandList;
	}
	
	public List<Requirement> getRequirementList() {
		return this.requirementList;
	}
	
	public int getStateCounter() {
		return this.stateCounter;
	}
}
