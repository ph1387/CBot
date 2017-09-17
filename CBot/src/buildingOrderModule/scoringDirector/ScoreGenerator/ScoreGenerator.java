package buildingOrderModule.scoringDirector.ScoreGenerator;

import buildingOrderModule.scoringDirector.ScoreGeneratorFactory;
import buildingOrderModule.scoringDirector.gameState.GameState;

/**
 * ScoreGenerator.java --- A Interface for Classes representing a
 * ScoreGenerator. These Classes can be used with the
 * {@link ScoreGeneratorFactory} (Abstract Factory Pattern).
 * 
 * @author P H - 16.07.2017
 *
 */
public interface ScoreGenerator {

	/**
	 * Function for generating a score based on a given {@link GameState}.
	 * 
	 * @param gameState
	 *            the {@link GameState} whose score will be generated.
	 * @param framesPassed
	 *            the frames that have passed since the last time the score was
	 *            generated.
	 * @return a score for the given {@link GameState} utilizing the frames that
	 *         passed since the last iteration.
	 */
	public double generateScore(GameState gameState, int framesPassed);

	/**
	 * Function for generating a divider based on a given {@link GameState}.
	 * 
	 * @param gameState
	 *            the {@link GameState} whose score will be generated.
	 * @param framesPassed
	 *            the frames that have passed since the last time the score was
	 *            generated.
	 * @return a divider for the given {@link GameState} utilizing the frames
	 *         that passed since the last iteration.
	 */
	public int generateDivider(GameState gameState, int framesPassed);

}
