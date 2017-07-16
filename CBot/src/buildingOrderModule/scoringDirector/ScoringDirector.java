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

	public void update(HashSet<ScoringAction> updatableActions) {
		
	}
	
	private void updateGameStates(HashSet<GameState> usedGameStates) {
		
	}
	
	// ------------------------------ Getter / Setter

}
