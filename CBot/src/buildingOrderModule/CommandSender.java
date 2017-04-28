package buildingOrderModule;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

/**
 * CommandSender.java --- Interface for communicating with the module that
 * handles the construction of units and buildings as well as upgrades, etc.
 * 
 * @author P H - 26.04.2017
 *
 */
public interface CommandSender {

	public void buildUnit(UnitType unitType);

	public void buildBuilding(UnitType unitType);

	public void researchTech(TechType techType);

	public void buildAddon(UnitType addon);

	public void buildUpgrade(UpgradeType upgradeType);
}
