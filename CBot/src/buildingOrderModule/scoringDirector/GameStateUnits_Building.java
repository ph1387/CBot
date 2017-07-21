package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Building.java --- A GameState focused on buildings.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Building extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		double score = scoringDirector.defineDesiredBuildingsPercent()
				- (currentBuildingsPercent - scoringDirector.defineDesiredBuildingsPercent());

		// TODO: WIP REMOVE
		System.out.println("GameState Buildings: " + score);

		return score;
	}

}
