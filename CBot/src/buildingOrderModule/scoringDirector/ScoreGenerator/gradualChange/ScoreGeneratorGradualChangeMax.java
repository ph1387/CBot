package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorGradualChangeMax.java --- A {@link ScoreGenerator} applying a
 * constant rate to an existing score until a threshold is reached.
 * 
 * @author P H - 15.09.2017
 *
 */
public abstract class ScoreGeneratorGradualChangeMax extends ScoreGeneratorGradualChange {

	public ScoreGeneratorGradualChangeMax(BuildActionManager manager, double rate, double frameDiff) {
		super(manager, rate, frameDiff);
	}

	// -------------------- Functions

	/**
	 * Function defining if the threshold of the score is being reached. If it
	 * is the rate is no longer applied to it.
	 * 
	 * @param score
	 *            the current score of the {@link GameState} used.
	 * @return true if the threshold is reached and the rate should / must no
	 *         longer be added towards the score or false if the rate can still
	 *         be added towards it.
	 */
	protected abstract boolean isThresholdReached(double score);

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		double score = gameState.getCurrentScore();

		if (!this.isThresholdReached(score)) {
			score = super.generateScore(gameState, framesPassed);
		}
		return score;
	}
}
