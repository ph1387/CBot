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

	/**
	 * Function for starting the training of a Unit.
	 * 
	 * @param unitType
	 *            the UnitType that is going to be trained.
	 */
	public void buildUnit(UnitType unitType);

	/**
	 * Function for starting the construction of a building.
	 * 
	 * @param unitType
	 *            the UnitType of the building that is going to be constructed.
	 */
	public void buildBuilding(UnitType unitType);

	/**
	 * Function for starting the research of a specific technology.
	 * 
	 * @param techType
	 *            the TechType that is going to be researched.
	 */
	public void researchTech(TechType techType);

	/**
	 * Function for starting the construction of an addon.
	 * 
	 * @param addon
	 *            the UnitType, in form of an addon, that is going to be
	 *            constructed.
	 */
	public void buildAddon(UnitType addon);

	/**
	 * Function for starting an upgrade.
	 * 
	 * @param upgradeType
	 *            the UpgradeType that is going to be started.
	 */
	public void buildUpgrade(UpgradeType upgradeType);
}
