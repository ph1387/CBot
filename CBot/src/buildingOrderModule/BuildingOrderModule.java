package buildingOrderModule;

import java.util.ArrayList;
import java.util.List;

import buildingOrderModule.commands.BuildBuildingCommand;
import buildingOrderModule.commands.BuildBuildingCommandSupplyCurrent;
import buildingOrderModule.commands.BuildBuildingCommandTimeWait;
import buildingOrderModule.commands.BuildBuildingCommandWorkerCount;
import buildingOrderModule.commands.BuildUnitCommand;
import bwapi.*;
import cBotBWEventDistributor.CBotBWEventDistributor;
import cBotBWEventDistributor.CBotBWEventListener;
import core.Core;

public class BuildingOrderModule implements CBotBWEventListener {

	private static BuildingOrderModule instance;

	private Game game;
	private Player player;
	private BuildingCommandManager currentBuildingCommandManager;
	private int supplyDepotTimeStamp = 0;
	private int supplyDepotWaitTime = 80;
	private int supplyDepotBuildTriggerPoint = 2;

	private List<Object> buildingOrdersListeners = new ArrayList<Object>();

	private BuildingOrderModule() {
		this.game = Core.getInstance().getGame();
		this.player = this.game.self();

		// Default building queue in the beginning
		BuildingCommandManager beginningSender = new BuildingCommandManager();
		beginningSender.addCommand(new BuildBuildingCommandWorkerCount(UnitType.Terran_Barracks, 11));
		for (int i = 0; i < 25; i++) {
			beginningSender.addCommand(new BuildUnitCommand(UnitType.Terran_Marine));
		}
		beginningSender.addCommand(new BuildBuildingCommandWorkerCount(UnitType.Terran_Barracks, 13));
		beginningSender.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Refinery, 21));
		beginningSender.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Command_Center, 24));
		beginningSender.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Factory, 26));
		beginningSender.addCommand(new BuildBuildingCommandSupplyCurrent(UnitType.Terran_Factory, 28));
		for (int i = 0; i < 10; i++) {
			beginningSender.addCommand(new BuildUnitCommand(UnitType.Terran_Vulture));
		}

		this.currentBuildingCommandManager = beginningSender;
		
		
		
		
		
		
		
		
		
		
		
		
		

		CBotBWEventDistributor.getInstance().addListener(this);
	}

	// -------------------- Functions

	// Singleton function
	public static BuildingOrderModule getInstance() {
		if (instance == null) {
			instance = new BuildingOrderModule();
		}
		return instance;
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

	// -------------------- Eventlisteners

	// ------------------------------ Own CBotBWEventListener
	@Override
	public void onStart() {

	}

	@Override
	public void onFrame() {
		try {
			this.currentBuildingCommandManager.runCommands();
		} catch (Exception e) {
			System.out.println("---BUILDINGCOMMAND: error onFrame---");
		}

		this.updateSupplyTriggerPoint();
		this.buildSupplyIfNeeded();
	}

	@Override
	public void onUnitCreate(Unit unit) {

	}

	@Override
	public void onUnitComplete(Unit unit) {

	}

	@Override
	public void onUnitDestroy(Unit unit) {

	}

	// -------------------- Events

	// ------------------------------ Building orders
	public synchronized void addBuildingOrdersEventListener(DistributeBuildingOrdersEventListener listener) {
		this.buildingOrdersListeners.add(listener);
	}

	public synchronized void removeBuildingOrdersEventListener(DistributeBuildingOrdersEventListener listener) {
		this.buildingOrdersListeners.remove(listener);
	}

	public synchronized void dispatchNewBuildingOrdersEvent(UnitType building) {
		for (Object listener : buildingOrdersListeners) {
			((DistributeBuildingOrdersEventListener) listener).onDistributeBuildingOrders(building);
		}
	}

	public synchronized void dispatchNewUnitBuildingOrdersEvent(UnitType unit) {
		for (Object listener : buildingOrdersListeners) {
			((DistributeBuildingOrdersEventListener) listener).onDistributeUnitBuildingOrders(unit);
		}
	}
}
