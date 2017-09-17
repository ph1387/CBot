package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGeneratorProportion.java --- A {@link ScoreGenerator} based on returning
 * a score based on a generated numerator and denominator (=> Proportion!).
 * 
 * @author P H - 16.09.2017
 *
 */
public abstract class ScoreGeneratorProportion extends ScoreGeneratorDefault {

	private double defaultScore;

	public ScoreGeneratorProportion(BuildActionManager manager, double defaultScore) {
		super(manager);

		this.defaultScore = defaultScore;
	}

	// -------------------- Functions

	/**
	 * Function defining if a score can be generated. If not the default score
	 * is returned instead.
	 * 
	 * @return true if a score can be generated, false if not.
	 */
	protected abstract boolean canGenerateScore();

	/**
	 *
	 * @return the numerator that will be used in the score generation.
	 */
	protected abstract int defineNumerator();

	/**
	 *
	 * @return the denominator that will be used in the score generation.
	 */
	protected abstract int defineDenominator();

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		double score = this.defaultScore;

		if (this.canGenerateScore()) {
			score = (double) (this.defineNumerator()) / (double) (this.defineDenominator());
		}

		return score;
	}

}
