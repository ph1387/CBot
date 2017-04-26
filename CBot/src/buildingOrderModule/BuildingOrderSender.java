package buildingOrderModule;

import bwapi.UnitType;
import core.CBot;

/**
 * BuildingOrderSender.java --- Class for sending building orders to the UnitControlModule. 
 * @author P H - 26.04.2017
 *
 */
public class BuildingOrderSender implements CommandSender {
	
	public BuildingOrderSender() {
		
	}
	
	// -------------------- Functions

	public void buildUnit(UnitType unitType) {
		CBot.getInstance().getUnitControlModule().addToTrainingQueue(unitType);
	}

	public void buildBuilding(UnitType unitType) {
		CBot.getInstance().getUnitControlModule().addToTrainingQueue(unitType);
	}
}
