package buildingOrderModule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.buildActionManagers.BuildActionManagerFactory;
import bwapi.*;
import core.CBot;
import core.Core;
import informationStorage.InformationStorage;
import javaGOAP.DefaultGoapAgent;
import javaGOAP.GoapAgent;
import unitControlModule.UnitControlModule;

/**
 * BuildingOrderModule.java --- Module for controlling the Player's building
 * actions.
 * 
 * @author P H - 25.03.2017
 *
 */
public class BuildingOrderModule {

	// The elements that are allowed in the queue regardless of the generated
	// result.
	private int extraQueueElements = 1;
	// TODO: UML ADD
	// The maximum number of queued elements in the training and building Queue.
	private int maxQueuedElements = 5;

	// The Terran Unit production facilities that are tested in the generation
	// of the allowed Queue elements.
	private List<UnitType> terranProductionUnits = Arrays.asList(new UnitType[] { UnitType.Terran_Command_Center,
			UnitType.Terran_Barracks, UnitType.Terran_Factory, UnitType.Terran_Starport

	});

	// The Protoss Unit production facilities that are tested in the generation
	// of the allowed Queue elements.
	private List<UnitType> protossProductionUnits = Arrays.asList(new UnitType[] { UnitType.Protoss_Nexus,
			UnitType.Protoss_Gateway, UnitType.Protoss_Robotics_Facility, UnitType.Protoss_Stargate });

	// The Zerg Unit production facilities that are tested in the generation of
	// the allowed Queue elements.
	// NOTE:
	// Zerg do evolve, so if the Bot plays Zerg, another form of generating the
	// max number of Queue elements must be found.
	private List<UnitType> zergProductionUnits = Arrays.asList(new UnitType[] {

	});

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
			// Check the config if the updates are enabled or disabled. This is
			// necessary since some Actions of the module can only be performed
			// on "normal" maps.
			// Also only allow a certain amount of elements in the building and
			// training Queues.
			if (this.informationStorage.getiBuildingOrderModuleConfig().enableBuildingOrderModuleUpdates()
					&& CBot.getInstance().getWorkerManagerConstructionJobDistribution().getBuildingQueue().size()
							+ this.informationStorage.getTrainingQueue().size() < this
									.generateMaxConcurrentQueuedElements() + this.extraQueueElements) {
				this.buildingAgent.update();
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

	/**
	 * Function for generating the maximum allowed number of currently present
	 * elements that are forwarded into the {@link UnitControlModule} Queue.
	 * 
	 * @return the maximum number of elements that are allowed to be in the
	 *         Queue at the same time.
	 */
	private int generateMaxConcurrentQueuedElements() {
		Race race = Core.getInstance().getPlayer().getRace();
		int maxQueuedNumber = 0;

		if (race == Race.Terran) {
			maxQueuedNumber = this.extractFreeFacilitiesCount(this.terranProductionUnits);
		} else if (race == Race.Protoss) {
			maxQueuedNumber = this.extractFreeFacilitiesCount(this.protossProductionUnits);
		} else {
			// TODO: Needed Change: If the Bot plays Zerg, another form of
			// defining the maximum number of queued elements must be found.
			maxQueuedNumber = -1;
		}

		return Math.min(maxQueuedNumber, this.maxQueuedElements);
	}

	/**
	 * Function for extracting the number of training facilities that are
	 * currently not active and therefore not training any Unit.
	 * 
	 * @param facilityTypes
	 *            the UnitTypes that are counted as training facilities.
	 * @return the number of training facilities that are not currently training
	 *         any Unit.
	 */
	private int extractFreeFacilitiesCount(List<UnitType> facilityTypes) {
		int freeFacilitiesCount = 0;

		for (UnitType unitType : facilityTypes) {
			HashSet<Unit> trainingFacilities = this.informationStorage.getCurrentGameInformation().getCurrentUnits()
					.get(unitType);

			if (trainingFacilities != null) {
				for (Unit unit : trainingFacilities) {
					if (!unit.isTraining()) {
						freeFacilitiesCount++;
					}
				}
			}
		}

		return freeFacilitiesCount;
	}

}
