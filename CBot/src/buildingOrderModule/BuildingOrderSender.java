package buildingOrderModule;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.CBot;
import unitControlModule.UnitControlModule;

/**
 * BuildingOrderSender.java --- Class for sending building orders to the
 * UnitControlModule.
 * 
 * @author P H - 26.04.2017
 *
 */
public class BuildingOrderSender implements CommandSender {

	private UnitControlModule unitControlModule = CBot.getInstance().getUnitControlModule();

	public BuildingOrderSender() {

	}

	// -------------------- Functions

	public void buildUnit(UnitType unitType) {
		this.unitControlModule.addToTrainingQueue(unitType);
	}

	public void buildBuilding(UnitType unitType) {
		this.unitControlModule.addToBuildingQueue(unitType);
	}

	@Override
	public void researchTech(TechType techType) {
		this.unitControlModule.addToResearchQueue(techType);
	}

	@Override
	public void buildAddon(UnitType addon) {
		this.unitControlModule.addToAddonQueue(addon);
	}

	@Override
	public void buildUpgrade(UpgradeType upgradeType) {
		this.unitControlModule.addToUpgradeQueue(upgradeType);
	}
}
