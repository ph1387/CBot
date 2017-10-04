package buildingOrderModule.scoringDirector.ScoreGenerator.specific;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGenerator;
import buildingOrderModule.scoringDirector.ScoreGenerator.ScoreGeneratorDefault;
import buildingOrderModule.scoringDirector.ScoreGenerator.gradualChange.ScoreGeneratorGradualChange;
import buildingOrderModule.scoringDirector.gameState.GameState;

// TODO: UML ADD
/**
 * ScoreGeneratorSpecific.java --- A {@link ScoreGenerator} applying a target
 * specific rate to the score. This can either be a fixed or a changing one
 * depending on the function of the specific subclass. <b>Note:</b> <br>
 * Instances of this class used in for generating scores <b>must</b> be saved!
 * This is due to them sometimes relying on {@link ScoreGeneratorGradualChange}
 * instances, which in return rely on a continuous saved internal state.
 * 
 * @author P H - 03.10.2017
 *
 */
public abstract class ScoreGeneratorSpecific extends ScoreGeneratorDefault {

	public ScoreGeneratorSpecific(BuildActionManager manager) {
		super(manager);
	}

	// -------------------- Functions

	@Override
	public double generateScore(GameState gameState, int framesPassed) {
		double score = 0.;

		try {
			score = this.generateScoreForGameState(gameState, framesPassed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return score;
	}

	// TODO: UML ADD
	/**
	 * Function for generating a score based on a given {@link GameState}. This
	 * function exists since the superclass implementation does not take
	 * Exceptions into consideration. Some of the subclasses might implement a
	 * functionality that require the function to throw an exception and
	 * therefore returning 0 as a value.
	 * 
	 * @param gameState
	 *            the {@link GameState} whose score will be generated.
	 * @param framesPassed
	 *            the frames that have passed since the last time the score was
	 *            generated.
	 * @return a score for the given {@link GameState} utilizing the frames that
	 *         passed since the last iteration.
	 */
	protected abstract double generateScoreForGameState(GameState gameState, int framesPassed) throws Exception;

	// TODO: UML ADD
	@Override
	public int generateDivider(GameState gameState, int framesPassed) {
		int divider = 1;

		try {
			divider = this.generateDividerForGameState(gameState, framesPassed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return divider;
	}

	// TODO: UML ADD
	/**
	 * Function for generating a divider based on a given {@link GameState}.
	 * This function should return 1 for all GameStates that are not forbidden.
	 * 
	 * @param gameState
	 *            the {@link GameState} whose score will be generated.
	 * @param framesPassed
	 *            the frames that have passed since the last time the score was
	 *            generated.
	 * @return a divider for the given {@link GameState} utilizing the frames
	 *         that passed since the last iteration.
	 */
	protected abstract int generateDividerForGameState(GameState gameState, int framesPassed) throws Exception;

	// ------------------------------ Getter / Setter
}
