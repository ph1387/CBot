package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

// TODO: UML ADD
/**
 * GameStateGradualChange.java --- A Superclass for GameStates using a gradual system for generating their scores.
 * @author P H - 21.07.2017
 *
 */
public abstract class GameStateGradualChange extends GameState {
	
	// The time stamp of the last time the rate was applied to the score.
	private int timeStampLastScoringChange = 0;
	
	// The score of the previous iteration.
	private double scorePrev;
	// The rate at which the score will change. The rate is is then applied for
	// each X frames that passed since the last iteration.
	private double rate;
	// The frames after the rate is applied.
	private double frameDiff;
	
	/**
	 * @param scoreStart the starting score of the GameState.
	 * @param rate the rate that is apply to the starting score each iteration.
	 * @param frameDiff the frames that are being waited before a rate is being applied.
	 */
	public GameStateGradualChange(double scoreStart, double rate, double frameDiff) {
		this.scorePrev = scoreStart;
		this.rate = rate;
		this.frameDiff = frameDiff;
	}
	
	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		int currentTimeStamp = Core.getInstance().getGame().getFrameCount();
		// The number of times the rate is applied to the score.
		int iterations = (int) (((double) (currentTimeStamp - this.timeStampLastScoringChange)) / this.frameDiff);

		// At least one single iteration (Update using the rate) must be
		// performed for the time stamp to change.
		if (iterations > 0) {
			for (int i = 0; i < iterations && this.canIterationRateApply(this.scorePrev); i++) {
				this.scorePrev += this.rate;
			}

			this.timeStampLastScoringChange = currentTimeStamp;
		}

		return this.scorePrev;
	}
	
	/**
	 * Function for testing if the rate can be applied to the current score or if a specified breakpoint is being reached.
	 * @param score the score of the current iteration.
	 * @return true or false depending if the specified rate may be added towards the score or not.
	 */
	protected abstract boolean canIterationRateApply(double score);
}
