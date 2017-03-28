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

	private static BuildingOrderModule instance;

	private Game game;
	private Player player;
	private BuildingCommandManager currentBuildingCommandManager;
	private int supplyDepotTimeStamp = 0;
	private int supplyDepotWaitTime = 60;
	private int supplyDepotBuildTriggerPoint = 2;

	private BuildingOrderModule() {
		this.game = Core.getInstance().getGame();
		this.player = this.game.self();

		this.currentBuildingCommandManager = new BuildingCommandManagerTestingPurpose();
	}

	// -------------------- Functions

	/**
	 * Singleton function.
	 * 
	 * @return instance of the class.
	 */
	public static BuildingOrderModule getInstance() {
		if (instance == null) {
			instance = new BuildingOrderModule();
		}
		return instance;
	}

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
					.insertCommand(new BuildBuildingCommandTimeWait(UnitType.Terran_Supply_Depot, 0));
		}

		// Show the state of the current building list / queue and the elements
		// in it.
		BuildingOrderModuleDisplay.showCurrentBuildingCommandSender(this.currentBuildingCommandManager);
	}

	public void buildUnit(UnitType unit) {
		// TODO: Add Implementation: buildUnit
	}
	
	public void buildBuilding(UnitType unit) {
		// TODO: Add Implementation: buildBuilding
	}
}
