package unitControlModule;

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

	private static int reservedMinerals = 0;
	private static int reservedGas = 0;

	// -------------------- Functions

	/**
	 * Function to determined if the Player can afford the construction of the
	 * UniType.
	 * 
	 * @param desiredUnitType
	 *            the UnitType that is going to be build.
	 * @return true or false depending if the UnitType can be afforded.
	 */
	public static boolean canAffordConstruction(UnitType desiredUnitType) {
		return canAffortResources(desiredUnitType.mineralPrice(), desiredUnitType.gasPrice());
	}

	/**
	 * Function to determined if the Player can afford the construction of the
	 * UpgradeType.
	 * 
	 * @param desiredUpgradeType
	 *            the UpgradeType that is going to be build.
	 * @return true or false depending if the UpgradeType can be afforded.
	 */
	public static boolean canAffordConstruction(UpgradeType desiredUpgradeType) {
		return canAffortResources(desiredUpgradeType.mineralPrice(), desiredUpgradeType.gasPrice());
	}

	/**
	 * Function to determined if the Player can afford the research of the
	 * TechType.
	 * 
	 * @param desiredTechType
	 *            the TechType that is going to be build.
	 * @return true or false depending if the TechType can be afforded.
	 */
	public static boolean canAffordConstruction(TechType desiredTechType) {
		return canAffortResources(desiredTechType.mineralPrice(), desiredTechType.gasPrice());
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
	public static boolean canAffortResources(int mineralPrice, int gasPrice) {
		Player player = Core.getInstance().getPlayer();
		boolean canAffordCost = player.minerals() >= mineralPrice && player.gas() >= gasPrice;
		boolean mineralsNotReserved = player.minerals() - ResourceReserver.reservedMinerals >= mineralPrice;
		boolean gasNotReserved = player.gas() - ResourceReserver.reservedGas >= gasPrice;

		return canAffordCost && mineralsNotReserved && gasNotReserved;
	}

	/**
	 * Function for reserving minerals.
	 * 
	 * @param amount
	 *            the amount of minerals that is going to be reserved.
	 */
	public static void reserveMinerals(int amount) {
		if (amount >= 0) {
			reservedMinerals += amount;
		}
	}

	/**
	 * Function for reserving gas.
	 * 
	 * @param amount
	 *            the amount of gas that is going to be reserved.
	 */
	public static void reserveGas(int amount) {
		if (amount >= 0) {
			reservedMinerals += amount;
		}
	}

	/**
	 * Function for enabling minerals again.
	 * 
	 * @param amount
	 *            the amount of minerals that is going to be enabled again.
	 */
	public static void freeMinerals(int amount) {
		if (amount >= 0) {
			reservedMinerals -= amount;
		}
	}

	/**
	 * Function for enabling gas again.
	 * 
	 * @param amount
	 *            the amount of gas that is going to be enabled again.
	 */
	public static void freeGas(int amount) {
		if (amount >= 0) {
			reservedGas -= amount;
		}
	}

	// ------------------------------ Getter / Setter

	public static int getReservedMinerals() {
		return reservedMinerals;
	}

	public static int getReservedGas() {
		return reservedGas;
	}
}
