package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorGradualChangeMaxReset.java --- A {@link ScoreGenerator}
 * applying a constant rate to an existing score until a threshold is reached.
 * Also this Class implements a reset feature. If the function
 * {@link #shouldReset(GameState)} returns true, the score is reset to the
 * initially defined value.
 * 
 * @author P H - 15.09.2017
 *
 */
public abstract class ScoreGeneratorGradualChangeMaxReset extends ScoreGeneratorGradualChangeMax {

	private double resetValue;

	public ScoreGeneratorGradualChangeMaxReset(BuildActionManager manager, double rate, double frameDiff,
			double resetValue) {
		super(manager, rate, frameDiff);

		this.resetValue = resetValue;
	}

	// -------------------- Functions

	/**
	 * Function for determining if the score should be reset to the initially
	 * defined score.
	 * 
	 * @param gameState
	 *            the {@link GameState} which the generator currently operates
	 *            on and which contains the current score.
	 * @return true if the score should be reset to the initially defined score
	 *         or false if not.
	 */
	protected abstract boolean shouldReset(GameState gameState);

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		double score;

		if (this.shouldReset(gameState)) {
			score = this.resetValue;
		} else {
			score = super.generateScore(gameState, framesPassed);
		}
		return score;
	}
}
