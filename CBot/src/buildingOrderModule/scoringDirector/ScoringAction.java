package buildingOrderModule.scoringDirector;

import java.util.HashSet;

// TODO: UML ADD
/**
 * ScoringAction.java --- A Interface for all Actions that a
 * {@link ScoringDirector} will be able to update.
 * 
 * @author P H - 16.07.2017
 *
 */
public interface ScoringAction {

	/**
	 * Function for defining the GameStates that are used for generating the
	 * final score of the action.
	 * 
	 * @return a HashSet of GameStates that the Action relies on.
	 */
	public HashSet<GameState> defineUsedGameStates();

	/**
	 *
	 * @param score
	 *            the generated score by the ScoringDirector.
	 */
	public void setScore(int score);
}
