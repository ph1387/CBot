package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Bio.java --- A GameState focused on bio Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Bio extends GameState {

	private double value = 1.0;
	
	// -------------------- Functions

	@Override
	protected double generateScore(double desiredBuildingsPercent, double desiredCombatUnitsPercent,
			HashSet<TechType> desiredTechs, HashMap<UpgradeType, Integer> desiredUpgrades, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		
		// TODO: WIP REMOVE
		System.out.println("GameState BioUnits: " + this.value);
		
		return this.value;
	}

}
