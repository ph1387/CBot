package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Mineral.java --- A GameState focused on mineral requiring
 * Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Mineral extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(double desiredWorkerPercent, double desiredBuildingsPercent,
			double desiredCombatUnitsPercent, HashSet<TechType> desiredTechs,
			HashMap<UpgradeType, Integer> desiredUpgrades, double currentWorkerPercent, double currentBuildingsPercent,
			double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits, HashSet<TechType> currentTechs,
			HashMap<UpgradeType, Integer> currentUpgrades) {
		double minerals = Core.getInstance().getPlayer().minerals();
		double gas = Core.getInstance().getPlayer().gas();
		double totalResources = minerals + gas;
		
		// TODO: WIP REMOVE
		System.out.println("GameState Minerals: " + minerals / totalResources);
		
		return minerals / totalResources;
	}

}
