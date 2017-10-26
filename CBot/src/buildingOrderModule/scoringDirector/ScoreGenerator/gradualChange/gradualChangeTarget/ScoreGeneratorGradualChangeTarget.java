package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.gradualChangeTarget;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.ScoreGeneratorGradualChangeMax;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorGradualChangeTarget.java --- A {@link ScoreGenerator} applying
 * a constant rate to an existing score targeting a specific value. The
 * {@link ScoreGenerator} changes the rate (-/+) based on the score being larger
 * or smaller than the target value.
 * 
 * @author P H - 16.09.2017
 *
 */
public abstract class ScoreGeneratorGradualChangeTarget extends ScoreGeneratorGradualChangeMax {

	private double targetValue;

	public ScoreGeneratorGradualChangeTarget(BuildActionManager manager, double rate, double frameDiff,
			double targetValue) {
		super(manager, rate, frameDiff);

		this.targetValue = targetValue;
	}

	// -------------------- Functions

	@Override
	protected boolean isThresholdReached(double score) {
		return Double.compare(this.targetValue, score) == 0;
	}

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		this.updateRate(gameState);

		return super.generateScore(gameState, framesPassed);
	}

	/**
	 * Function for updating the rate. The sign of the rate might change (-/+).
	 * 
	 * @param gameState
	 *            the {@link GameState} whose score will be compared with the
	 *            target value.
	 */
	private void updateRate(GameState gameState) {
		double difference = this.targetValue - gameState.getCurrentScore();

		// Both possibilities include a growing difference (One time positive,
		// the other time negative).
		if ((difference >= 0 && this.rate < 0) || (difference < 0 && this.rate > 0)) {
			this.rate *= -1.;
		}
	}

}
