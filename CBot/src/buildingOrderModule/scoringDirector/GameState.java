package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;

/**
 * GameState.java --- Class for representing a state in the game with an
 * associated score attached to it. Each state has a score attached to it that
 * resembles the importance of it. The higher the score the better.
 * 
 * @author P H - 16.07.2017
 *
 */
public abstract class GameState {

	// States that can be used by other actions for generating their individual
	// score:
	public static final GameState Expansion_Focused = new GameStateFocused_Expansion();
	public static final GameState Technology_Focused = new GameStateFocused_Technology();
	public static final GameState Upgrade_Focused = new GameStateFocused_Upgrade();

	public static final GameState Refinery_Units = new GameStateFocused_Refinery();
	public static final GameState Building_Units = new GameStateUnits_Building();
	public static final GameState Worker_Units = new GameStateUnits_Worker();
	public static final GameState Combat_Units = new GameStateUnits_Combat();

	public static final GameState Cheap_Units = new GameStateUnits_Cheap();
	public static final GameState Expensive_Units = new GameStateUnits_Expensive();
	public static final GameState Mineral_Units = new GameStateUnits_Mineral();
	public static final GameState Gas_Units = new GameStateUnits_Gas();

	public static final GameState Flying_Units = new GameStateUnits_Flying();
	public static final GameState Bio_Units = new GameStateUnits_Bio();
	public static final GameState Support_Units = new GameStateUnits_Support();
	public static final GameState Healer_Units = new GameStateUnits_Healer();

	// The current score this state holds.
	private double currentScore = 0.;

	GameState() {

	}

	// -------------------- Functions

	/**
	 * Function for updating the current multiplier of the GameState.
	 *
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 */
	public void updateScore(ScoringDirector scoringDirector, BuildActionManager manager) {
		this.currentScore = this.generateScore(scoringDirector, manager);
	}

	/**
	 * Function for generating a new score for the GameState that represents the
	 * state of the game in the are that the GameState is responsible for. This
	 * score (For simplicity) should be between 0 and 1.
	 * 
	 * @param scoringDirector
	 *            the ScoringDirector that is going to be used for all missing
	 *            values and information regarding the various influences the
	 *            Bot is experiencing.
	 * @param manager
	 *            the BuildActionManager that contains all important
	 *            information.
	 * @return a score based on the provided information and the area the
	 *         GameState is performing in.
	 */
	protected abstract double generateScore(ScoringDirector scoringDirector, BuildActionManager manager);

	// ------------------------------ Getter / Setter

	public double getCurrentScore() {
		return currentScore;
	}

}
