package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
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
	protected boolean shouldReset(ScoringDirector scoringDirector, BuildActionManager manager) {
		int centerCountCurrent = manager.getCurrentGameInformation().getCurrentUnits()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
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

}
