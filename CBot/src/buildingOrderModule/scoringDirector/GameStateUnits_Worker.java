package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Worker.java --- A GameState focused on training worker Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Worker extends GameState {

	private double generalMultiplier = 4.;

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		double centers = (double) (currentUnits.get(Core.getInstance().getPlayer().getRace().getCenter()));
		double workers = (double) (currentUnits.get(Core.getInstance().getPlayer().getRace().getWorker()));

		// TODO: WIP REMOVE
		System.out.println("GameState WorkerUnits: " + (this.generalMultiplier * (centers / workers)));
		
		return this.generalMultiplier * (centers / workers);
	}

}
