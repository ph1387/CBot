package informationStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import bwapi.TechType;
import bwapi.Unit;
import bwapi.UnitType;
import bwapi.UpgradeType;

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

	// TODO: UML ADD
	private int currentMineralGatherers = 0;
	// TODO: UML ADD
	private int currentGasGatherers = 0;
	// TODO: UML ADD
	private int currentConstructionWorkers = 0;

	private double currentWorkerPercent = 0.;
	private double currentBuildingsPercent = 0.;
	private double currentCombatUnitsPercent = 0.;

	private HashMap<UnitType, Integer> currentUnitCounts = new HashMap<>();
	private HashMap<UnitType, HashSet<Unit>> currentUnits = new HashMap<>();
	private HashSet<TechType> currentTechs = new HashSet<>();
	private HashMap<UpgradeType, Integer> currentUpgrades = new HashMap<>();

	public CurrentGameInformation() {

	}

	/**
	 * @param currentUnitCountTotal
	 *            the total number of Units the Bot controls.
	 * @param currentWorkerCount
	 *            the number of worker Units.
	 * @param currentBuildingCount
	 *            the number of buildings.
	 * @param currentCombatUnitCount
	 *            the number of combat Units.
	 * @param currentMineralGatherers
	 *            the number of mineral gathering worker Units.
	 * @param currentGasGatherers
	 *            the number of gas gathering worker Units.
	 * @param currentConstructionWorkers
	 *            the number of worker Units constructing buildings.
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
	public CurrentGameInformation(int currentUnitCountTotal, int currentWorkerCount, int currentBuildingCount,
			int currentCombatUnitCount, int currentMineralGatherers, int currentGasGatherers,
			int currentConstructionWorkers, double currentWorkerPercent, double currentBuildingsPercent,
			double currentCombatUnitsPercent, HashMap<UnitType, HashSet<Unit>> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		this.currentUnitCountTotal = currentUnitCountTotal;
		this.currentWorkerCount = currentWorkerCount;
		this.currentBuildingCount = currentBuildingCount;
		this.currentCombatUnitCount = currentCombatUnitCount;

		this.currentMineralGatherers = currentMineralGatherers;
		this.currentGasGatherers = currentGasGatherers;
		this.currentConstructionWorkers = currentConstructionWorkers;

		this.currentWorkerPercent = currentWorkerPercent;
		this.currentBuildingsPercent = currentBuildingsPercent;
		this.currentCombatUnitsPercent = currentCombatUnitsPercent;

		this.currentUnitCounts = this.generateCurrentUnitCounts(currentUnits);
		this.currentUnits = currentUnits;
		this.currentTechs = currentTechs;
		this.currentUpgrades = currentUpgrades;
	}

	// -------------------- Functions

	/**
	 * Internal function for transforming a HashMap containing references to all
	 * current Player Units to a HashMap containing the UnitTypes as keys and
	 * the number of Units currently associated with this type as value.
	 * 
	 * @param currentUnits
	 *            the currently active Units of the Player.
	 * @return a new HashMap containing UnitTypes as keys and the number of
	 *         Units of that type that the Player currently controls as values.
	 */
	private HashMap<UnitType, Integer> generateCurrentUnitCounts(HashMap<UnitType, HashSet<Unit>> currentUnits) {
		HashMap<UnitType, Integer> currentUnitCounts = new HashMap<>();
		Set<UnitType> keySet = currentUnits.keySet();

		// Transfer the number of individual Units into the new HashMap.
		for (UnitType unitType : keySet) {
			currentUnitCounts.put(unitType, currentUnits.get(unitType).size());
		}

		return currentUnitCounts;
	}

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
	
	// TODO: UML ADD
	public int getCurrentMineralGatherers() {
		return currentMineralGatherers;
	}

	// TODO: UML ADD
	public void setCurrentMineralGatherers(int currentMineralGatherers) {
		this.currentMineralGatherers = currentMineralGatherers;
	}

	// TODO: UML ADD
	public int getCurrentGasGatherers() {
		return currentGasGatherers;
	}

	// TODO: UML ADD
	public void setCurrentGasGatherers(int currentGasGatherers) {
		this.currentGasGatherers = currentGasGatherers;
	}

	// TODO: UML ADD
	public int getCurrentConstructionWorkers() {
		return currentConstructionWorkers;
	}

	// TODO: UML ADD
	public void setCurrentConstructionWorkers(int currentConstructionWorkers) {
		this.currentConstructionWorkers = currentConstructionWorkers;
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

	public HashMap<UnitType, Integer> getCurrentUnitCounts() {
		return currentUnitCounts;
	}

	public void setCurrentUnitCounts(HashMap<UnitType, Integer> currentUnits) {
		this.currentUnitCounts = currentUnits;
	}

	public HashMap<UnitType, HashSet<Unit>> getCurrentUnits() {
		return currentUnits;
	}

	public void setCurrentUnits(HashMap<UnitType, HashSet<Unit>> currentUnits) {
		this.currentUnits = currentUnits;
		this.currentUnitCounts = this.generateCurrentUnitCounts(currentUnits);
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
