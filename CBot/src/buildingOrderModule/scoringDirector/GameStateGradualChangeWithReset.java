package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * GameStateGradualChangeWithReset.java --- A Superclass for GameStates using a
 * gradual system for generating their scores while also reseting the score to a
 * predefined base value when a certain condition is met. The score increases in
 * defined intervals by a specified amount until a threshold is reached or a
 * reset is being performed. This sort of GameState if mainly used for a certain
 * few states which rely on a execution sometime in the future but eventually
 * HAVE to be performed. Therefore the score (continuously) increases /
 * decreases.
 * 
 * @author P H - 22.07.2017
 *
 */
abstract class GameStateGradualChangeWithReset extends GameStateGradualChange {

	// The starting value to which the generated score is reseted to.
	private double scoreStart;

	public GameStateGradualChangeWithReset(double scoreStart, double rate, double frameDiff) {
		super(scoreStart, rate, frameDiff);

		this.scoreStart = scoreStart;
	}

	// -------------------- Functions

	@Override
	protected double generateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		// Reset the score to the previously specified starting score if the
		// function returns true.
		if (this.shouldReset(scoringDirector, manager)) {
			this.scorePrev = scoreStart;
		}

		// Apply the rate towards the score if the threshold is not
		// yet reached.
		if (!this.isTresholdReached(this.scorePrev)) {
			super.generateScore(scoringDirector, manager);
		}

		return this.scorePrev;
	}

	@Override
	protected boolean canIterationRateApply(double score) {
		// The change can always be applied by default. Can be overridden!
		return true;
	}

	/**
	 * Function for testing if the score should be reseted to the previously
	 * provided starting score.
	 * 
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for determining
	 *            if a reset of the current score is necessary.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 * @return true if the score should be reseted to the starting score and
	 *         false if the score should not be reseted.
	 */
	protected abstract boolean shouldReset(ScoringDirector scoringDirector, BuildActionManager manager);

	/**
	 * Function for determining if a certain score threshold is being reached
	 * and the increasing / decreasing of the score should stop.
	 * 
	 * @param score
	 *            the current score of the GameState (Changes are not yet
	 *            applied).
	 * @return true if the threshold is reached and no further changes should be
	 *         applied to it. False if the threshold is not yet reached and
	 *         score changes may be applied.
	 */
	protected abstract boolean isTresholdReached(double score);

}
