package informationStorage;

import bwapi.Player;
import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

/**
 * ResourceReserver.java --- Class that holds any reserved minerals or gases.
 * 
 * @author P H - 07.04.2017
 *
 */
public class ResourceReserver {

	private int reservedMinerals = 0;
	private int reservedGas = 0;

	public ResourceReserver() {

	}

	// -------------------- Functions

	/**
	 * Function to determined if the Player can afford the construction of the
	 * UniType.
	 * 
	 * @param desiredUnitType
	 *            the UnitType that is going to be build.
	 * @return true or false depending if the UnitType can be afforded.
	 */
	public boolean canAffordConstruction(UnitType desiredUnitType) {
		return this.canAffortResources(desiredUnitType.mineralPrice(), desiredUnitType.gasPrice());
	}

	/**
	 * Function to determined if the Player can afford the construction of the
	 * UpgradeType.
	 * 
	 * @param desiredUpgradeType
	 *            the UpgradeType that is going to be build.
	 * @return true or false depending if the UpgradeType can be afforded.
	 */
	public boolean canAffordConstruction(UpgradeType desiredUpgradeType) {
		return this.canAffortResources(desiredUpgradeType.mineralPrice(), desiredUpgradeType.gasPrice());
	}

	/**
	 * Function to determined if the Player can afford the research of the
	 * TechType.
	 * 
	 * @param desiredTechType
	 *            the TechType that is going to be build.
	 * @return true or false depending if the TechType can be afforded.
	 */
	public boolean canAffordConstruction(TechType desiredTechType) {
		return this.canAffortResources(desiredTechType.mineralPrice(), desiredTechType.gasPrice());
	}

	/**
	 * General function to determine if the Player can afford a certain amount
	 * of minerals / gas.
	 * 
	 * @param mineralPrice
	 *            the minerals which are going to be needed.
	 * @param gasPrice
	 *            the gas that is going to be needed.
	 * @return true or false depending if the Player can afford both prices.
	 */
	public boolean canAffortResources(int mineralPrice, int gasPrice) {
		Player player = Core.getInstance().getPlayer();
		boolean canAffordCost = player.minerals() >= mineralPrice && player.gas() >= gasPrice;
		boolean mineralsNotReserved = player.minerals() - this.reservedMinerals >= mineralPrice;
		boolean gasNotReserved = player.gas() - this.reservedGas >= gasPrice;

		return canAffordCost && mineralsNotReserved && gasNotReserved;
	}

	/**
	 * Function for reserving minerals.
	 * 
	 * @param amount
	 *            the amount of minerals that is going to be reserved.
	 */
	public void reserveMinerals(int amount) {
		if (amount >= 0) {
			this.reservedMinerals += amount;
		}
	}

	/**
	 * Function for reserving gas.
	 * 
	 * @param amount
	 *            the amount of gas that is going to be reserved.
	 */
	public void reserveGas(int amount) {
		if (amount >= 0) {
			this.reservedMinerals += amount;
		}
	}

	/**
	 * Function for enabling minerals again.
	 * 
	 * @param amount
	 *            the amount of minerals that is going to be enabled again.
	 */
	public void freeMinerals(int amount) {
		if (amount >= 0) {
			this.reservedMinerals -= amount;
		}
	}

	/**
	 * Function for enabling gas again.
	 * 
	 * @param amount
	 *            the amount of gas that is going to be enabled again.
	 */
	public void freeGas(int amount) {
		if (amount >= 0) {
			this.reservedGas -= amount;
		}
	}

	// ------------------------------ Getter / Setter

	public int getReservedMinerals() {
		return this.reservedMinerals;
	}

	public int getReservedGas() {
		return this.reservedGas;
	}
}
