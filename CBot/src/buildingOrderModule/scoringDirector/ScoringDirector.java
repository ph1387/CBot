package buildingOrderModule.scoringDirector;

import java.util.HashSet;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.stateFactories.actions.AvailableActionsSimulationQueue;
import buildingOrderModule.stateFactories.actions.executableActions.actionQueues.ActionQueueSimulationResults;
import buildingOrderModule.stateFactories.updater.ActionUpdaterSimulationQueue;

// TODO: UML ADD
/**
 * ScoringDirector.java --- A director that updates the scores of provided
 * ScoringActions based on the current state of the game in comparison with
 * goals that each subclass of the ScoringDirector defines. Different Actions
 * are based on different types of {@link GameState}s whose scores are directly
 * connected to the game and predefined goals. The score of all
 * {@link GameState}s combined divided by the number of states used is the final
 * score of a ScoringAction. </br>
 * <b>Note:</b></br>
 * The ScoringDirector is used for changing the scores of the Actions used in
 * the {@link ActionQueueSimulationResults}. Therefore any subclass must be
 * carefully chosen due to them defining the desired Unit percentages and the
 * technologies and upgrades that count towards the scores. Also since the
 * Updater ({@link ActionUpdaterSimulationQueue}) for the specific
 * {@link AvailableActionsSimulationQueue} defines the actual abilities of the
 * manager, these goals that are defined here should overlap with the actual
 * possible actions of the manager.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class ScoringDirector {

	// Base multiplier which all Actions use to generate their score and ensure
	// better / more results (No values below 1.0 are discarded).
	private double basePointMultiplier = 10.;

	// -------------------- Functions

	/**
	 * Function for updating the score of a provided HashSet of ScoringActions.
	 * 
	 * @param updatableActions
	 *            the actions whose scores are going to be updated.
	 * @param manager
	 *            the BuildActionManager whose GameStates are going to be
	 *            updated.
	 */
	public void update(HashSet<ScoringAction> updatableActions, BuildActionManager manager) {
		// Update the extracted GameStates
		this.updateGameStates(this.extractUsedGameStates(updatableActions), manager);

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
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 */
	private void updateGameStates(HashSet<GameState> usedGameStates, BuildActionManager manager) {
		// Using these information update all GameStates that are being
		// provided.
		for (GameState gameState : usedGameStates) {
			gameState.updateScore(this, manager);
		}
	}

	/**
	 * Function for specifying the desired percentage of buildings that the Bot
	 * should have. </br>
	 * <b>Note:</b></br>
	 * Should not be larger than 0.5 to prevent the maximum score from exceeding
	 * 1.0.
	 * 
	 * @return the percentage of buildings the Bot should try to keep.
	 */
	protected abstract double defineDesiredBuildingsPercent();

	/**
	 * Function for specifying the desired percentage of combat Units that the
	 * Bot should have. </br>
	 * <b>Note:</b></br>
	 * Should not be larger than 0.5 to prevent the maximum score from exceeding
	 * 1.0.
	 * 
	 * @return the percentage of combat Units the Bot should try to keep.
	 */
	protected abstract double defineDesiredCombatUnitsPercent();

	/**
	 * Function for defining the fixed score for Bio-Units.
	 * 
	 * @return the score for Bio-Units.
	 */
	protected abstract double defineFixedScoreUnitsBio();

	/**
	 * Function for defining the fixed score for flying Units.
	 * 
	 * @return the score for flying Units.
	 */
	protected abstract double defineFixedScoreUnitsFlying();

	/**
	 * Function for defining the fixed score for healer Units.
	 * 
	 * @return the score for healer Units.
	 */
	protected abstract double defineFixedScoreUnitsHealer();

	/**
	 * Function for defining the fixed score for support Units.
	 * 
	 * @return the score for support Units.
	 */
	protected abstract double defineFixedScoreUnitsSupport();

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
				gameStateSum += gameState.getCurrentScore();
			}

			// Divide the total sum by the number of GameStates added together
			// to calculate the average value and set the score of the action to
			// this value. The base multiplier is added to ensure that no scores
			// below 1.0 are missing (Casted to int!).
			scoringAction.setScore((int) ((double) (scoringAction.defineMineralCost() + scoringAction.defineGasCost())
					* (gameStateSum / gameStateCount) * this.basePointMultiplier));
		}
	}

	// ------------------------------ Getter / Setter

}
