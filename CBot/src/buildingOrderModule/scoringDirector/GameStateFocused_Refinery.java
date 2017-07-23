package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import core.Core;

// TODO: UML ADD NOT PUBLIC
/**
 * GameStateFocused_Refinery.java --- A GameState focused on the construction of
 * refineries since they are required at newly acquired base locations for
 * gathering gas.
 * 
 * @author P H - 23.07.2017
 *
 */
class GameStateFocused_Refinery extends GameStateGradualChangeWithReset {

	// The starting score of this GameState.
	private static double ScoreStart = 0.;
	// The rate that is applied to the score in each iteration.
	private static double Rate = 0.1;
	// The frames after the rate is applied.
	private static double FrameDiff = 200;

	// The number of centers built by the Bot.
	private int centerCountPrev = 0;
	// The number of refineries build by the Bot.
	private int refineryCountPrev = 0;

	public GameStateFocused_Refinery() {
		super(ScoreStart, Rate, FrameDiff);
	}

	// -------------------- Functions

	@Override
	protected boolean shouldReset(ScoringDirector scoringDirector, BuildActionManager manager) {
		boolean reset = false;

		// Extract the number of centers and refineries from the storage.
		Integer centerCountCurrent = manager.getCurrentGameInformation().getCurrentUnits()
				.get(Core.getInstance().getPlayer().getRace().getCenter());
		Integer refineryCountCurrent = manager.getCurrentGameInformation().getCurrentUnits()
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
	protected boolean isTresholdReached(double score) {
		// Increase the score when the number of centers is larger than the
		// number of refineries since this means that there are free geysers on
		// which refineries can be built (Inverted since the function returns
		// true when no more changes may be applied).
		return this.refineryCountPrev >= this.centerCountPrev;
	}

	// TODO: WIP REMOVE
	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		double value = super.generateScore(scoringDirector, manager);

		// TODO: WIP REMOVE
		System.out.println("GameState RefineryFocused: " + value);

		return value;
	}

}
