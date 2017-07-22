package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD NOT PULBIC
/**
 * GameStateCurrentInformation.java --- Storage class for all information that
 * are currently important for updating the scores.
 * 
 * @author P H - 22.07.2017
 *
 */
class GameStateCurrentInformation {

	private int currentWorkerCount;
	private int currentBuildingCount;
	private int currentCombatUnitCount;

	private double currentWorkerPercent;
	private double currentBuildingsPercent;
	private double currentCombatUnitsPercent;

	private HashMap<UnitType, Integer> currentUnits;
	private HashSet<TechType> currentTechs;
	private HashMap<UpgradeType, Integer> currentUpgrades;

	/**
	 * @param currentWorkerCount
	 *            the number of worker Units.
	 * @param currentBuildingCount
	 *            the number of buildings.
	 * @param currentCombatUnitCount
	 *            the number of combat Units.
	 * @param currentWorkerPercent
	 *            the actual current percentage of worker Units.
	 * @param currentBuildingsPercent
	 *            the actual current percentage of buildings.
	 * @param currentCombatUnitsPercent
	 *            the actual current percentage of combat Units.
	 * @param currentUnits
	 *            all current Units.
	 * @param currentTechs
	 *            all currently researched TechTypes of the desired ones.
	 * @param currentUpgrades
	 *            all currently performed UpgradeTypes of the desired ones.
	 */
	public GameStateCurrentInformation(int currentWorkerCount, int currentBuildingCount, int currentCombatUnitCount,
			double currentWorkerPercent, double currentBuildingsPercent, double currentCombatUnitsPercent,
			HashMap<UnitType, Integer> currentUnits, HashSet<TechType> currentTechs,
			HashMap<UpgradeType, Integer> currentUpgrades) {
		this.currentWorkerCount = currentWorkerCount;
		this.currentBuildingCount = currentBuildingCount;
		this.currentCombatUnitCount = currentCombatUnitCount;

		this.currentWorkerPercent = currentWorkerPercent;
		this.currentBuildingsPercent = currentBuildingsPercent;
		this.currentCombatUnitsPercent = currentCombatUnitsPercent;

		this.currentUnits = currentUnits;
		this.currentTechs = currentTechs;
		this.currentUpgrades = currentUpgrades;
	}

	// -------------------- Functions

	// ------------------------------ Getter / Setter

	public int getCurrentWorkerCount() {
		return currentWorkerCount;
	}

	public int getCurrentBuildingCount() {
		return currentBuildingCount;
	}

	public int getCurrentCombatUnitCount() {
		return currentCombatUnitCount;
	}

	public double getCurrentWorkerPercent() {
		return currentWorkerPercent;
	}

	public double getCurrentBuildingsPercent() {
		return currentBuildingsPercent;
	}

	public double getCurrentCombatUnitsPercent() {
		return currentCombatUnitsPercent;
	}

	public HashMap<UnitType, Integer> getCurrentUnits() {
		return currentUnits;
	}

	public HashSet<TechType> getCurrentTechs() {
		return currentTechs;
	}

	public HashMap<UpgradeType, Integer> getCurrentUpgrades() {
		return currentUpgrades;
	}

}
