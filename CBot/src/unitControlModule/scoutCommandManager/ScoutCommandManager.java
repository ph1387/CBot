package unitControlModule.scoutCommandManager;

import java.util.ArrayList;
import java.util.List;

import bwapi.TilePosition;
import bwapi.Unit;
import bwta.BWTA;
import bwta.BaseLocation;
import bwta.Chokepoint;
import core.Core;
import unitControlModule.scoutCommandManager.commands.Command;
import unitControlModule.scoutCommandManager.commands.CommandGoal;
import unitControlModule.scoutCommandManager.commands.ScoutCommandMove;

public class ScoutCommandManager {

	private Scout assignedScout;
	private int stateCounter = 0;

	// Commandlist, which the object is working on
	private List<ScoutCommandMove> scoutCommandList = new ArrayList<ScoutCommandMove>();

	// Requirementlist, which the object has to check before executing a command
	private List<ScoutCommandMove> scoutGoalList = new ArrayList<ScoutCommandMove>();

	public ScoutCommandManager(Unit unit) {
		this.assignedScout = new Scout(unit);
	}

	// -------------------- Functions

	// Generate a scouting list for the scout. The distance between the
	// locations is minimized
	public void generateBaseScoutingList() {
		List<BaseLocation> baseLocations = BWTA.getBaseLocations();
		TilePosition currentBaseTilePosition = Core.getInstance().getPlayer().getStartLocation();
		
		// Assign all base locations to a command for the scout
		while (!baseLocations.isEmpty()) {
			BaseLocation nearestBaseLocation = null;

			// Find the nearest base location
			for (BaseLocation baseLocation : baseLocations) {
				// Do not assign the starting base
				if (baseLocation.getTilePosition() == Core.getInstance().getPlayer().getStartLocation()) {
					baseLocations.remove(baseLocation);
				}
				// Find the closest BaseLocation relative to the current
				// location of the remaining BaseLocations
				else {
					if (nearestBaseLocation == null
							|| baseLocation.getTilePosition().getDistance(currentBaseTilePosition) < nearestBaseLocation
									.getTilePosition().getDistance(currentBaseTilePosition)) {
						nearestBaseLocation = baseLocation;
					}
				}
			}

			if (nearestBaseLocation != null) {
				// Get the TilePosition of the nearest BaseLocation, remove it
				// from the pool of possible BaseLocations and generate a
				// command object based on the found TilePosition and add it to
				// the list of locations the scout has to visit.
				currentBaseTilePosition = nearestBaseLocation.getTilePosition();
				baseLocations.remove(nearestBaseLocation);

				ScoutCommandMove scoutCommandMove = new ScoutCommandMove(this.assignedScout.getUnit(),
						currentBaseTilePosition);
				this.scoutCommandList.add(scoutCommandMove);
				this.scoutGoalList.add((ScoutCommandMove) scoutCommandMove);
			}
		}
	}

	public void runCommands() {
		// Test if the current command is finished before assigning the next
		// command to the unit
		try {
			// The first command has to apply always. Check the stateCounter - 1
			// afterwards since the previous action has to achieve its goal
			// first
			if (this.stateCounter == 0 || this.stateCounter < this.scoutCommandList.size()
					&& this.scoutGoalList.get(stateCounter - 1).commandGoalReached()) {

				this.assignedScout.setCommand(this.scoutCommandList.get(this.stateCounter));
				this.stateCounter++;
			}

			// Execute the currently active command
			this.assignedScout.execute();
		} catch (Exception e) {
			System.out.println("---SCOUTCOMMANDMANAGER: error---");
			e.printStackTrace();
		}
	}
}
