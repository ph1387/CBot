package buildingOrderModule;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.buildActionManagers.BuildActionManagerFactory;
import bwapi.*;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.DefaultGoapAgent;
import javaGOAP.GoapAgent;

/**
 * BuildingOrderModule.java --- Module for controlling the Player's building
 * actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildingOrderModule {

	private CommandSender sender = new BuildingOrderSender();
	private GoapAgent buildingAgent;

	private int supplyDepotTimeStamp = 0;
	private int supplyDepotWaitTime = 60;
	private int supplyDepotBuildTriggerPoint = 2;

	private InformationStorage informationStorage;

	public BuildingOrderModule(InformationStorage informationStorage) {
		this.informationStorage = informationStorage;
		this.buildingAgent = new DefaultGoapAgent(
				BuildActionManagerFactory.createManager(sender, this.informationStorage));
	}

	// -------------------- Functions

	/**
	 * Function for updating all major functionalities of this module.
	 */
	public void update() {
		this.updateSupplyTriggerPoint();
		this.buildSupplyIfNeeded();

		try {
			// Only allow a certain amount of elements in the building and
			// training Queues.
			if (this.informationStorage.getConcurrentQueuedElementCount() < this.informationStorage
					.getMaxConcurrentElements()) {
//				this.buildingAgent.update();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Display the information on screen.
		BuildingOrderDisplay.showInformation((BuildActionManager) (this.buildingAgent).getAssignedGoapUnit());
	}

	/**
	 * Function for updating the trigger point at which new supply providers are
	 * being needed. This function ensures a dynamic construction of these.
	 */
	private void updateSupplyTriggerPoint() {
		this.supplyDepotBuildTriggerPoint = (int) Math
				.round(3. / 20. * (Core.getInstance().getPlayer().supplyTotal() / 2.) + 1. / 2.);
	}

	/**
	 * Function for constructing supply providers if the current supply is below
	 * the previously calculated trigger point. It has a time stamp as a safety
	 * feature since buildings cannot be constructed immediately.
	 */
	private void buildSupplyIfNeeded() {
		Player player = Core.getInstance().getPlayer();
		Game game = Core.getInstance().getGame();

		if ((player.supplyTotal() - player.supplyUsed()) / 2 <= this.supplyDepotBuildTriggerPoint
				&& game.elapsedTime() - this.supplyDepotTimeStamp >= this.supplyDepotWaitTime) {
			this.supplyDepotTimeStamp = game.elapsedTime();
			this.sender.buildBuilding(player.getRace().getSupplyProvider());
		}
	}
}
