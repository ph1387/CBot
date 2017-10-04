package buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorDefault;
import buildingOrderModule.scoringDirector.gameState.GameState;

//TODO: UML PACKAGE
/**
 * ScoreGeneratorGradualChange.java --- A {@link ScoreGenerator} applying a
 * constant rate to an existing score. <br>
 * <b>Note:</b> <br>
 * Instances of this class used in for generating scores <b>must</b> be saved!
 * This is due to them relying on the {@link #extraFramesFromLastIteration}
 * field which keeps the score from not changing at all when the required frame
 * difference is not met. The frames, that are not used, are stored in this
 * reference and are applied to the next iteration!
 * 
 * @author P H - 15.09.2017
 *
 */
public abstract class ScoreGeneratorGradualChange extends ScoreGeneratorDefault {

	protected double rate;
	protected double frameDiff;

	private double extraFramesFromLastIteration = 0;

	public ScoreGeneratorGradualChange(BuildActionManager manager, double rate, double frameDiff) {
		super(manager);

		this.rate = rate;
		this.frameDiff = frameDiff;
	}

	// -------------------- Functions

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		// The number of times the rate is applied to the score.
		int iterations = (int) ((double) (framesPassed + this.extraFramesFromLastIteration) / this.frameDiff);
		double score = gameState.getCurrentScore();

		// Save the remaining frames for the next iteration. This ensures that a
		// new score is eventually generated.
		// I.e.:
		// First iteration: 290 frames passed. 300 needed.
		// Second iteration: 295 frames passed. 300 needed.
		// With saving the extra frames this function will trigger the second
		// time it is called (585 frames passed. 300 needed).
		// Without it no new score would be generated since both times the
		// needed frame count was not reached.
		this.extraFramesFromLastIteration = (framesPassed + this.extraFramesFromLastIteration) % this.frameDiff;

		for (int i = 0; i < iterations; i++) {
			score += this.rate;
		}
		return score;
	}

}
