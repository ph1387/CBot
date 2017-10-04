package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.ScoreGeneratorGradualChangeMaxReset;
import buildingOrderModule.scoringDirector.gameState.GameState;
import core.Core;

/**
 * ScoreGeneratorRefinery.java --- A {@link ScoreGenerator} focused on
 * refineries. Increases it's score until the number of refineries changes.
 * 
 * @author P H - 16.09.2017
 *
 */
public class ScoreGeneratorRefinery extends ScoreGeneratorGradualChangeMaxReset {

	private static double DefaultRate = 0.1;
	private static double DefaultFrameDiff = 200;
	private static double DefaultResetValue = 0.;

	// The number of centers built by the Bot.
	private int centerCountPrev = 0;
	// The number of refineries build by the Bot.
	private int refineryCountPrev = 0;

	public ScoreGeneratorRefinery(BuildActionManager manager) {
		super(manager, DefaultRate, DefaultFrameDiff, DefaultResetValue);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(GameState gameState) {
		boolean reset = false;

		// Extract the number of centers and refineries from the storage.
		Integer centerCountCurrent = this.manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
		Integer refineryCountCurrent = this.manager.getCurrentGameInformation().getCurrentUnitCounts()
				.get(Core.getInstance().getPlayer().getRace().getRefinery());

		if (refineryCountCurrent != null) {
			// This is necessary since reseting is only required when the
			// previously stored center count is higher than the count of
			// refineries (= A refinery could be built) and the count of
			// refineries is now equal to the number of centers (= Each center
			// has a refinery).
			if (this.centerCountPrev > this.refineryCountPrev && refineryCountCurrent.equals(this.centerCountPrev)) {
				reset = true;
			}

			// Also reset the score if the number of current refineries is
			// smaller than the previously stored one since this means that a
			// refinery was destroyed. Therefore wait a moment before trying to
			// construct a new one at maybe the same position (Enemies might be
			// near and do not yet have destroyed the center).
			if (this.refineryCountPrev < refineryCountCurrent.intValue()) {
				reset = true;
			}

			this.refineryCountPrev = refineryCountCurrent.intValue();
		}

		if (centerCountCurrent != null) {
			this.centerCountPrev = centerCountCurrent.intValue();
		}

		return reset;
	}

	@Override
	protected boolean isThresholdReached(double score) {
		// Increase the score when the number of centers is larger than the
		// number of refineries since this means that there are free geysers on
		// which refineries can be built (Inverted since the function returns
		// true when no more changes may be applied).
		return this.refineryCountPrev >= this.centerCountPrev;
	}

}
