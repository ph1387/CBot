package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Support.java --- A GameState focused on support Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Support extends GameState {

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		
		// TODO: WIP REMOVE
		System.out.println("GameState SupportUnits: " + scoringDirector.defineFixedScoreUnitsSupport());
		
		return scoringDirector.defineFixedScoreUnitsSupport();
	}

}
