package buildingOrderModule.scoringDirector;

import java.util.HashSet;

import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;

// TODO: UML ADD
/**
 * ScoringDirector.java --- A director that updates the scores of provided
 * ScoringActions based on the current state of the game. Different Actions are
 * based on different types of {@link GameState}s whose scores are directly
 * connected to the game and predefined goals. The score of all
 * {@link GameState}s combined divided by the number of states used is the final
 * score of a ScoringAction. </br>
 * <b>Note:</b></br>
 * The ScoringDirector is used for changing the scores of the Actions used in
 * the {@link ActionQueueSimulationResults}.
 * 
 * @author P H - 16.07.2017
 *
 */
public class ScoringDirector {

	// -------------------- Functions

	/**
	 * Function for updating the score of a provided HashSet of ScoringActions.
	 * 
	 * @param updatableActions
	 *            the actions whose scores are going to be updated.
	 */
	public void update(HashSet<ScoringAction> updatableActions) {
		// Update the extracted GameStates
		this.updateGameStates(this.extractUsedGameStates(updatableActions));

		// Update the scores of the actions.
		this.updateScoringActionScores(updatableActions);
	}

	/**
	 * Function for extracting the used GameStates from a HashSet of
	 * ScoringActions.
	 * 
	 * @param scoringActions
	 *            the actions whose GameStates are being extracted.
	 * @return a HashSet of GameStates that the provided ScoringActions use.
	 */
	private HashSet<GameState> extractUsedGameStates(HashSet<ScoringAction> scoringActions) {
		HashSet<GameState> usedGameStates = new HashSet<>();

		for (ScoringAction scoringAction : scoringActions) {
			usedGameStates.addAll(scoringAction.defineUsedGameStates());
		}

		return usedGameStates;
	}

	/**
	 * Function for updating a HashSet of GameStates based on the current state
	 * of the game. The multipliers of the provided GameStates are set
	 * accordingly to the different areas they belong to. These GameStates can
	 * then be used for generating scores in a simulation.
	 * 
	 * @param usedGameStates
	 *            the HashSet of GameStates that are going to be updated.
	 */
	private void updateGameStates(HashSet<GameState> usedGameStates) {
		for (GameState gameState : usedGameStates) {
			gameState.updateMultiplier();
		}
	}

	/**
	 * Function for updating the scores of the given ScoringActions.
	 * 
	 * @param updatableActions
	 *            the ScoringActions whose scores are being updated.
	 */
	private void updateScoringActionScores(HashSet<ScoringAction> updatableActions) {
		for (ScoringAction scoringAction : updatableActions) {
			double gameStateCount = scoringAction.defineUsedGameStates().size();
			double gameStateSum = 0.;

			// Prevent errors by dividing by 0.
			if (gameStateCount == 0.) {
				gameStateCount = 1;
			}

			// Get the sum of all used GameStates of the action.
			for (GameState gameState : scoringAction.defineUsedGameStates()) {
				gameStateSum += gameState.getCurrentMultiplier();
			}

			// Divide the total sum by the number of GameStates added together
			// to calculate the average value and set the score of the action to
			// this value.
			scoringAction.setScore((int) (gameStateSum / gameStateCount));
		}
	}

	// ------------------------------ Getter / Setter

}
