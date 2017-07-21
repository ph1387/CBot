package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateUnits_Cheap.java --- A GameState focused on training / building
 * cheap Units.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateUnits_Cheap extends GameState {

	// The score of the previous iteration. Initialized with 1 since the Bot
	// should start with cheap Units.
	private double scorePrev = 1.;
	// The minimum score that can be returned.
	private double scoreMin = 0.5;
	// The rate at which the score will change. The rate is is then applied for
	// each X frames that passed since the last iteration.
	private double rate = -0.1;
	// The frames after the rate is applied.
	private double frameDiff = 2000;
	// The time stamp of the last time the rate was applied to the score.
	private int timeStampLastScoringChange = 0;

	// -------------------- Functions

	@Override
	protected double generateScore(double desiredWorkerPercent, double desiredBuildingsPercent,
			double desiredCombatUnitsPercent, HashSet<TechType> desiredTechs,
			HashMap<UpgradeType, Integer> desiredUpgrades, double currentWorkerPercent, double currentBuildingsPercent,
			double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits, HashSet<TechType> currentTechs,
			HashMap<UpgradeType, Integer> currentUpgrades) {
		int currentTimeStamp = Core.getInstance().getGame().getFrameCount();
		// The number of times the rate is applied to the score.
		int iterations = (int) (((double) (currentTimeStamp - this.timeStampLastScoringChange)) / this.frameDiff);

		// At least one single iteration (Update using the rate) must be
		// performed for the time stamp to change.
		if (iterations > 0) {
			for (int i = 0; i < iterations && this.scorePrev > this.scoreMin; i++) {
				this.scorePrev += this.rate;
			}

			this.timeStampLastScoringChange = currentTimeStamp;
		}

		// TODO: WIP REMOVE
		System.out.println("GameState Cheap: " + this.scorePrev);

		return this.scorePrev;
	}

}
