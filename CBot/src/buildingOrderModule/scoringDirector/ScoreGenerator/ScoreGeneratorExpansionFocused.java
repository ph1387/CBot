package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;
import core.Core;

/**
 * ScoreGeneratorExpansionFocused.java --- A {@link ScoreGenerator} focused on
 * expansion. Increases it's score until the number of centers changes.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorExpansionFocused extends ScoreGeneratorGradualChangeMaxReset {

	private static double DefaultRate = 0.1;
	private static double DefaultFrameDiff = 600;
	private static double DefaultResetValue = 0.;

	// The number of centers built by the Bot.
	private int centerCountPrev = 0;

	public ScoreGeneratorExpansionFocused(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultResetValue);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(GameState gameState) {
		int centerCountCurrent = this.manager.getCurrentGameInformation().getCurrentUnitCounts()
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
	protected boolean isThresholdReached(double score) {
		// The threshold is never reached. The rate is constantly applied to the
		// score and therefore the Bot is bound to build centers.
		return false;
	}

}
