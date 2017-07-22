package buildingOrderModule.scoringDirector;

import java.util.HashMap;
import java.util.HashSet;

import bwapi.TechType;
import bwapi.UnitType;
import bwapi.UpgradeType;
import core.Core;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateFocused_Expansion.java --- A GameState focused on expansion.
 * 
 * @author P H - 16.07.2017
 *
 */
class GameStateFocused_Expansion extends GameStateGradualChangeWithReset {

	// The starting score of this GameState.
	private static double ScoreStart = 0.;
	// The rate that is applied to the score in each iteration.
	private static double Rate = 0.1;
	// The frames after the rate is applied.
	private static double FrameDiff = 200;

	// The number of centers built by the Bot.
	private int centerCountPrev = 0;

	public GameStateFocused_Expansion() {
		super(ScoreStart, Rate, FrameDiff);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(ScoringDirector scoringDirector, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		int centerCountCurrent = currentUnits.get(Core.getInstance().getPlayer().getRace().getCenter());
		boolean reset = false;

		// Reset the score after the previously stored number of centers does
		// not match the current number of centers. This includes either a lower
		// number (= A center was destroyed) or a higher one (= A center was
		// build).
		if (centerCountCurrent != this.centerCountPrev) {
			this.centerCountPrev = centerCountCurrent;
			reset = true;
		}

		return reset;
	}

	@Override
	protected boolean isTresholdReached(double score) {
		// The threshold is never reached. The rate is constantly applied to the
		// score and therefore the Bot is bound to build centers.
		return false;
	}
	
	// TODO: WIP REMOVE
	@Override
	protected double generateScore(ScoringDirector scoringDirector, double currentWorkerPercent,
			double currentBuildingsPercent, double currentCombatUnitsPercent, HashMap<UnitType, Integer> currentUnits,
			HashSet<TechType> currentTechs, HashMap<UpgradeType, Integer> currentUpgrades) {
		double value = super.generateScore(scoringDirector, currentWorkerPercent, currentBuildingsPercent, currentCombatUnitsPercent, currentUnits, currentTechs, currentUpgrades);
		
		// TODO: WIP REMOVE
		System.out.println("GameState ExpansionFocused: " + value);
		
		return value;
	}

}
