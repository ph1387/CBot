package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Cheap.java --- A GameState focused on training / building
 * cheap Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Cheap extends GameStateGradualChange {

	// The score of the starting iteration. Initialized with 1 since the Bot
	// should start with cheap Units.
	private static double ScoreStarting = 1.;
	// The rate at which the score will change. The rate is is then applied for
	// each X frames that passed since the last iteration.
	private static double Rate = -0.1;
	// The frames after the rate is applied.
	private static double FrameDiff = 2000;

	// The minimum score that can be returned.
	private double scoreMin = 0.5;

	public GameStateUnits_Cheap() {
		super(GameStateUnits_Cheap.ScoreStarting, GameStateUnits_Cheap.Rate, GameStateUnits_Cheap.FrameDiff);
	}

	// -------------------- Functions

	@Override
	protected boolean canIterationRateApply(double score) {
		return score > this.scoreMin;
	}
	
	// TODO: WIP REMOVE
	@Override
	protected double generateScore(ScoringDirector scoringDirector, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		double value = super.generateScore(scoringDirector, currentWorkerPercent, currentBuildingsPercent, currentCombatUnitsPercent, currentUnits, currentTechs, currentUpgrades);
			
		// TODO: WIP REMOVE
		System.out.println("GameState Cheap: " + value);
	
		return value;
	}

}
