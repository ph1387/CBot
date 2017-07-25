package informationStorage;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD
/**
 * CurrentGameInformation.java --- Storage class for all current information of
 * the game.
 * 
 * @author P H - 22.07.2017
 *
 */
public class CurrentGameInformation {

	private int currentUnitCountTotal = 0;
	private int currentWorkerCount = 0;
	private int currentBuildingCount = 0;
	private int currentCombatUnitCount = 0;

	private double currentWorkerPercent = 0.;
	private double currentBuildingsPercent = 0.;
	private double currentCombatUnitsPercent = 0.;

	private HashMap<UnitType, Integer> currentUnits = new HashMap<>();
	private HashSet<TechType> currentTechs = new HashSet<>();
	private HashMap<UpgradeType, Integer> currentUpgrades = new HashMap<>();

	public CurrentGameInformation() {

	}

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
	public CurrentGameInformation(int currentWorkerCount, int currentBuildingCount, int currentCombatUnitCount,
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

	public int getCurrentUnitCountTotal() {
		return currentUnitCountTotal;
	}

	public void setCurrentUnitCountTotal(int currentUnitCountTotal) {
		this.currentUnitCountTotal = currentUnitCountTotal;
	}

	public int getCurrentWorkerCount() {
		return currentWorkerCount;
	}

	public void setCurrentWorkerCount(int currentWorkerCount) {
		this.currentWorkerCount = currentWorkerCount;
	}

	public int getCurrentBuildingCount() {
		return currentBuildingCount;
	}

	public void setCurrentBuildingCount(int currentBuildingCount) {
		this.currentBuildingCount = currentBuildingCount;
	}

	public int getCurrentCombatUnitCount() {
		return currentCombatUnitCount;
	}

	public void setCurrentCombatUnitCount(int currentCombatUnitCount) {
		this.currentCombatUnitCount = currentCombatUnitCount;
	}

	public double getCurrentWorkerPercent() {
		return currentWorkerPercent;
	}

	public void setCurrentWorkerPercent(double currentWorkerPercent) {
		this.currentWorkerPercent = currentWorkerPercent;
	}

	public double getCurrentBuildingsPercent() {
		return currentBuildingsPercent;
	}

	public void setCurrentBuildingsPercent(double currentBuildingsPercent) {
		this.currentBuildingsPercent = currentBuildingsPercent;
	}

	public double getCurrentCombatUnitsPercent() {
		return currentCombatUnitsPercent;
	}

	public void setCurrentCombatUnitsPercent(double currentCombatUnitsPercent) {
		this.currentCombatUnitsPercent = currentCombatUnitsPercent;
	}

	public HashMap<UnitType, Integer> getCurrentUnits() {
		return currentUnits;
	}

	public void setCurrentUnits(HashMap<UnitType, Integer> currentUnits) {
		this.currentUnits = currentUnits;
	}

	public HashSet<TechType> getCurrentTechs() {
		return currentTechs;
	}

	public void setCurrentTechs(HashSet<TechType> currentTechs) {
		this.currentTechs = currentTechs;
	}

	public HashMap<UpgradeType, Integer> getCurrentUpgrades() {
		return currentUpgrades;
	}

	public void setCurrentUpgrades(HashMap<UpgradeType, Integer> currentUpgrades) {
		this.currentUpgrades = currentUpgrades;
	}

}
