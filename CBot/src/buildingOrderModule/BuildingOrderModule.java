package buildingOrderModule;

import buildingOrderModule.commands.BuildBuildingCommandTimeWait;
import bwapi.*;
import core.Core;

/**
 * BuildingOrderModule.java --- Module for controlling the Player's building
 * actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildingOrderModule {

	private Game game = Core.getInstance().getGame();
	private Player player = this.game.self();
	private CommandSender sender = new BuildingOrderSender();
	private BuildingCommandManager currentBuildingCommandManager = new BuildingCommandManagerTestingPurpose(this.sender);
	
	private int supplyDepotTimeStamp = 0;
	private int supplyDepotWaitTime = 60;
	private int supplyDepotBuildTriggerPoint = 2;

	public BuildingOrderModule() {

	}

	// -------------------- Functions

	public void update() {
		try {
			this.currentBuildingCommandManager.runCommands();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.updateSupplyTriggerPoint();
		this.buildSupplyIfNeeded();
	}

	// Update the trigger point of the automatic supply building mechanism
	private void updateSupplyTriggerPoint() {
		this.supplyDepotBuildTriggerPoint = (int) Math.round(3. / 20. * (this.player.supplyTotal() / 2.) + 1. / 2.);
	}

	// Build supply depots if the supply left count is low. Save the timestamp
	// of the action to only build one depot at a time.
	private void buildSupplyIfNeeded() {
		if ((this.player.supplyTotal() - this.player.supplyUsed()) / 2 <= this.supplyDepotBuildTriggerPoint
				&& this.game.elapsedTime() - this.supplyDepotTimeStamp >= this.supplyDepotWaitTime) {
			this.supplyDepotTimeStamp = this.game.elapsedTime();
			this.currentBuildingCommandManager
					.insertCommand(new BuildBuildingCommandTimeWait(UnitType.Terran_Supply_Depot, 0, this.sender));
		}

		// TODO: Enable
		// Show the state of the current building list / queue and the elements
		// in it.
		// BuildingOrderModuleDisplay.showCurrentBuildingCommandSender(this.currentBuildingCommandManager);
	}
}
