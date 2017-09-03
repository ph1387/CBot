package buildingOrderModule.scoringDirector;

import buildingOrderModule.buildActionManagers.BuildActionManager;
import buildingOrderModule.scoringDirector.freeTrainingFacilities.FreeTrainingFacilities_Center;
import buildingOrderModule.scoringDirector.freeTrainingFacilities.FreeTrainingFacilities_TerranBarracks;
import buildingOrderModule.scoringDirector.freeTrainingFacilities.FreeTrainingFacilities_TerranCommandCenter;
import buildingOrderModule.scoringDirector.freeTrainingFacilities.FreeTrainingFacilities_TerranFactory;
import buildingOrderModule.scoringDirector.freeTrainingFacilities.FreeTrainingFacilities_TerranStarport;

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

	// The GameStates that rely on idling training facilities:
	public static final GameState FreeTrainingFacility_Center = new FreeTrainingFacilities_Center();
	public static final GameState FreeTrainingFacility_Terran_Barracks = new FreeTrainingFacilities_TerranBarracks();
	public static final GameState FreeTrainingFacility_Terran_CommandCenter = new FreeTrainingFacilities_TerranCommandCenter();
	public static final GameState FreeTrainingFacility_Terran_Factory = new FreeTrainingFacilities_TerranFactory();
	public static final GameState FreeTrainingFacility_Terran_Starport = new FreeTrainingFacilities_TerranStarport();

	// The current score this state holds.
	private double currentScore = 0.;
	// The number of times the state is going to be divided.
	private int currentDivider = 1;

	public GameState() {

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
		this.currentDivider = this.defineDivider();
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

	/**
	 * Function for defining the divider which will be added together with the
	 * other GameStates and used in generating the total score
	 * ({@link ScoringDirector#updateScoringActionScores}). The returned value
	 * defines the number of influence points the GameState has. This is usually
	 * 1, since most GameStates are only influenced by a single component.
	 * <b>BUT</b> some GameStates ( I.e.: The number of free and therefore
	 * idling training facilities) require the GameState to account for the
	 * extra components (In this example the number of facilities). This is due
	 * to the multiplier in most cases not being allowed to be larger than 1.0.
	 * Only accounting for 1.0 a single time would yield no appropriate result
	 * as well as n (N = The number of idling facilities) would result in
	 * gigantic multiplier (Namely: N).</br>
	 * Using this method the multiplier converges to 1.0 but should not
	 * overshoot it.
	 * 
	 * @return the divider that will be used for dividing the score of the
	 *         state.
	 */
	protected int defineDivider() {
		return 1;
	}

	// ------------------------------ Getter / Setter

	public double getCurrentScore() {
		return currentScore;
	}

	public int getCurrentDivider() {
		return currentDivider;
	}

}
